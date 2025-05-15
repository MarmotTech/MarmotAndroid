package com.marmot.marmotapp.models

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import com.marmot.marmotapp.utils.NativeMessageReceiver

object Llama {
    var id: Long = 0L

    var answering: Boolean = false

    var answerState: AnswerState = AnswerState.NO_MESSAGE_NEED_REPLY

    var msg: NativeMessageReceiver = NativeMessageReceiver()

    var input: String? = null

    var curThread: Thread? = null

    init {
        System.loadLibrary("llama-jni")
    }

    private external fun inputString(s: String?)

    private external fun startChat(
            msg: NativeMessageReceiver?,
            localModelPath: String?,
            systemPrompt: String?,
            threadNum: Int
    )

    private external fun benchmark(
        msg: NativeMessageReceiver?,
        localModelPath: Array<String>,
        threadNum: Int,
        promptLength: Int,
        generationSize: Int,
        tasks: Array<String>
    )

    private external fun startChatWPrefetch(
            msg: NativeMessageReceiver,
            localModelPath: String,
            systemPrompt: String,
            threadNum: Int,
            prefetchThreadNum: Int,
            lSize: Float,
            contextSize: Int
    )

    private external fun stop()

    private external fun kill()

    fun hasInitialModels():Boolean {
        val modelFolder = File(Config.modelPath!!)
        for (file in modelFolder.listFiles()!!) {
            if (file.isFile && file.name.endsWith(".gguf")) {
                Log.d("MRM", "Found initial model: ${file.name}")
                return false
            }
        }
        Log.d("MRM", "No initial model found")
        return true
    }

    fun initFolder(externalDir: File?) {
        val llamaFolder = File(externalDir, "llama")
        Config.basePath = llamaFolder.absolutePath + "/"

        val cppFolder = File(llamaFolder, "main")
        Config.cppPath = cppFolder.absolutePath + "/"
        if (!cppFolder.exists()) {
            cppFolder.mkdirs()
        }

        val modelFolder = File(llamaFolder, "models")
        Config.modelPath = modelFolder.absolutePath + "/"
        if (!modelFolder.exists()) {
            modelFolder.mkdirs()
        }

        val historyFolder = File(llamaFolder, "history")
        Config.historyPath = historyFolder.absolutePath + "/"
        if (!historyFolder.exists()) {
            historyFolder.mkdirs()
        }

        val dataFolder = File(llamaFolder, "data")
        Config.dataPath = dataFolder.absolutePath + "/"
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }
        Log.d("MRM", "Initialize cpp path: ${Config.cppPath}")
        Log.d("MRM", "Initialize model path: ${Config.modelPath}")
        Log.d("MRM", "Initialize history path: ${Config.historyPath}")
        Log.d("MRM", "Initialize data path: ${Config.dataPath}")
    }

    fun startBenchmark(
        models: Array<String>,
        tasks: Array<String>,
        onFinished: (List<BenchmarkResult>) -> Unit
    ) {
        val taskSet = setOf(tasks)
        for (task in taskSet) {
            Log.d("MRM", "Start benchmark task: $task")
        }
        curThread = Thread {
            msg.reset()

            benchmark(
                msg,
                models,
                Config.threadNum,
                Config.benchmarkPromptLength,
                Config.benchmarkGenerationSize,
                tasks
            )
            val s: String = msg.waitForString()!!

            val itemType = object : TypeToken<List<BenchmarkResult>>() {}.type
            val results = Gson().fromJson<List<BenchmarkResult>>(s, itemType)

            onFinished(results)
        }
        curThread!!.start()
    }

    fun init(modelInfo: ModelInfo, enablePrefetch: Boolean, listener: ChatListener) {
        Log.d("MRM", "Start to chat with [${modelInfo.modelName}]")
        if (enablePrefetch) {
            Log.d("MRM", "Enable prefetching")
            startChatWPrefetch(
                msg,
                Config.modelPath + modelInfo.modelLocalPath,
                modelInfo.systemPrompt,
                Config.threadNum,
                1,  // number of threads for prefetching
                8F, // available memory size
                512 // context size
            )
        } else {
            Log.d("MRM", "Disable prefetching")
            startChat(
                msg,
                Config.modelPath + modelInfo.modelLocalPath,
                modelInfo.systemPrompt,
                Config.threadNum
            )
        }

        curThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                msg.reset()
                val s: String = msg.waitForString()!!

                if (answerState == AnswerState.NO_MESSAGE_NEED_REPLY) {
                    //
                } else if (answerState == AnswerState.MESSAGE_NEED_REPLY) {
                    answerState = AnswerState.ANSWERING
                    listener.onBotContinue(s)
                } else {
                    if (msg.isStart) {
                        listener.onUpdateInfo(s)
                        answerState = AnswerState.NO_MESSAGE_NEED_REPLY
                    } else {
                        listener.onBotContinue(s)
                    }
                }
            }
        }
        curThread!!.start()
    }

    @Throws(RuntimeException::class)
    fun run(input_: String?) {
        input = input_
        inputString(input)
        answerState = AnswerState.MESSAGE_NEED_REPLY
    }

    fun destroy() {
        Log.d("MRM", "Destroy LLama")
        stop()
        curThread!!.interrupt()
    }

    fun clear() {
        //        messageAdapter.clear();
    }

    interface ChatListener {
        fun onUpdateInfo(s: String)
        fun onBotContinue(s: String)
    }
}
