package com.example.mytodolist.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MyDatabaseHelper(val context: Context?, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    //如果数据库中不存在该表就创建一个任务的数据表
    private val createTask = "create table if not exists Task (" +
            "id integer primary key autoincrement," +
            "taskDate text," +
            "taskContent text," +
            "taskState text," +
            "taskPriority Int," +
            "taskCategory text) "

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTask)
        Log.d("table", "数据表创建成功")
    }

    //更新数据库
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists Task")
        onCreate(db)
    }

}