package com.example.mytodolist.popupwindow

import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mytodolist.MainActivity
import com.example.mytodolist.R
import com.example.mytodolist.databinding.FragmentTaskBinding
import com.example.mytodolist.ui.task.TaskViewModel


class PrioritySet(val mainActivity: MainActivity,val taskBinding: FragmentTaskBinding) {

    //弹窗相关
    private var popupWindow: PopupWindow? = null

    //阈值设定界面
    @SuppressLint("InflateParams", "CutPasteId")
    fun start(){
        //创建弹出窗口视图，和阈值设置界面布局联系
        val view: View =
            LayoutInflater.from(mainActivity).inflate(R.layout.priority_set, null)
        popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        //设置弹窗位置
        popupWindow?.showAtLocation(view, Gravity.BOTTOM, 5, 5)

        //返回按钮点击事件
        val cancel = view.findViewById<View>(R.id.cancel_button) as TextView
        cancel.setOnClickListener {
            //弹窗消失
            popupWindow?.dismiss()
            popupWindow = null
        }

        val sure = view.findViewById<View>(R.id.sure_button)

        sure.setOnClickListener {

            val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

            val selectedButton = radioGroup.checkedRadioButtonId
            TaskViewModel.task_priority = (selectedButton % 4)

            Log.d("test","priority id:"+ selectedButton)

            when((selectedButton % 4)){//多次选择radiobutton后其id会再次增加4
                1->{
                   taskBinding.drawerItem.priorityImage.setImageResource(R.drawable.priority1)

                    Log.d("test","UI1更新")
                }
                2->{
                    taskBinding.drawerItem.priorityImage.setImageResource(R.drawable.priority_two)
                    Log.d("test","UI2更新")
                }
                3->{
                    taskBinding.drawerItem.priorityImage.setImageResource(R.drawable.priority3)
                }
                0->{
                    taskBinding.drawerItem.priorityImage.setImageResource(R.drawable.priority)
                }
            }

            //弹窗消失
            popupWindow?.dismiss()
            popupWindow = null
        }

    }
}