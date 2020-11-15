package com.nathanjwarfield.sunrisesettings

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.DayOfWeek
import java.time.LocalTime

class AlarmSettings(var enabled: Boolean, var alarms: MutableList<AlarmDay>) {
    fun alarmTimeByDay(day: DayOfWeek): LocalTime {
        val dayAlarm = alarms.singleOrNull { alarmDay -> alarmDay.kotlinDay == day.value }
        return if(dayAlarm == null) LocalTime.MIN else LocalTime.of(dayAlarm.hour, dayAlarm.minute)
    }

    fun updateAlarmDay(alarm: AlarmDay) {
        alarms.removeIf { it.day == alarm.day }
        alarms.add(alarm)
    }
}

data class AlarmDay(
    @JsonProperty("d") val day: Int,
    @JsonProperty("h") var hour: Int,
    @JsonProperty("m") var minute: Int
) {
    //  Time library used on ESP32 starts monday on 1 instead of Sunday.
    @JsonIgnore
    val kotlinDay: Int = if (day == 1) 7 else day - 1
}