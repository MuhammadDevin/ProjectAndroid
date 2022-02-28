package com.devinidn.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.devinidn.smartalarm.data.Alarm
import com.devinidn.smartalarm.data.local.AlarmDB
import com.devinidn.smartalarm.databinding.ActivityRepeatingAlarmBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepeatingAlarmActivity : AppCompatActivity() {

    private var _binding : ActivityRepeatingAlarmBinding? = null
    private val binding get() = _binding as ActivityRepeatingAlarmBinding

    private val db by lazy { AlarmDB(this) }

    private var alarmReceiver: AlarmReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmReceiver = alarmReceiver
        initView()
    }

    private fun initView() {
        binding.apply {
            btnSetTime.setOnClickListener {
                val timePickerDialog = TimeDialogFragment()
                timePickerDialog.show(supportFragmentManager, "TimePickerDialog")
            }
            btnAdd.setOnClickListener {
                val time = tvOneTimeAlarm.text.toString()
                val message = edtNoteOneTime.text.toString()

                if (time != "Time") {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.txt_toast_alarm),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    alarmReceiver?.setRepeatingAlarm(
                        applicationContext,
                        AlarmReceiver.TYPE_REPEATING,
                        time,
                        message
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                "Repeating Alarm",
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
}