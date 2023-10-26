package com.ajiew.phonecallapp.phonecallui

import android.annotation.SuppressLint
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.CallLog
import android.util.Log

class CallLogObserver(private val context: Context, handler: Handler?) : ContentObserver(handler) {
    private var curPhoneNumber: String? = null
    fun setCurPhoneNumber(curPhoneNumber: String) {
        this.curPhoneNumber = curPhoneNumber
        Log.d(TAG, "setCurPhoneNumber curPhoneNumber：$curPhoneNumber")
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        if (uri != null && uri == CallLog.Calls.CONTENT_URI) {
            // 通话记录发生变化，您可以在这里执行您的逻辑
            Log.d(TAG, "Call log has changed")
            // 检索最新的通话记录信息
            retrieveCallLogData()
        }
    }

    @SuppressLint("Range")
    private fun retrieveCallLogData() {
        val contentResolver = context.contentResolver
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE
        )

        // 查询通话记录
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            CallLog.Calls.DEFAULT_SORT_ORDER // 按时间排序
        )
        cursor?.use { cursor ->
            // 遍历通话记录
            while (cursor.moveToNext()) {
                val callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
                val phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                val callDate = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))

                // 在这里处理通话记录信息
                Log.d(TAG, "Call Type: $callType, Phone Number: $phoneNumber, Call Date: $callDate")
                if (phoneNumber == curPhoneNumber) {
//                    context.contentResolver.delete(CallLog.Calls.CONTENT_URI, "NUMBER=$phoneNumber", null)
                }
            }
        }
    }

    companion object {
        private const val TAG = "CallLogObserver"
    }
}
