package me.jinheng.cityullm.models

object Config {
    var threadNum: Int = Constants.DEFAULT_THREAD_NUM

    var benchmarkPromptLength: Int = Constants.DEFAULT_BENCHMARK_PROMPT_LENGTH

    var benchmarkGenerationSize: Int = Constants.DEFAULT_BENCHMARK_GENERATION_SIZE

    var CPUGPU: String = Constants.DEFAULT_DEVICE

    var maxMemorySize: Int = Constants.DEFAULT_MAX_MEMORY_SIZE

    var useMMap: Boolean = true

    var prefechSize: Int = 1

    var lockSize: Float = 0.8f

    var basePath: String? = null

    var cppPath: String? = null

    var modelPath: String? = null

    var historyPath: String? = null

    var dataPath: String? = null
}
