package com.moonkin.luoxiaodou

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moonkin.luoxiaodou.ui.main.MainFragment


class StartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //此处及是重启的之后，打开我们app的方法
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            val intent = Intent(context, MainActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 非常重要，如果缺少的话，程序将在启动时报错
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //自启动APP（Activity）
            context.startActivity(intent)
            //自启动服务（Service）
            //context.startService(intent);
        }
    }
}
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}