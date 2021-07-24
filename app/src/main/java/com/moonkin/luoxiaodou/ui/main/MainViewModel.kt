package com.moonkin.luoxiaodou.ui.main

//import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread

//import com.hivemq.client.mqtt.MqttClient
//import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import tp.xmaihh.serialport.SerialHelper
import tp.xmaihh.serialport.bean.ComBean
import java.io.IOException
import java.io.UnsupportedEncodingException


class MainViewModel : ViewModel() {
    //private var mBlockingClient: Mqtt3BlockingClient? = null
    private var mTopic:String = ""
    private var serialHelper: SerialHelper? = null
    private var client:MqttAndroidClient? = null
    var TAG:String = "MQTT"
    // TODO: Implement the ViewModel
    fun initMQTT(activity: Activity, host:String, id:String, topic:String) {
        mTopic = topic
        //val clientId: String = "adasdjlkasjdniv"
        client = MqttAndroidClient(
            activity.applicationContext, host,
            id
        )
        client!!.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                Log.i(TAG, "connection lost")
                connect(10000)
            }

            @Throws(java.lang.Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.i(TAG, "topic: " + topic + ", msg: " + String(message.payload))
                send(message.payload)

            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                Log.i(TAG, "msg delivered")
            }
        })
    }
    fun connect(wait:Long) {

        viewModelScope.launch(Dispatchers.IO) {
            if (wait > 0)
                delay(wait)
            try {

                val option= MqttConnectOptions()
                option.connectionTimeout = 60
                option.userName = "hnmsky"
                option.password = "821122hh".toCharArray()
                val token = client!!.connect(option)
                token.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        // We are connected
                        Log.d(TAG, "connect onSuccess")
                        //val topic = "foo/bar"
                        val qos = 1
                        try {
                            publish("ready".toByteArray())
                            val subToken = client!!.subscribe(mTopic, qos)
                            subToken.actionCallback = object : IMqttActionListener {
                                override fun onSuccess(asyncActionToken: IMqttToken) {
                                    // The message was published
                                    Log.d(TAG, "sub onSuccess")
                                }

                                override fun onFailure(
                                    asyncActionToken: IMqttToken,
                                    exception: Throwable
                                ) {
                                    Log.d(TAG, "sub onFailure")
                                    // The subscription could not be performed, maybe the user was not
                                    // authorized to subscribe on the specified topic e.g. using wildcards
                                    disconnect()
                                    connect(10000)
                                }
                            }
                        } catch (e: MqttException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d(TAG, "connect onFailure")
                        connect(10000)
                    }
                }
            } catch (e: MqttException) {
                e.printStackTrace()
            }

        }

    }
    fun openUart(port:String, baudrate: Int) {
        try {
        serialHelper = object : SerialHelper(port, baudrate) {
            override fun onDataReceived(comBean: ComBean) {
                //publish(comBean.bRec)
                //Log.i("mqtt:", String(comBean.bRec))
            }
        }


            (serialHelper as SerialHelper).open()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }
    fun closeUart() {
        try {
            (serialHelper as SerialHelper).close()
        }catch (ex:Exception) {
            Log.i("mqtt:", ex.toString())
            //client.disconnect()
        }
    }
    fun send(payload: ByteArray){

        (serialHelper as SerialHelper).send(payload)
    }
    fun publish(payload:ByteArray) {
        //val topic = "foo/bar"
        //val payload = "the payload"
        //var encodedPayload = ByteArray(0)
        try {
            //encodedPayload = payload.toByteArray(charset("UTF-8"))
            val message = MqttMessage(payload)
            client!!.publish(mTopic, message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun disconnect() {
        client!!.disconnect()
    }
}