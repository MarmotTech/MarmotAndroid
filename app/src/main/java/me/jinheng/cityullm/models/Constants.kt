package me.jinheng.cityullm.models

object Constants {
    val LOCAL_PATH: String = "/data/local/tmp/llama.cpp/models/"

    val MAX_INIT_HISTORY_ITEM: Int = 5

    val DEFAULT_THREAD_NUM: Int = 2

    val DEFAULT_DEVICE: String = "CPU"

    val DEFAULT_MAX_MEMORY_SIZE: Int = 4

    public val GB: Long = (1024 * 1024 * 1024).toLong()
}
