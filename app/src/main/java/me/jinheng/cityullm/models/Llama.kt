package me.jinheng.cityullm.models

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.math.min
import me.jinheng.cityullm.utils.NativeMessageReceiver
import me.jinheng.cityullm.utils.getTotalMemory

object LLama {
    var id: Long = 0L

    var answering: Boolean = false

    var answerState: AnswerState = AnswerState.NO_MESSAGE_NEED_REPLY

    var msg: NativeMessageReceiver = NativeMessageReceiver()

    var input: String? = null

    var curThread: Thread? = null

    init {
        System.loadLibrary("llama-jni")
    }

    fun walkFolder(folderPath: String) {
        val folder = File(folderPath)
        val files = folder.listFiles()
        for (file in files!!) {
            if (file.isDirectory) {
                Log.d("debug", "Directory: " + file.absolutePath)
                walkFolder(file.absolutePath)
            } else if (file.isFile) {
                Log.d("debug", "File: " + file.absolutePath + ", size " + file.length())
            }
        }
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
        Log.d("MRM", "modelPath: ${modelFolder.absolutePath}")
        Config.historyPath = historyFolder.absolutePath + "/"
        if (!historyFolder.exists()) {
            historyFolder.mkdirs()
        }

        val dataFolder = File(llamaFolder, "data")
        Config.dataPath = dataFolder.absolutePath + "/"
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }
    }

    fun hasNoInitialModel(): Boolean {
        val modelFolder = File(Config.modelPath)
        if (modelFolder.exists()) {
            val files = modelFolder.listFiles()
            for (f in files!!) {
                if (f.name.endsWith(".gguf")) {
                    Log.d("debug", "Find initial model " + f.absolutePath)
                    return false
                }
            }
        } else {
            Log.d("debug", modelFolder.absolutePath + " does not exist")
        }
        return true
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

    fun startBenchmark(
        models: Array<String>,
        tasks: Array<String>,
        onFinished: (List<BenchmarkResult>) -> Unit
    ) {
        curThread = Thread {
            msg.reset()

            println("started")

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
        val totalMemory = (getTotalMemory() / Constants.GB).toFloat()
        val canUseMemory = min(totalMemory.toDouble(), Config.maxMemorySize.toDouble()).toFloat()
        val modelSize = modelInfo.modelSize.toFloat() / Constants.GB

        var prefetchSizeInGB = 0f
        var kvCacheSizeInGB = 0f
        val memSize = 0f

        if (canUseMemory <= modelSize) {
            prefetchSizeInGB = modelInfo.prefetchSize.toFloat() / Constants.GB
            kvCacheSizeInGB = modelInfo.kvSize.toFloat() / Constants.GB
        }

        println(
                """
                INIT: ${modelInfo.modelName}
                path: ${modelInfo.modelLocalPath}
                prefetch: $enablePrefetch
                """.trimIndent()
        )
        if (true) {
            println("Start to chat with prefetch")
            startChatWPrefetch(
                msg,
                Config.modelPath + modelInfo.modelLocalPath,
                "You are a helpful assistant, please answer my question",
                Config.threadNum,
                1,
                8F,
                512
            )
        } else {
            println("Start to chat without prefetch")
            startChat(msg, Config.modelPath + modelInfo.modelLocalPath, modelInfo.systemPrompt, Config.threadNum)
        }
        println("Start thread to receive new strings")
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
        kill()
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
