package com.example.mytodolist.ui.task

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.ViewModel
import com.example.mytodolist.entity.Task
import java.util.ArrayList

object TaskViewModel:ViewModel() {

     //待完成的箭头
     var arrow1 = "up"

     //已完成&已放弃的箭头
     var arrow2 = "up"

     var time = "今天"

     //任务的优先级,1,2,3,4
     var task_priority = 4

     //任务的分类
     var task_category = "无分组"//默认

     //待完成的任务数量
     var task_wait_num = 0

     //已完成&放弃的任务数量
     var task_done_num = 0

     //数据库
     internal var db: SQLiteDatabase? = null

     //查询的数据库结果
     var cursor: Cursor? = null

     val taskList = ArrayList<Task>()
     val taskDoneList = ArrayList<Task>()
}