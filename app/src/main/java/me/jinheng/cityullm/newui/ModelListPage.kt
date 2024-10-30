package me.jinheng.cityullm.newui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import me.jinheng.cityullm.R

class ModelListPage : AppCompatActivity() {
    var is4Chat: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_model_list_page)
        CustomApi.setFullscreen(this@ModelListPage)
        var listView:ListView = findViewById(R.id.model_list_page_listview)
        listView.adapter = ModelItemAdapter(this@ModelListPage)
        if(is4Chat){
            listView.onItemClickListener =
                AdapterView.OnItemClickListener { _: AdapterView<*>?, view1: View?, i: Int, _: Long ->
                    val it = Intent(
                        this@ModelListPage,
                        CustomChat::class.java
                    )
                    it.putExtra("Selected", i)
                    it.putExtra("Prefetch", CustomApi.models[i].enablePrefetch)
                    println("Prefetch: ${CustomApi.models[i].enablePrefetch}")
                    this@ModelListPage.startActivity(it)
                }
        }else{ // for benchmark
            //TODO
        }
    }
}