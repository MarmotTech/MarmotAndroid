package me.jinheng.cityullm.models

class ModelInfo {
    var enablePrefetch: Boolean = false

    var modelName: String

    var modelUrl: String

    var modelLocalPath: String

    var modelSize: Long

    var kvSize: Long

    var prefetchSize: Long

    var systemPrompt: String

    var tasks: ArrayList<String>?

    constructor(
        modelName: String,
        modelUrl: String,
        modelLocalPath: String,
        modelSize: Long,
        kvSize: Long,
        prefetchSize: Long,
        systemPrompt: String,
        tasks: ArrayList<String>?
    ) {
        this.modelName = modelName
        this.modelUrl = modelUrl
        this.modelLocalPath = modelLocalPath
        this.modelSize = modelSize
        this.kvSize = kvSize
        this.prefetchSize = prefetchSize
        this.systemPrompt = systemPrompt
        this.tasks = tasks
    }
}
