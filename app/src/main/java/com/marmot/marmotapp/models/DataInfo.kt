package com.marmot.marmotapp.models;

class DataInfo {
    var dataName: String
    var dataLocalPath: String
    var dataUrl: String

    constructor(dataName: String, dataLocalPath: String, dataUrl: String) {
        this.dataName = dataName
        this.dataLocalPath = dataLocalPath
        this.dataUrl = dataUrl
    }
}
