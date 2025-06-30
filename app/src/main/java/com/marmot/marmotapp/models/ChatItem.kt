package com.marmot.marmotapp.models

import com.google.gson.annotations.SerializedName

enum class ChatItemType {
    @SerializedName("user")
    UserMessage,
    
    @SerializedName("assistant")
    AssistantMessage
}

data class ChatItem(
    @SerializedName("role")
    val role: ChatItemType,
    
    @SerializedName("content")
    val content: String
) {
    fun appendText(text: String): ChatItem {
        return ChatItem(
            role = role,
            content = this.content + text
        )
    }
}
