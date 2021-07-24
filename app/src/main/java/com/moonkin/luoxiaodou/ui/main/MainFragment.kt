package com.moonkin.luoxiaodou.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.moonkin.luoxiaodou.R
import java.util.*

class MainFragment : Fragment() {
    val host = "tcp://broker.hivemq.com:1883"
    val topic = "luoxiaodou_1"
    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
        viewModel.openUart("dev/ttyMT0",9600)
        this.activity?.let { it1 -> viewModel.initMQTT(it1, host, UUID.randomUUID().toString(), topic) }
        viewModel.connect(0)

        activity?.findViewById<Button?>(R.id.btn_connect)?.setOnClickListener {
            this.activity?.let { it1 -> viewModel.initMQTT(it1, host, UUID.randomUUID().toString(), topic) }
            viewModel.connect(0)
        }
        activity?.findViewById<Button?>(R.id.btn_publish)?.setOnClickListener {
            viewModel.publish("123456".toByteArray())
        }
        activity?.findViewById<Button?>(R.id.btn_open)?.setOnClickListener {
            viewModel.openUart("dev/ttyMT0",9600)
        }
        activity?.findViewById<Button?>(R.id.btn_send)?.setOnClickListener {
            //viewModel.send()
        }
    }
    override fun onDestroy () {
        super.onDestroy()
        viewModel.disconnect()
        viewModel.closeUart()
    }
}