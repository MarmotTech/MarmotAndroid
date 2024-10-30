package me.jinheng.cityullm.newui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import me.jinheng.cityullm.R
import me.jinheng.cityullm.models.ModelInfo
import me.jinheng.cityullm.models.ModelOperation

class ModelItemAdapter(context: Context) : BaseAdapter(){
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var models: MutableList<ModelInfo> = CustomApi.models
    override fun getCount(): Int {
        return models.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItem(p0: Int): Any {
        return models[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, contentView: View?, p2: ViewGroup?): View? {
        var content = contentView
        val holder: ViewHolder
        val currentItem = models[position]
        if (contentView == null) {
            holder = ViewHolder()
            content = inflater.inflate(R.layout.model_list_item, null)
            holder.text = content.findViewById(R.id.item_text)
            holder.checkBox = content.findViewById(R.id.item_checkbox)
            content.tag = holder
        } else {
            holder = contentView.tag as ViewHolder
        }
        holder.text!!.text = "\t\tModel name: ${currentItem.modelName}\n\t\tModel size:${currentItem.modelSize}\n\n"
        holder.checkBox!!.setOnCheckedChangeListener { _, checked ->
            run {
                models[position].enablePrefetch = checked
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