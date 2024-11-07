package me.jinheng.cityullm

import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.navigation.ui.AppBarConfiguration
import androidx.appcompat.app.AppCompatActivity
import me.jinheng.cityullm.newui.CustomChat
import me.jinheng.cityullm.databinding.CustomActivityMainBinding
import me.jinheng.cityullm.models.Config
import me.jinheng.cityullm.models.LLama
import me.jinheng.cityullm.models.ModelOperation
import me.jinheng.cityullm.newui.CustomApi
import me.jinheng.cityullm.newui.ModelPageItemAdapter
import me.jinheng.cityullm.newui.ModelListPage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: CustomActivityMainBinding

    private fun copyFileFromAssets(assetManager: AssetManager, initialModelName: String, modelDir: String): Boolean {
        println("Copy: $initialModelName")
        return try {
            val inputStream = assetManager.open(initialModelName)
            val outFile = File(modelDir + initialModelName)
            val outputStream = FileOutputStream(outFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.flush()
            outputStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

//        binding.btmMainCustomBenchmark.setOnClickListener {
//            val it = Intent(this@MainActivity, ModelListPage::class.java)
//            it.putExtra("Chat", false)
//            startActivity(it)
//        }
//        binding.btmMainCustomChat.setOnClickListener{
//            startActivity(Intent(this@MainActivity, ModelListPage::class.java))
////             showModels()
//        }
        LLama.initFolder(getExternalFilesDir(null))
        CustomApi.LoadingDialogUtils.show(this, "LOADING")

        try {
            Thread{
                val initialModelName = "ggml-model-tinyllama-1.1b-chat-v1.0-q4_0.gguf"
                val modelInfoName = "models.json"
                val assetManager = assets
                val file = assetManager.list("")
                if (file?.contains(initialModelName) == true) {
                    copyFileFromAssets(assetManager, initialModelName, Config.modelPath)
                }
                if (file?.contains(modelInfoName) == true) {
                    copyFileFromAssets(assetManager, modelInfoName, Config.modelPath)
                }
                ModelOperation.updateModels()
                CustomApi.models = ModelOperation.getAllSupportModels()
                CustomApi.LoadingDialogUtils.dismiss()
                if (LLama.hasNoInitialModel()) {
                    runOnUiThread {
                        showDownloadDialog()
                    }
                }
            }.start()
            // showModels()
        } catch (e: IOException) {
            e.printStackTrace();
        }

        setContent {
            WelcomeScreen()
        }
    }

    private fun showModels() {
        val b = AlertDialog.Builder(this)
        val view = View.inflate(this, R.layout.custom_layout_model_list, null)
        view.findViewById<TextView>(R.id.model_list_text).text = "Model List"
        var modelListview = view.findViewById<ListView>(R.id.listview_models)
        var models = ModelOperation.getAllSupportModels()
        if (models.isNotEmpty()) {
//            val mainInfo = arrayOfNulls<String>(models.size)
//            for (i in 0 until models.size) {
//                mainInfo[i] = "\t\tModel name: ${models[i].modelName}\n\t\tModel size:${models[i].modelSize}\n\n"
//            }
            // modelListview.adapter = ArrayAdapter(this@MainActivity, R.layout.custom_listview_item, mainInfo)
            modelListview.adapter = ModelPageItemAdapter(this@MainActivity, true)
            modelListview.onItemClickListener =
                AdapterView.OnItemClickListener { _: AdapterView<*>?, view1: View?, i: Int, _: Long ->
                    val it = Intent(
                        this@MainActivity,
                        CustomChat::class.java
                    )
                    val tag = view1!!.tag as ModelPageItemAdapter.ViewHolder
                    it.putExtra("Selected", i)
                    it.putExtra("Prefetch", tag.prefetch)
                    println("Prefetch: ${tag.prefetch}")
                    this@MainActivity.startActivity(it)
                }
        }
        b.setView(view)
        b.show()
    }

    private fun showDownloadDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null)
        val progressTextView: TextView = dialogView.findViewById(R.id.textViewProgressInDialog)

        // Create and show the AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setTitle("Download Model")
        builder.setMessage("Could not find model in local, download tinyllama-1.1b-chat-v1.0 and have a try？")
        builder.setPositiveButton("Download", null) // 先不设置监听器
        builder.setNegativeButton("Cancel", null) // 同样先不设置监听器
        val progressDialog: AlertDialog = builder.create()
        progressDialog.setCancelable(false); // 设置对话框不可通过点击外部取消，只能通过按钮
        progressDialog.show()

        // 设置按钮的点击事件，这样可以确保对话框已经显示
        progressDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            progressTextView.text = "Starting download..."
            ModelOperation.downloadModelAsync("tinyllama-1.1b-chat-v1.0", progressTextView) {
                progressDialog.dismiss()
            }
        }
        progressDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            progressDialog.dismiss()
        }
    }
}