package me.jinheng.cityullm.newui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import me.jinheng.cityullm.R
import me.jinheng.cityullm.models.LLama
import java.io.IOException

class CustomChat : AppCompatActivity() {
    private var dataloaded = false
    private var selectedNum: Int = -1
    private var isBotTalking: Boolean = false
    private var history: ArrayList<String>? = null
    private var currentBotChat: ChatItem? = null
    private var customChatListAdapter: CustomChatListAdapter? = null
    private var result: ListView? = null
    private var input: EditText? = null
    private var help: ImageView? = null
    private var goback: ImageView? = null
    private var start: ImageView? = null
    private var config: ImageView? = null
    private var delHistory: ImageView? = null
    private var mBackPressed: Long = 0
    private var info: TextView? = null
    private var textViewModelName: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_activity_chat)
        CustomApi.setFullscreen(this@CustomChat)
        CustomApi.chatItems = ArrayList()
        selectedNum = intent.getIntExtra("Selected", 0)
        history = ArrayList()
        result = findViewById(R.id.result)
        info = findViewById(R.id.chat_info)
        textViewModelName = findViewById(R.id.chat_model_name)
        customChatListAdapter = CustomChatListAdapter(this)
        result!!.adapter = customChatListAdapter
        input = findViewById(R.id.input)
        //help = findViewById(R.id.help)
        goback = findViewById(R.id.goback_page)
        start = findViewById(R.id.start)
        config = findViewById(R.id.config)
        delHistory = findViewById(R.id.del_history)
        delHistory!!.setOnClickListener {
            CustomApi.chatItems!!.clear()
            refreshListview()
            //LLama.clear()
        }
        //help!!.setOnClickListener {
            //showHelp()
        //}
        goback!!.setOnClickListener {
            LLama.destroy()
            finish()
        }
        config!!.setOnClickListener {
            showConfig()
        }
        start!!.setOnClickListener {
            val str:String = input!!.text.toString().trim()
            if(str.isNotEmpty()){
                println("INPUT STR: $str")
                userMsg(str)
            }
        }
        loadData()
        textViewModelName!!.text = CustomApi.models!![selectedNum].modelName
        botEnd()
    }

    private fun loadData() {
        try {
            var modelName = CustomApi.models!![selectedNum].modelName
            Log.d("debug", modelName)
            // ModelOperation.downloadModelAsync(modelName, null)
            CustomApi.LoadingDialogUtils.show(this@CustomChat, "Loading Model...")
            Thread{
                LLama.init(modelName, CustomApi.models!![selectedNum].enablePrefetch, this@CustomChat)
                CustomApi.LoadingDialogUtils.dismiss()
            }.start()
        } catch (e: IOException) {
            e.printStackTrace();
        }

    }

    private fun closeInputMethod() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            val v = window.peekDecorView()
            if (null != v) {
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }

    private fun refreshListview() {
        closeInputMethod()
        customChatListAdapter!!.notifyDataSetChanged()
        result!!.adapter = customChatListAdapter
    }

    private fun showHelp() {
        val b = AlertDialog.Builder(this)
        val view = View.inflate(this, R.layout.custom_layout_help, null)
        b.setView(view)
        b.setNegativeButton(
            "OK"
        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        b.show()
    }

    private fun buildPrompt(): String {
        val prompt = StringBuilder()
//        if (!history!!.isEmpty()) {
//            for (s in history!!) {
//                prompt.append(s)
//            }
//        }
        prompt.append("Q: ").append(input!!.text.toString()).append("\n\nA:")
        return prompt.toString()
    }

    private fun showConfig() {
        val builder = AlertDialog.Builder(this)
        val view = View.inflate(this, R.layout.custom_layout_config, null)
        // initConfigs(view);
        builder.setView(view)
        builder.setTitle("Setting")
        builder.setNegativeButton(
            "Cancel"
        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        builder.setPositiveButton(
            "OK"
        ) { dialog: DialogInterface?, which: Int -> }.show()
    }

    private fun stopPrinting() {
        LLama.stop()
    }

    fun updateInfo(msg:String){
        runOnUiThread {
            info!!.text = msg;
        }
    }

    override fun onBackPressed() {
        if (mBackPressed > System.currentTimeMillis() - 2000) {
            finish()
            super.onBackPressed()
        } else {
            CustomApi.showMsg(this, "Return twice to exit APP")
            mBackPressed = System.currentTimeMillis()
        }
    }

    private fun userMsg(msg: String){
        botEnd()
        input!!.setText("")
//        if (history!!.size >= CustomApi.max_history) {
//            history!!.removeAt(0)
//            history!!.removeAt(0)
//        }
//        history!!.add(("Q: " + msg.obj.toString()).toString() + "<|endoftext|>\n\n")
        val chatItem = ChatItem()
        chatItem.type = 1
        chatItem.text = msg
        CustomApi.chatItems!!.add(chatItem)
        refreshListview()
        LLama.run(msg)
        botBegin()
    }

    private fun botBegin(){
        isBotTalking = true;
        input!!.isClickable = false
        CustomApi.chatItems!!.add(currentBotChat!!);
        refreshListview();
    }

    fun botContinue(msg: String){
        runOnUiThread{
            currentBotChat!!.appendText(msg);
            refreshListview();
        }
    }

    fun botEnd() {
        runOnUiThread {
            isBotTalking = false
            // history!!.add("A: $msg<|endoftext|>\n\n")
            currentBotChat = ChatItem()
            currentBotChat!!.type = 0
            currentBotChat!!.text = "\t\t"
            input!!.isClickable = true
            refreshListview()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LLama.destroy()
        // exitProcess(0)
    }

}