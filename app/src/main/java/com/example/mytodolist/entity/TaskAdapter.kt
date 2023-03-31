package com.example.mytodolist.entity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.R

class TaskAdapter(var taskList:List<Task>):
            RecyclerView.Adapter<TaskAdapter.ViewHolder>(){

    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(clickListener: OnItemClickListener?) {
        mOnItemClickListener = clickListener
    }

    inner class ViewHolder(view: View,onClickListener: OnItemClickListener)
        : RecyclerView.ViewHolder(view){

        val content: TextView = view.findViewById(R.id.taskContent) //任务内容缓存
        val taskTime: TextView = view.findViewById(R.id.taskTime) //任务时间缓存
        val state = view.findViewById<CheckBox>(R.id.taskState) //任务状态缓存
        val delete = view.findViewById<ImageButton>(R.id.TaskDelete) //任务删除缓存

        init {
            state.setOnClickListener {

                val position = adapterPosition
                //确保position值有效
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener.onButtonClicked(view, position)
                }

            }

            delete.setOnClickListener{
                val position = adapterPosition
                //确保position值有效
                if (position != RecyclerView.NO_POSITION) {
                    onClickListener.onDeleteBtnClicked(view, position)
                }
            }
        }

        /**
         * 此方法实现列表数据的绑定和item的展开/关闭
         */
        fun bindView(pos: Int, task: Task) {
            content.text = task.content
            taskTime.text = task.taskTime

            state.isChecked = task.state != "待完成"

        }

    }


    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Task>){
        this.taskList = list
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item,parent,false)

        val viewHolder = mOnItemClickListener?.let { ViewHolder(view, it) }

        return viewHolder!!
    }

    //用于给子项数据进行赋值
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]

        //缓存赋值
        holder.bindView(position,task)

    }

    override fun getItemCount() = taskList.size


}