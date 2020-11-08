package com.nathanjwarfield.sunrisesettings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.nathanjwarfield.sunrisesettings.databinding.ActivityMainBinding
import java.time.DayOfWeek
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    fun showTimePickerDialog(v: View) {
        val tag = v.tag.toString()
        val dayOfWeek = DayOfWeek.valueOf(tag.toUpperCase(Locale.ROOT))
        val alarmDay = viewModel.alarmSettings.value?.alarms!!.single{alarmDay -> alarmDay.kotlinDay == dayOfWeek.value }
        val dialog = TimePickerFragment(alarmDay, viewModel)
        dialog.show(supportFragmentManager, "Alarm Picker")
    }
}