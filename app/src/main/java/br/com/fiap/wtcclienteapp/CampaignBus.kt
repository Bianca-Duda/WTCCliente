package br.com.fiap.wtcclienteapp

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class Campaign(val title: String, val content: String)

object CampaignBus {
    private val _events = MutableSharedFlow<Campaign>(extraBufferCapacity = 8)
    val events = _events.asSharedFlow()

    fun send(campaign: Campaign) {
        _events.tryEmit(campaign)
    }
}


