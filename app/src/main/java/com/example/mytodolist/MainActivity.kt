package com.example.mytodolist

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.widget.SeekBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.mytodolist.databinding.ActivityMainBinding
import com.example.mytodolist.ui.task.TaskViewModel
import com.example.mytodolist.utils.MyDatabaseHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    //线程池
    private lateinit var mThreadPool: ExecutorService
    internal lateinit var handler: Handler

    private val updateTextUI = 1
    private val updateImageUI = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.let{
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

        //隐藏标题栏
//        supportActionBar?.hide()

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_task, R.id.nav_focus, R.id.nav_praise
            ), drawerLayout

        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //开启线程池，更新UI
        mThreadPool = Executors.newCachedThreadPool()
        handler = @SuppressLint("HandlerLeak") object: Handler(){
            override fun handleMessage( msg: Message){
                Log.d("Handler", msg.what.toString())
                when(msg.what){
                    //更新文字的UI
                    updateTextUI->{

                    }
                    //更新图片的UI
                    updateImageUI->{

                    }
                }
            }
        }

        //创建或打开数据库和表Sensor
        TaskViewModel.db = MyDatabaseHelper(this,"TaskStore.db",1).writableDatabase
    }

    //UIid指传过来的要更改的UI的id
    fun sendMsg(type:String,UIid : Int){
        var typeUI = 1
        //判断要更新的是什么UI
        when(type){
            "text"->{
                typeUI = updateTextUI
            }
            "image"->{
                typeUI = updateImageUI
            }
        }

        mThreadPool.execute{
            val msg = Message()
            msg.what = updateTextUI
            handler.sendMessage(msg)
        }
    }


    //加载标题栏
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}