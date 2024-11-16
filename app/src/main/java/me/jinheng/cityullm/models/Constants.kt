package me.jinheng.cityullm.models

object Constants {
    const val LOCAL_PATH: String = "/data/local/tmp/llama.cpp/models/"

    const val MAX_INIT_HISTORY_ITEM: Int = 5

    const val DEFAULT_THREAD_NUM: Int = 2

    const val DEFAULT_BENCHMARK_PROMPT_LENGTH: Int = 512

    const val DEFAULT_BENCHMARK_GENERATION_SIZE: Int = 512

    const val DEFAULT_DEVICE: String = "CPU"

    const val DEFAULT_MAX_MEMORY_SIZE: Int = 4

    const val GB: Long = (1024 * 1024 * 1024).toLong()
}
