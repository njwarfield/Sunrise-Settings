package com.nathanjwarfield.sunrisesettings

import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter


class MainActivityViewModel : ViewModel() {
    private val domain : String = "local.sunrise-alarm.com"
    lateinit var alarmSettings: AlarmSettings
    private val client: HttpClient = HttpClient(Android) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    init {
        runBlocking {
            alarmSettings = client.get("http://$domain/")
        }
    }

    fun getAlarmTime(dayOfWeek: DayOfWeek): String {
        val format = DateTimeFormatter.ofPattern("h:mm a")
        return alarmSettings.alarmTimeByDay(dayOfWeek).format(format)
    }

    fun updateAlarm(alarm: AlarmDay) {
        alarmSettings.updateAlarmDay(alarm)
        var response: AlarmSettings? = null
        runBlocking {
             response = client.post<AlarmSettings> {
                url("http://$domain/set-alarm")
                contentType(ContentType.Application.Json)
                body = alarmSettings
            }
        }
        if(response != null) {
            alarmSettings = response as AlarmSettings
        }

        //TODO: Update LiveData
    }
}