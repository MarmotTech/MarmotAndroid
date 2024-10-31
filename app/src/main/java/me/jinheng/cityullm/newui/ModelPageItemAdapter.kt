package me.jinheng.cityullm.newui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import me.jinheng.cityullm.R

class ModelPageItemAdapter(context: Context, _isModels: Boolean) : BaseAdapter(){
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    val isModels = _isModels
    override fun getCount(): Int {
        return if(isModels) CustomApi.models.size else CustomApi.benchmarkTasks.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItem(p0: Int): Any {
        return if (isModels) CustomApi.models[p0] else CustomApi.benchmarkTasks[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, contentView: View?, p2: ViewGroup?): View? {
        var content = contentView
        val holder: ViewHolder
        if (!isModels){
            val currentItem = CustomApi.benchmarkTasks[position]
            if (contentView == null) {
                holder = ViewHolder()
                content = inflater.inflate(R.layout.custom_model_list_item, null)
                holder.text = content.findViewById(R.id.item_text)
                holder.checkBox = content.findViewById(R.id.item_checkbox)
                content.tag = holder
            } else {
                holder = contentView.tag as ViewHolder
            }
            holder.text!!.text = "\n\t\tTask name: ${currentItem.taskName}\n\n\t\tTask value: ${currentItem.taskValue}\n"
            holder.checkBox!!.setOnCheckedChangeListener { _, checked ->
                run {
                    CustomApi.benchmarkTasks[position].enable = checked
                }
            }
            return content
        }

        val currentItem = CustomApi.models[position]
        if (contentView == null) {
            holder = ViewHolder()
            content = inflater.inflate(R.layout.custom_model_list_item, null)
            holder.text = content.findViewById(R.id.item_text)
            holder.checkBox = content.findViewById(R.id.item_checkbox)
            content.tag = holder
        } else {
            holder = contentView.tag as ViewHolder
        }
        holder.text!!.text = "\n\t\tModel name: ${currentItem.modelName}\n\n\t\tModel size: ${currentItem.modelSize}\n"
        holder.checkBox!!.setOnCheckedChangeListener { _, checked ->
            run {
                CustomApi.models[position].enablePrefetch = checked
            }
        }
        return content
    }

    class ViewHolder {
        var text: TextView? = null
        var checkBox: CheckBox? = null
        var prefetch: Boolean = false
    }
}