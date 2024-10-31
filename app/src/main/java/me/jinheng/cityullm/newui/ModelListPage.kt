package me.jinheng.cityullm.newui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import me.jinheng.cityullm.models.BenchmarkTask
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import me.jinheng.cityullm.R

class ModelListPage : AppCompatActivity() {
    var is4Chat: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_model_list_page)
        CustomApi.setFullscreen(this@ModelListPage)
        is4Chat = intent.getBooleanExtra("Chat", true)
        if(is4Chat){
            findViewById<LinearLayout>(R.id.model_list_layout_4chat).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.model_list_layout_4benchmark).visibility = View.GONE
            val listView:ListView = findViewById(R.id.model_list_page_listview_models_4chat)
            listView.adapter = ModelPageItemAdapter(this@ModelListPage, true)
            listView.onItemClickListener =
                AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, i: Int, _: Long ->
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
            findViewById<LinearLayout>(R.id.model_list_layout_4chat).visibility = View.GONE
            findViewById<LinearLayout>(R.id.model_list_layout_4benchmark).visibility = View.VISIBLE
            findViewById<TextView>(R.id.model_list_page_listview_Cancel_4benchmark).setOnClickListener {
                //TODO Benchmark
            }
            findViewById<TextView>(R.id.model_list_page_listview_Cancel_4benchmark).setOnClickListener {
                finish()
            }
            val jsar:JSONArray = JSON.parseArray(CustomApi.benchmarkTasksJson)
            for (js in jsar){
                val data:JSONObject = JSON.parseObject(js.toString())
                CustomApi.benchmarkTasks.add(
                    BenchmarkTask(data.getString("name"),
                    data.getString("value"))
                )
            }
            val listView:ListView = findViewById(R.id.model_list_page_listview_models_4benchmark)
            listView.adapter = ModelPageItemAdapter(this@ModelListPage, true)

            val taskList:ListView = findViewById(R.id.model_list_page_listview_tasks_4benchmark)
            taskList.adapter = ModelPageItemAdapter(this@ModelListPage, false)
        }
    }
}