package com.marmot.marmotapp.models

enum class ChatItemType {
    UserMessage,
    BotMessage
}

class ChatItem(
    val type: ChatItemType,
    val text: String
) {
    fun appendText(text: String): ChatItem {
        return ChatItem(
            type = type,
            text = this.text + text
        )
    }
}
