package com.marmot.marmotapp.models;

class DataInfo {
    var dataName: String            // Name of the data, e.g., wikitext2
    var dataLocalPath: String       // Local path of the data, e.g., data/wikitext2-test.txt
    var dataUrl: String             // URL to download the data, e.g., https://conference.cs.cityu.edu.hk/saccps/app/data/wikitext2-test.txt

    constructor(dataName: String, dataLocalPath: String, dataUrl: String) {
        this.dataName = dataName
        this.dataLocalPath = dataLocalPath
        this.dataUrl = dataUrl
    }
}
