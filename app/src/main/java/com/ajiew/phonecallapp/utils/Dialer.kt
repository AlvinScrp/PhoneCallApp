package com.ajiew.phonecallapp.utils

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ajiew.phonecallapp.R

class Dialer(private val activity: AppCompatActivity) {

    private val dialerRequestLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            ToastUtils.show(activity, activity.getString(R.string.app_name) + " 已成为默认电话应用")
        }
    }


    fun requestAsDefaultDialer() {
        // Android 10 之后需要通过 RoleManager 修改默认电话应用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val roleManager = activity.getSystemService(AppCompatActivity.ROLE_SERVICE) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
            dialerRequestLauncher.launch(intent)
        } else {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, activity.packageName)
            activity.startActivity(intent)
        }
    }

    fun isDefaultPhoneCallApp(): Boolean {
        /**
         * Android M 及以上检查是否是系统默认电话应用
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val manger = activity.getSystemService(AppCompatActivity.TELECOM_SERVICE) as TelecomManager
            if (manger != null && manger.defaultDialerPackage != null) {
                return manger.defaultDialerPackage == activity.packageName
            }
        }
        return false
    }
}