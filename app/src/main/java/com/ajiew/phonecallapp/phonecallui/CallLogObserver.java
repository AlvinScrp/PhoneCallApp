package com.ajiew.phonecallapp.phonecallui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;

public class CallLogObserver extends ContentObserver {
    private static final String TAG = "CallLogObserver";
    private Context context;

    private String curPhoneNumber = null;

    public CallLogObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
    }

    public void setCurPhoneNumber(String curPhoneNumber) {
        this.curPhoneNumber = curPhoneNumber;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        if (uri != null && uri.equals(CallLog.Calls.CONTENT_URI)) {
            // 通话记录发生变化，您可以在这里执行您的逻辑
            Log.d(TAG, "Call log has changed");
            // 检索最新的通话记录信息
            retrieveCallLogData();
        }
    }

    private void retrieveCallLogData() {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE
        };

        // 查询通话记录
        Cursor cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                CallLog.Calls.DEFAULT_SORT_ORDER // 按时间排序
        );

        if (cursor != null) {
            try {
                // 遍历通话记录
                while (cursor.moveToNext()) {
                    int callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                    long callDate = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));

                    // 在这里处理通话记录信息
                    Log.d(TAG, "Call Type: " + callType + ", Phone Number: " + phoneNumber + ", Call Date: " + callDate);
                    if (phoneNumber.equals(curPhoneNumber)) {
                        context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, "NUMBER=" + phoneNumber, null);
                    }
                }
            } finally {
                cursor.close();
            }
        }
    }
}

