package com.marmot.marmotapp.models

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.UUID

class ChatHistory private constructor(
    val id: String,
    val modelName: String,
    private val context: Context
) {
    private val _history = mutableStateListOf<ChatItem>()
    val history: SnapshotStateList<ChatItem> = _history

    init {
        loadFromFile()
    }

    fun addItem(item: ChatItem) {
        _history.add(item)
        saveToFile()
        updateSharedPrefs()
    }
    
    fun updateLastItem(item: ChatItem) {
        if (_history.isNotEmpty()) {
            _history[_history.size - 1] = item
            saveToFile()
            updateSharedPrefs()
        }
    }
    
    fun clear() {
        _history.clear()
        saveToFile()
        updateSharedPrefs()
    }

    fun getHistoryPath(): String {
        val directory = File(context.filesDir, "chat_histories")

        return File(directory, "$id.json").absolutePath
    }
    
    private fun getHistoryFile(): File {
        val directory = File(context.filesDir, "chat_histories")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File(directory, "$id.json")
    }
    
    private fun loadFromFile() {
        val file = getHistoryFile()
        if (file.exists()) {
            try {
                val json = file.readText()
                val type = object : TypeToken<List<ChatItem>>() {}.type
                val loadedHistory = Gson().fromJson<List<ChatItem>>(json, type)
                _history.clear()
                _history.addAll(loadedHistory)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun saveToFile() {
        try {
            val json = Gson().toJson(_history)
            getHistoryFile().writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun updateSharedPrefs() {
        val lastMessage = if (_history.isNotEmpty()) {
            val lastItem = _history.last()
            val prefix = if (lastItem.role == ChatItemType.UserMessage) "You: " else "Assistant: "
            val content = lastItem.content.take(50) + if (lastItem.content.length > 50) "..." else ""
            "$prefix$content"
        } else {
            ""
        }
        
        getSharedPrefs(context).edit().apply {
            putString(PREF_CHAT_LAST_MESSAGE_PREFIX + id, lastMessage)
            putString(PREF_CHAT_MODEL_NAME_PREFIX + id, modelName)
            apply()
        }
    }
    
    companion object {
        private const val PREF_NAME = "chat_history_prefs"
        private const val PREF_CHAT_IDS = "chat_ids"
        private const val PREF_CHAT_LAST_MESSAGE_PREFIX = "chat_last_message_"
        private const val PREF_CHAT_MODEL_NAME_PREFIX = "chat_model_name_"
        
        fun create(context: Context, modelName: String): ChatHistory {
            val id = UUID.randomUUID().toString()
            val chatHistory = ChatHistory(id, modelName, context)
            
            val chatIds = getStoredChatIds(context).toMutableSet()
            chatIds.add(id)
            getSharedPrefs(context).edit().apply {
                putStringSet(PREF_CHAT_IDS, chatIds)
                putString(PREF_CHAT_MODEL_NAME_PREFIX + id, modelName)
                apply()
            }
            
            return chatHistory
        }
        
        fun getById(context: Context, id: String): ChatHistory? {
            val chatIds = getStoredChatIds(context)
            return if (chatIds.contains(id)) {
                val prefs = getSharedPrefs(context)
                val modelName = prefs.getString(PREF_CHAT_MODEL_NAME_PREFIX + id, "") ?: ""
                ChatHistory(id, modelName, context)
            } else {
                null
            }
        }
        
        fun getAllChatSummaries(context: Context): List<ChatSummary> {
            val chatIds = getStoredChatIds(context)
            val prefs = getSharedPrefs(context)
            
            return chatIds.map { id ->
                val lastMessage = prefs.getString(PREF_CHAT_LAST_MESSAGE_PREFIX + id, "") ?: ""
                val modelName = prefs.getString(PREF_CHAT_MODEL_NAME_PREFIX + id, "") ?: ""
                ChatSummary(id, lastMessage, modelName)
            }
        }
        
        private fun getStoredChatIds(context: Context): Set<String> {
            return getSharedPrefs(context).getStringSet(PREF_CHAT_IDS, emptySet()) ?: emptySet()
        }
        
        private fun getSharedPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }
}

data class ChatSummary(
    val id: String,
    val lastMessage: String,
    val modelName: String
)
