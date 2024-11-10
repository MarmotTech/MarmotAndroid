package me.jinheng.cityullm.models

class HistoryItem {
    var id: Long? = null
    var send: String? = null
    var receive: String? = null

    constructor()

    constructor(id: Long?, send: String?, receive: String?) {
        this.id = id
        this.send = send
        this.receive = receive
    }
}
