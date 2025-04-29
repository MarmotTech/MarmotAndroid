package com.marmot.marmotapp.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapConverter {
    companion object{
        @JvmStatic
        @TypeConverter
        fun fromString(value: String): Map<String, Float> {
            val mapType = object : TypeToken<Map<String, Float>>() {}.type
            return Gson().fromJson(value, mapType)
        }

        @TypeConverter
        @JvmStatic
        fun fromStringMap(map: Map<String, Float>): String {
            val gson = Gson()
            return gson.toJson(map)
        }
    }
}
