package com.nathanjwarfield.sunrisesettings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
        val sharedPreferences =
            this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)

        val alarmAddress = sharedPreferences.getString(
            getString(R.string.alarm_address_key),
            getString(R.string.alarm_default_address)
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, MainActivityViewModelFactory(alarmAddress!!)).get(MainActivityViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setActionBar(binding.toolbar)

        viewModel.alarmOnline.observe(this, { alarmOnline ->
            binding.alarmEnabled.isChecked = alarmOnline
            binding.alarmEnabled.isVisible = alarmOnline
        })

        viewModel.alarmSettings.observe(this, androidx.lifecycle.Observer {
            binding.mondayAlarmTime.text = viewModel.getAlarmTime(DayOfWeek.MONDAY)
            binding.tuesdayAlarmTime.text = viewModel.getAlarmTime(DayOfWeek.TUESDAY)
            binding.wednesdayAlarmTime.text = viewModel.getAlarmTime(DayOfWeek.WEDNESDAY)
            binding.thursdayAlarmTime.text = viewModel.getAlarmTime(DayOfWeek.THURSDAY)
            binding.fridayAlarmTime.text = viewModel.getAlarmTime(DayOfWeek.FRIDAY)
            binding.saturdayAlarmTime.text = viewModel.getAlarmTime(DayOfWeek.SATURDAY)
            binding.sundayAlarmTime.text = viewModel.getAlarmTime(DayOfWeek.SUNDAY)

            binding.alarmEnabled.isChecked = it.enabled
        })
    }

    fun showTimePickerDialog(v: View) {
        val tag = v.tag.toString()
        val dayOfWeek = DayOfWeek.valueOf(tag.toUpperCase(Locale.ROOT))
        val alarmDay =
            viewModel.alarmSettings.value?.alarms!!.single { alarmDay -> alarmDay.kotlinDay == dayOfWeek.value }
        val dialog = TimePickerFragment(alarmDay, viewModel)
        dialog.show(supportFragmentManager, "Alarm Picker")
    }
}