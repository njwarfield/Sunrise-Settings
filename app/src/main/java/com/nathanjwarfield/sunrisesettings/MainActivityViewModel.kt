package com.nathanjwarfield.sunrisesettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

class MainActivityViewModelFactory(alarmAddress: String) : ViewModelProvider.Factory {
    private val _alarmAddress: String = alarmAddress

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(_alarmAddress::class.java).newInstance(_alarmAddress)
    }
}

class MainActivityViewModel(alarmAddress: String) : ViewModel() {
    //TODO: Move all api communication methods out of view model, including configuration
    private val domain: String = alarmAddress

    private val client: HttpClient = HttpClient(Android) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
    }

    private val _alarmSettings = MutableLiveData<AlarmSettings>()
    val alarmSettings: LiveData<AlarmSettings>
        get() = _alarmSettings

    private var _alarmOnline = MutableLiveData<Boolean>(false)
    val alarmOnline: LiveData<Boolean>
        get() = _alarmOnline

    init {
        try {
            runBlocking {
                _alarmSettings.value = client.get("http://$domain/")
                _alarmOnline.value = true
            }
        } catch (e: HttpRequestTimeoutException) {
            _alarmSettings.value = AlarmSettings(false, mutableListOf())
            //TODO: Timeout error, viewModel initialized and update view to notify user
            _alarmOnline.value = false
        } finally {
            if (_alarmSettings.value?.alarms?.count() != 7) {
                //TODO: Set the app state to error
            }
        }
    }

    fun changeAlarmState(enabled: Boolean) {
        val endpoint: String = if (enabled) "on" else "off"

        runBlocking{
            try {
                val response: HttpResponse = client.get("http://$domain/$endpoint")
                if (response.status == HttpStatusCode.OK)
                    _alarmSettings.value!!.enabled = enabled
            } catch (e: HttpRequestTimeoutException) {
                _alarmOnline.value = false
            }
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
                _alarmOnline.value = true
            }
        }
        if (response != null) {
            _alarmSettings.postValue(response as AlarmSettings)
        }
    }
}