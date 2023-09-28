package com.ajiew.phonecallapp.utils

import android.app.ActivityManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.ajiew.phonecallapp.listenphonecall.CallListenerService

class ListenCallCheckChanger(private val activity: AppCompatActivity) {

    private var switchCallCheckChangeListener: CompoundButton.OnCheckedChangeListener? = null

//    private var switchListenCall: Switch

    fun setup(switchListenCall: Switch) {

        // 检查是否开启了权限
        switchCallCheckChangeListener =
            CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
                if (isChecked
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !Settings.canDrawOverlays(activity)
                ) {
                    // 请求 悬浮框 权限
                    DrawOverlayUtils.askForDrawOverlay(activity)
                    // 未开启时清除选中状态，同时避免回调
                    switchListenCall.setOnCheckedChangeListener(null)
                    switchListenCall.isChecked = false
                    switchListenCall.setOnCheckedChangeListener(switchCallCheckChangeListener)
                    return@OnCheckedChangeListener
                }

//            if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
//                    ContextCompat.checkSelfPermission(this,
//                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//                ToastUtils.show(this@MainActivity, "缺少获取电话状态权限";
//                return;
//            }
                val callListener = Intent(activity, CallListenerService::class.java)
                if (isChecked) {
                    activity.startService(callListener)
                    ToastUtils.show(activity, "电话监听服务已开启")
                } else {
                    activity.stopService(callListener)
                    ToastUtils.show(activity, "电话监听服务已关闭")
                }
            }
        switchListenCall.setOnCheckedChangeListener(switchCallCheckChangeListener)
    }

    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = activity.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as? ActivityManager
            ?: return false
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

}