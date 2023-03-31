package com.example.mytodolist.entity

//类别和内容
data class Task(var content:String, var taskTime:String, var state:String,
                var category:String, var priority:Int )