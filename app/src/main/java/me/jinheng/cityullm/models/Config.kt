package me.jinheng.cityullm.models

object Config {
    var threadNum: Int = Constants.DEFAULT_THREAD_NUM

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
