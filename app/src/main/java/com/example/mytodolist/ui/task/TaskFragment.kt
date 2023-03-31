package com.example.mytodolist.ui.task

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytodolist.MainActivity
import com.example.mytodolist.R
import com.example.mytodolist.databinding.FragmentTaskBinding
import com.example.mytodolist.entity.OnItemClickListener
import com.example.mytodolist.entity.Task
import com.example.mytodolist.entity.TaskAdapter
import com.example.mytodolist.entity.TaskDoneAdapter
import com.example.mytodolist.popupwindow.CategorySet
import com.example.mytodolist.popupwindow.PrioritySet
import java.text.SimpleDateFormat
import java.util.*


class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null

    private val binding get() = _binding!!

    //初始化两个列表的适配器
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskDoneAdapter: TaskDoneAdapter

    //选择日期Dialog
    private var datePickerDialog: DatePickerDialog? = null

    private val calendar: Calendar  = Calendar.getInstance()

    private lateinit var mainActivity :MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //初始化mainActivity，需要和 mainActivity交互
        mainActivity = activity as MainActivity

        //初始化任务栏
        initTasks()

        initOthers()
        return root
    }

    fun initOthers(){
        binding.drawerItem.calendarChoose.text = TaskViewModel.time

        binding.drawerItem.categoryChoose.text = TaskViewModel.task_category
    }

    //初始化
    @SuppressLint("Range")
    private fun initTasks(){

        val layoutManager= LinearLayoutManager(context)
        val layoutManager2= LinearLayoutManager(context)

        layoutManager.orientation=LinearLayoutManager.VERTICAL
        layoutManager.stackFromEnd = true;//列表再底部开始展示，反转后由上面开始展示
        layoutManager.reverseLayout = true;//列表翻转

        layoutManager2.orientation=LinearLayoutManager.VERTICAL
        layoutManager2.stackFromEnd = true;//列表再底部开始展示，反转后由上面开始展示
        layoutManager2.reverseLayout = true;//列表翻转

        binding.taskTodoRecyclerView.layoutManager = layoutManager
        binding.taskTodo2RecyclerView.layoutManager = layoutManager2

        //让recyclerView使用自定义的适配器，生成相应列表
        taskAdapter = TaskAdapter(TaskViewModel.taskList)
        taskDoneAdapter = TaskDoneAdapter(TaskViewModel.taskDoneList)

        //分类选择为‘今天’,默认展示
        categoryChoose("今天")

        //监听state
        taskAdapter.setOnItemClickListener(object : OnItemClickListener {

            @SuppressLint("Recycle")
            override fun onButtonClicked(view: View?, position: Int) {
                //已完成 & 已放弃任务列表增加 1
                val task_change = TaskViewModel.taskList[position]
                task_change.state = "Done"

                //添加
                TaskViewModel.taskDoneList.add(task_change)
                taskDoneAdapter.setList(TaskViewModel.taskDoneList)
                binding.taskTodo2RecyclerView.adapter = taskDoneAdapter

                //移除
                TaskViewModel.taskList.removeAt(position)
                taskAdapter.setList(TaskViewModel.taskList)
                binding.taskTodoRecyclerView.adapter = taskAdapter

                //待完成任务数量减少 1
                TaskViewModel.task_wait_num--
                binding.taskWaitNum.text = TaskViewModel.task_wait_num.toString()

                //已完成 & 已放弃任务数量增加 1
                TaskViewModel.task_done_num++
                binding.taskDoneNum.text = TaskViewModel.task_done_num.toString()

                //更新该任务的状态(数据库)
                updateTask(task_change,"Done")
            }

            override fun onDeleteBtnClicked(view: View?, position: Int) {
                val task_change = TaskViewModel.taskList[position]

                TaskViewModel.taskList.removeAt(position)
                taskAdapter.setList(TaskViewModel.taskList)
                binding.taskTodoRecyclerView.adapter = taskAdapter

                //待完成任务数量减少 1
                TaskViewModel.task_wait_num--
                binding.taskWaitNum.text = TaskViewModel.task_wait_num.toString()

                TaskViewModel.db?.delete("Task",
                    "taskDate = ? and taskCategory = ? and taskContent = ?",
                    arrayOf(task_change.taskTime,task_change.category,task_change.content))

            }

        })

        taskDoneAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onButtonClicked(view: View?, position: Int) {
                val task_change = TaskViewModel.taskDoneList[position]
                task_change.state = "待完成"

                updateTask(task_change,"待完成")

                //已完成 & 已放弃任务列表增加 1
                TaskViewModel.taskList.add(task_change)
                taskAdapter.setList(TaskViewModel.taskList)

                TaskViewModel.taskDoneList.removeAt(position)
                taskDoneAdapter.setList(TaskViewModel.taskDoneList)
                binding.taskTodo2RecyclerView.adapter = taskDoneAdapter

                //已完成任务数量减少 1
                TaskViewModel.task_done_num--
                binding.taskDoneNum.text = TaskViewModel.task_done_num.toString()
                binding.taskTodoRecyclerView.adapter = taskAdapter

                //待完成任务数量增加 1
                TaskViewModel.task_wait_num++
                binding.taskWaitNum.text = TaskViewModel.task_wait_num.toString()

            }

            override fun onDeleteBtnClicked(view: View?, position: Int) {
                val task_change = TaskViewModel.taskDoneList[position]

                TaskViewModel.taskDoneList.removeAt(position)
                taskDoneAdapter.setList(TaskViewModel.taskDoneList)
                binding.taskTodo2RecyclerView.adapter = taskDoneAdapter

                //待完成任务数量减少 1
                TaskViewModel.task_done_num--
                binding.taskDoneNum.text = TaskViewModel.task_done_num.toString()

                TaskViewModel.db?.delete("Task",
                    "taskDate = ? and taskCategory = ? and taskContent = ?",
                    arrayOf(task_change.taskTime,task_change.category,task_change.content))
            }

        })


    }

    @SuppressLint("Recycle", "Range")
    fun updateTask(task_change:Task, stateTo:String){

        //先查询该task的Id,然后依据获取到的id更新该任务的状态(数据库)
        val findId = "select * from  `Task` where taskDate = ${task_change.taskTime} and " +
                "taskCategory = ${task_change.category} and "+
                "taskPriority = ${task_change.priority} and "+
                "taskContent = ${task_change.content}"

        val currentCursor = TaskViewModel.db?.query("Task",null,
            "taskDate = ? and taskCategory = ? and taskContent = ?",
            arrayOf(task_change.taskTime,task_change.category,task_change.content),
            null,null,null)

        currentCursor?.let {
            it.moveToFirst()
            val tid = it.getInt(it.getColumnIndex("id"))
            val updateSql = "update `Task` set taskState = '${stateTo}' where id = $tid;"
            //更新数据库中该位置的数据
            TaskViewModel.db?.execSQL(updateSql)
        }

    }


    override fun onResume() {
        super.onResume()

        //两个任务卡片的内容视图
        val taskToDo1 = binding.taskTodoRecyclerView
        val taskToDo2 = binding.taskTodo2RecyclerView

        //调用点击监听
        binding.fab.setOnClickListener {
            val drawerHandle = binding.handle
            drawerHandle.performClick()//模拟把手被点击
        }

        //点击任务箭头展开/收起任务列表
        binding.arrowImage.setOnClickListener {

            val arrow = TaskViewModel.arrow1
                when(arrow){
                    //down代表点击后应该展开列表
                    "down"->{
                        taskToDo1.visibility = View.VISIBLE
                        TaskViewModel.arrow1 = "up"
                        binding.arrowImage.setImageResource(R.drawable.arrow_up)
                    }
                    //up代表点击后应该收起-列表
                    "up"->{
                         taskToDo1.visibility = View.GONE
                         TaskViewModel.arrow1 = "down"
                         binding.arrowImage.setImageResource(R.drawable.arrow_down)
                    }
                }

            //发生变化时的动画
            taskToDo1.itemAnimator?.changeDuration = 500
            taskToDo1.itemAnimator?.moveDuration = 500
        }

        //点击任务箭头展开/收起任务列表
        binding.arrowImage2.setOnClickListener {

            val arrow = TaskViewModel.arrow2
            when(arrow){
                //down代表点击后应该展开列表
                "down"->{
                    taskToDo2.visibility = View.VISIBLE
                    TaskViewModel.arrow2 = "up"
                    binding.arrowImage2.setImageResource(R.drawable.arrow_up)
                }
                //up代表点击后应该收起-列表
                "up"->{
                    taskToDo2.visibility = View.GONE
                    TaskViewModel.arrow2 = "down"
                    binding.arrowImage2.setImageResource(R.drawable.arrow_down)
                }
            }

            //发生变化时的动画
            taskToDo2.itemAnimator?.changeDuration = 500
            taskToDo2.itemAnimator?.moveDuration = 500
        }

        //日期选择监听
        binding.drawerItem.calendar.setOnClickListener {
            showDailog()
        }

        //优先级
        binding.drawerItem.priorityImage.setOnClickListener {
            Log.d("test","priority is clicked")
            val prioritySet = PrioritySet(mainActivity,binding)
            prioritySet.start()
            Log.d("test","执行完毕u")
        }

        //分类选择
        binding.drawerItem.category.setOnClickListener {
            val categorySet = CategorySet(mainActivity)
            categorySet.start()
        }

        //确定添加
        binding.drawerItem.sureButton.setOnClickListener {
            //获取任务内容
            val taskContent = binding.drawerItem.taskName.text.toString()

            //获取任务的时间
            val taskTime = binding.drawerItem.calendarChoose.text.toString()

            //获取任务的优先级
            val taskPriority = TaskViewModel.task_priority

            //获取任务的分类
            val taskCategory = TaskViewModel.task_category

            val values_task = ContentValues().apply {
                //组装数据
                put("taskDate",taskTime)
                put("taskState","待完成")
                put("taskCategory",taskCategory)
                put("taskContent",taskContent)
                put("taskPriority",taskPriority)
            }
            Log.d("databaseTest","插入数据")
            TaskViewModel.db?.insert("Task",null,values_task)


            //待完成任务数量增加 1
            TaskViewModel.task_wait_num++
            binding.taskWaitNum.text = TaskViewModel.task_wait_num.toString()

            //刷新任务栏列表
            TaskViewModel.taskList.add(
                Task(taskContent, taskTime ,"待完成",taskCategory,taskPriority))
            taskAdapter.setList(TaskViewModel.taskList)
            taskToDo1.adapter = taskAdapter

            //模拟把手点击，收起抽屉
            binding.handle.performClick()

        }

        //分类监听
        binding.todayTask.setOnClickListener{
            categoryChoose("今天")
        }
        binding.allTask.setOnClickListener{
            categoryChoose("all")
        }
        binding.studyTask.setOnClickListener{
            categoryChoose("学习")
        }
        binding.lifeTask.setOnClickListener{
            categoryChoose("生活")
        }
        binding.workTask.setOnClickListener{
            categoryChoose("工作")
        }

    }

    //分类选择
    @SuppressLint("Range")
    fun categoryChoose(category:String){

        //查询数据库的所有数据
        TaskViewModel.cursor =
            TaskViewModel.db?.query("Task",null,null,
                null,null,null,null)

        //先清空各list,初始化一下
        TaskViewModel.taskList.clear()
        TaskViewModel.taskDoneList.clear()
        TaskViewModel.task_wait_num = 0
        TaskViewModel.task_done_num = 0

        //再从数据库中添加
        TaskViewModel.cursor?.let {
            if(it.moveToNext()){
                do {
                    //遍历cursor对象，取出数据并添加至taskList和taskDoneList中
                    val taskDate = it.getString(it.getColumnIndex("taskDate"))
                    val taskContent = it.getString(it.getColumnIndex("taskContent"))
                    val taskState= it.getString(it.getColumnIndex("taskState"))
                    val taskPriority = it.getInt(it.getColumnIndex("taskPriority"))
                    val taskCategory = it.getString(it.getColumnIndex("taskCategory"))

                    val newTask = Task(taskContent,taskDate,taskState,taskCategory,taskPriority)


                    if (taskState == "待完成"){
                        //如果是今天的任务，则从日期判断，否则按照归属判断
                        if(category == "今天"){
                            if (taskDate == category){
                                TaskViewModel.taskList.add(newTask)
                                TaskViewModel.task_wait_num++
                            }

                        }else{
                            if(taskCategory == category || category == "all"){
                                TaskViewModel.taskList.add(newTask)
                                TaskViewModel.task_wait_num++
                            }
                       }

                    }else {
                        //如果是今天的任务，则从日期判断，否则按照归属判断
                        if(category == "今天"){
                            if (taskDate == category){
                                TaskViewModel.taskDoneList.add(newTask)
                                TaskViewModel.task_done_num++
                            }

                        }else{
                            if(taskCategory == category || category == "all"){
                                TaskViewModel.taskDoneList.add(newTask)
                                TaskViewModel.task_done_num++
                            }
                        }

                    }

                }while(it.moveToNext())
            }
        }

        //初始化任务栏展示的数目
        binding.taskDoneNum.text = TaskViewModel.task_done_num.toString()
        binding.taskWaitNum.text = TaskViewModel.task_wait_num.toString()

        //刷新recycleyview列表
        taskAdapter.setList(TaskViewModel.taskList)
        binding.taskTodoRecyclerView.adapter = taskAdapter
        taskDoneAdapter.setList(TaskViewModel.taskDoneList)
        binding.taskTodo2RecyclerView.adapter = taskDoneAdapter

    }


    override fun onDestroyView() {
        super.onDestroyView()

        TaskViewModel.task_wait_num = 0
        TaskViewModel.task_done_num = 0


        _binding = null
    }

    //显示日历控件
    @SuppressLint("SimpleDateFormat")
    private fun showDailog() {
        datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth -> //monthOfYear 得到的月份会减1所以我们要加1
                val time =
                    (monthOfYear + 1).toString() + "月" + dayOfMonth.toString()+"日"
                Log.d("测试", time)

                //将当前选择的日期显示
                binding.drawerItem.calendarChoose.text = time

                //将当前选择的日期存储
                TaskViewModel.time = time
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        )
        datePickerDialog!!.show()
        //自动弹出键盘问题解决
        datePickerDialog!!.window!!
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

    }

}