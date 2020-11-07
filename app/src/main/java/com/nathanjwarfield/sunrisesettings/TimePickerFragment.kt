package com.nathanjwarfield.sunrisesettings

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment

class TimePickerFragment(private val alarm: AlarmDay, private var viewModel: MainActivityViewModel) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(activity, this, alarm.hour, alarm.minute, false)
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        alarm.hour = p1
        alarm.minute = p2
        viewModel.updateAlarm(alarm)
    }
}