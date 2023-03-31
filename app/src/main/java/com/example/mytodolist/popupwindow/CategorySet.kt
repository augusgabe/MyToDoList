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
import com.example.mytodolist.ui.task.TaskViewModel


class CategorySet(val mainActivity: MainActivity) {

    //弹窗相关
    private var popupWindow: PopupWindow? = null

    //阈值设定界面
    @SuppressLint("InflateParams", "CutPasteId")
    fun start(){
        //创建弹出窗口视图，和阈值设置界面布局联系
        val view: View =
            LayoutInflater.from(mainActivity).inflate(R.layout.category_set, null)
        popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        //设置弹窗位置
        popupWindow?.showAtLocation(view, Gravity.BOTTOM, 5, 5)

        //返回按钮点击事件
        val cancel = view.findViewById<View>(R.id.cancel_button_c) as TextView
        cancel.setOnClickListener {
            //弹窗消失
            popupWindow?.dismiss()
            popupWindow = null
        }

        val sure = view.findViewById<View>(R.id.sure_button_c)
        sure.setOnClickListener {

            val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup_c)

            val selectedButton = radioGroup.checkedRadioButtonId

            val category = mainActivity.findViewById<TextView>(R.id.category_choose)

            when(selectedButton % 4 ){
                1->{

                    category.text = "无分组"
                    TaskViewModel.task_category = "无分组"
                }
                2->{
                    category.text = "学习"
                    TaskViewModel.task_category = "学习"
                }
                3->{
                        category.text = "生活"
                    TaskViewModel.task_category = "生活"
                }
                0->{
                        category.text = "工作"
                    TaskViewModel.task_category = "工作"
                }
            }

            //弹窗消失
            popupWindow?.dismiss()
            popupWindow = null
        }


    }
}