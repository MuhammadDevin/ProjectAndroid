package com.devinidn.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.devinidn.smartalarm.data.Alarm
import com.devinidn.smartalarm.data.local.AlarmDB
import com.devinidn.smartalarm.databinding.ActivityOneTimeAlarmBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OneTimeAlarmActivity : AppCompatActivity(), DateDialogFragment.DialogDateSetListener,
    TimeDialogFragment.DialogTimeListener {

    private var _binding: ActivityOneTimeAlarmBinding? = null
    private val binding get() = _binding as ActivityOneTimeAlarmBinding

    private val db by lazy { AlarmDB(this) }
    private var alarmService : AlarmReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_time_alarm)

        _binding = ActivityOneTimeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmService = AlarmReceiver()
        initView()
    }

    private fun initView() {
        binding.apply {

            btnSetTimOneTime.setOnClickListener {
                val timePickerFragment  = TimeDialogFragment()
                timePickerFragment.show(supportFragmentManager, "TimePickerDialog")
            }

            btnSetDateOneTime.setOnClickListener {
                val datePickerFragment = DateDialogFragment()
                datePickerFragment.show(supportFragmentManager, "DatePickerDialog")
            }

            btnAddSetAlarm.setOnClickListener {
                val date = tvOnceAlarm.text.toString()
                val time = tvTime.text.toString()
                val message = edtNoteOneTime.text.toString()

                if (date == "Date" && time == "Time") {
                    Toast.makeText(applicationContext, "Please set Date & Time", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    alarmService?.setOneTimeAlarm(applicationContext, AlarmReceiver.TYPE_ONE_TIME, date, time, message)
                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                date,
                                time,
                                message
                            )
                        )
                        Log.i("AddAlarm", "alarm set on: $time with $message")
                        finish()
                    }
                }
            }
            btnCancel.setOnClickListener {
                finish()
            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        // calendar.set() untuk mengubah waktu yang mengambil dari waktu sekarang
        calendar.set(year, month, dayOfMonth)
        val dateFormatted = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        binding.tvOnceAlarm.text = dateFormatted.format(calendar.time)
    }

    override fun onTimeSetListener(tag: String?, hour: Int, minute: Int) {
        binding.tvTime.text = timeFormatter(hour, minute)
    }
}