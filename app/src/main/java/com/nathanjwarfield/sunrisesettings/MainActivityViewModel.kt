package com.nathanjwarfield.sunrisesettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter


class MainActivityViewModel : ViewModel() {
    private val domain : String = "local.sunrise-alarm.com"
    private val client: HttpClient = HttpClient(Android) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    private val _alarmSettings = MutableLiveData<AlarmSettings>()
    val alarmSettings: LiveData<AlarmSettings>
        get() = _alarmSettings

    init {
        runBlocking {
            _alarmSettings.value = client.get("http://$domain/")
        }
    }

    fun changeAlarmState(enabled: Boolean) {
        val endpoint: String = if(enabled) "on" else "off"
        runBlocking {
            val response: HttpResponse = client.get("http://$domain/$endpoint")
            if(response.status == HttpStatusCode.OK)
               _alarmSettings.value!!.enabled = enabled
        }
    }

    fun getAlarmTime(dayOfWeek: DayOfWeek): String {
        val format = DateTimeFormatter.ofPattern("h:mm a")
        return alarmSettings.value?.alarmTimeByDay(dayOfWeek)!!.format(format)
    }

    fun updateAlarm(alarm: AlarmDay) {
        alarmSettings.value?.updateAlarmDay(alarm)
        var response: AlarmSettings? = null
        runBlocking {
             response = client.post<AlarmSettings> {
                url("http://$domain/set-alarm")
                contentType(ContentType.Application.Json)
                body = alarmSettings.value!!
            }
        }
        if(response != null) {
            _alarmSettings.postValue(response as AlarmSettings)
        }
    }
}