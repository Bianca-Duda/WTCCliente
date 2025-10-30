package br.com.fiap.wtcclienteapp

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class Notice(
    val title: String,
    val content: String,
    val date: String? = null,
    val recipientClientIds: List<String> = emptyList(),
    val recipientGroupIds: List<String> = emptyList()
)

object NoticeBus {
    private val _events = MutableSharedFlow<Notice>(extraBufferCapacity = 8)
    val events = _events.asSharedFlow()

    fun send(notice: Notice) {
        _events.tryEmit(notice)
    }
}
