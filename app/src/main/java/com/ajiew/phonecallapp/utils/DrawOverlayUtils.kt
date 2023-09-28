package com.ajiew.phonecallapp.utils

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

object DrawOverlayUtils {

     fun askForDrawOverlay(activity: AppCompatActivity) {
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle("允许显示悬浮框")
            .setMessage("为了使电话监听服务正常工作，请允许这项权限")
            .setPositiveButton("去设置") { dialog: DialogInterface, which: Int ->
                openDrawOverlaySettings(activity)
                dialog.dismiss()
            }
            .setNegativeButton("稍后再说") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .create()
        alertDialog.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        alertDialog.show()
    }

    /**
     * 跳转悬浮窗管理设置界面
     */
    private fun openDrawOverlaySettings(activity: AppCompatActivity) {
        val context: Context = activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M 以上引导用户去系统设置中打开允许悬浮窗
            // 使用反射是为了用尽可能少的代码保证在大部分机型上都可用
            try {
                val clazz: Class<*> = Settings::class.java
                val field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION")
                val intent = Intent(field[null].toString())
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setData(Uri.parse("package:" + context.packageName))
                context.startActivity(intent)
            } catch (e: Exception) {
                ToastUtils.show(context, "请在悬浮窗管理中打开权限")
            }
        }
    }
}