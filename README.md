# luoxiaodou 萝小逗机器人 app

## 简介

这个App需要安装在机器人中。

需要拆机接入usb， 然后通过adb 安装。

拆机需要先拆轮子，然后把底座拆开，就可以看到主板上usb接口。



## 配置

默认会连接HiveMQ broker。 

如果用自己的broker，需要修改 MainFragment.kt 中的host 和topic。

```kotlin
val host = "tcp://broker.hivemq.com:1883"
val topic = "luoxiaodou_1"
```



## 控制App

手机端控制软件Luoxiaodou_ctrl
