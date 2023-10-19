package com.ajiew.phonecallapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.ajiew.phonecallapp.databinding.ActivityMainBinding
import com.ajiew.phonecallapp.listenphonecall.CallListenerService
import com.ajiew.phonecallapp.utils.Dialer
import com.ajiew.phonecallapp.utils.ListenCallCheckChanger
import com.ajiew.phonecallapp.utils.PermissionH
import com.ajiew.phonecallapp.utils.ToastUtils

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var dialer: Dialer

    private lateinit var listenCallCheckChanger: ListenCallCheckChanger

    private lateinit var permissionH: PermissionH


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        permissionH = PermissionH(this)
        dialer = Dialer(this)
        listenCallCheckChanger = ListenCallCheckChanger(this)
        initView()
    }

    private fun initView() {
        val number = "10010"
        binding.etPhone.setText(number)
        binding.etPhone.setSelection(number.length)
        binding.btnTel.setOnClickListener { tel1(binding.etPhone.text.toString()) }
        binding.btnCallNana.setOnClickListener { tel1("10010") }
        binding.btnRequestPermission.setOnClickListener { requestPermission() }
        binding.btnOtherSetting.setOnClickListener { permissionH.openAppSettings() }
        binding.switchPhoneCall.setOnClickListener { v: View? ->
            // 发起将本应用设为默认电话应用的请求，仅支持 Android M 及以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (binding.switchPhoneCall.isChecked) {
                    dialer.requestAsDefaultDialer()
                } else { // 取消时跳转到默认设置页面
                    startActivity(Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"))
                }
            } else {
                ToastUtils.show(this@MainActivity, "Android 6.0 以上才支持修改默认电话应用！")
                binding.switchPhoneCall.isChecked = false
            }
        }

        listenCallCheckChanger.setup(binding.switchListenCall)


    }

    private fun requestPermission() {
        permissionH.requestPermissionsX { allGranted, deniedList ->
            updatePermissionUI(allGranted, deniedList)
        }
    }

    private fun tel1(number: String) {
        val intent = Intent()
        intent.setAction(Intent.ACTION_CALL)
        intent.setData(Uri.parse("tel:$number"))
        startActivity(intent)
    }


    override fun onResume() {
        super.onResume()
        binding.switchPhoneCall.isChecked = dialer.isDefaultPhoneCallApp()
        binding.switchListenCall.isChecked = listenCallCheckChanger.isServiceRunning(CallListenerService::class.java)
        checkPermissionsX()

    }

    private fun checkPermissionsX(toast: Boolean = false) {
        permissionH.checkPermissionsX { allGranted, deniedList ->
            updatePermissionUI(allGranted, deniedList, toast)
        }
    }

    private fun updatePermissionUI(allGranted: Boolean, deniedList: List<String>, toast: Boolean = false) {
        if (allGranted) {
            binding.tvPermissionResult.text = "权限已全部授权！"
            binding.btnRequestPermission.visibility = View.GONE
            if (toast) {
                ToastUtils.show(this@MainActivity, "All permissions are granted")
            }
        } else {
            binding.btnRequestPermission.visibility = View.VISIBLE
            val text = deniedList.joinToString("\n") { it.replace("android.permission.", "") }
            binding.tvPermissionResult.text = "这些权限被拒绝：\n$text"
            if (toast) {
                ToastUtils.show(this@MainActivity, "These permissions are denied: $text")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionH.requestCode) {
            checkPermissionsX(true)
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//    }
}
