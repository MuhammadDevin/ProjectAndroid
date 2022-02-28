package com.devinidn.smartalarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devinidn.smartalarm.adapter.AlarmAdapter
import com.devinidn.smartalarm.data.Alarm
import com.devinidn.smartalarm.data.local.AlarmDB
import com.devinidn.smartalarm.data.local.AlarmDao
import com.devinidn.smartalarm.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var alarmAdapter: AlarmAdapter? = null

    private val db by lazy { AlarmDB( this) }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmService = AlarmService()

        initView()
        setupRecyclerView()

    }

    override fun onResume() {
        super.onResume()

        db.alarmDao().getAlarm().observe(this){
            alarmAdapter?.setData(it)
            Log.i("GetAlarm", "setupRecyclerView: with this data $it")

        }

        /*  CoroutineScope(Dispatchers.IO).launch {
              val alarm = db.alarmDao().getAlarm() as ArrayList<Alarm>
              withContext(Dispatchers.Main){
                  alarmAdapter?.setData(alarm)
              }
              Log.i("GetAlarm", "setupRecyclerView: with this data $alarm")
          } */
    }

    private fun setupRecyclerView() {
        binding.apply {
            alarmAdapter = AlarmAdapter()
            rvReminderAlarm.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = alarmAdapter
            }
            swipeToDelete(rvReminderAlarm)
        }
    }

    private fun initView(){
        binding.apply {
            cvSetOneTimeAlarm.setOnClickListener{
                startActivity(Intent(this@MainActivity, OneTimeAlarmActivity::class.java))
            }

            cvSetRepeatingAlarm.setOnClickListener{
                startActivity(Intent(this@MainActivity, RepeatingAlarmActivity::class.java))
            }
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = alarmAdapter?.listAlarm?.get(viewHolder.adapterPosition)
                CoroutineScope(Dispatchers.IO).launch {
                    deletedItem?.let {db.alarmDao().deleteAlarm(it)  }
                    Log.i("DeleteAlarm", "onSwiped: Success deleted alarm with $", )
                }
//                alarmAdapter?.notifyItemRemoved(viewHolder.adapterPosition)
                deletedItem?.type?.let { alarmService?.cancelAlarm(applicationContext, it) }
            }

        }).attachToRecyclerView(recyclerView)

    }

}