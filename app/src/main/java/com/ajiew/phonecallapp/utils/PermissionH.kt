package com.ajiew.phonecallapp.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PermissionH(val activity: AppCompatActivity) {

    companion object{
        val requestCode = 10000
    }


    val permissions = arrayOf(
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.CALL_PHONE
    )

    fun requestPermissionsX(callback: (allGranted: Boolean, deniedList: List<String>)->Unit) {
        checkPermissionsX{allGranted,deniedList->
            if (allGranted && deniedList.isNullOrEmpty()) {
                callback.invoke(true, deniedList)
            } else {
                val array: Array<String> = deniedList.toTypedArray()
                ActivityCompat.requestPermissions(activity, array, requestCode)
            }
        }
    }

    fun checkPermissionsX(callback: (allGranted: Boolean, deniedList: List<String>)->Unit) {
        val deniedList = mutableListOf<String>()
        permissions.forEach {
            val granted = ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                deniedList.add(it)
            }
        }
        val allGranted = deniedList.isEmpty()
        callback.invoke(allGranted,deniedList)

    }

    fun openAppSettings() {
        val intent = Intent()
        val  context =activity

        // 根据 Android 版本不同，打开不同的设置界面
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.setData(Uri.fromParts("package", context.packageName, null))
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_SETTINGS)
        }
        context.startActivity(intent)
    }


}
