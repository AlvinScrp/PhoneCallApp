package com.ajiew.phonecallapp.phonecallui;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.telecom.Call;
import android.telecom.InCallService;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.ajiew.phonecallapp.ActivityStack;


/**
 * 监听电话通信状态的服务，实现该类的同时必须提供电话管理的 UI
 *
 * @author aJIEw
 * @see PhoneCallActivity
 * @see android.telecom.InCallService
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PhoneCallService extends InCallService {


    private final Call.Callback callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);

            switch (state) {
                case Call.STATE_ACTIVE: {

                    break;
                }

                case Call.STATE_DISCONNECTED: {
                    ActivityStack.getInstance().finishActivity(PhoneCallActivity.class);
                    break;
                }

            }
        }
    };

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        call.registerCallback(callback);
        PhoneCallManager.call = call;


        CallType callType = null;

        if (call.getState() == Call.STATE_RINGING) {
            callType = CallType.CALL_IN;
        } else if (call.getState() == Call.STATE_CONNECTING) {
            callType = CallType.CALL_OUT;
        }

        if (callType != null) {
            Call.Details details = call.getDetails();
            String phoneNumber = details.getHandle().getSchemeSpecificPart();
            if (callLogObserver != null) {
                callLogObserver.setCurPhoneNumber(phoneNumber);
            }
            PhoneCallActivity.actionStart(this, phoneNumber, callType);
        }
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        call.unregisterCallback(callback);
        PhoneCallManager.call = null;
    }

    CallLogObserver callLogObserver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // 创建观察者对象
        callLogObserver = new CallLogObserver(this, new Handler(Looper.getMainLooper()));

        // 注册观察者
        registerCallLogObserver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterCallLogObserver();
    }

    private void registerCallLogObserver() {
        ContentResolver contentResolver = getContentResolver();
        contentResolver.registerContentObserver(
                CallLog.Calls.CONTENT_URI,
                true,
                callLogObserver
        );
    }

    private void unregisterCallLogObserver() {
        ContentResolver contentResolver = getContentResolver();
        contentResolver.unregisterContentObserver(callLogObserver);
    }

//    private void removeCallLog(Call call) {
//        String phoneNumber = call.getDetails().getHandle().getSchemeSpecificPart();
//        String queryString = "NUMBER=" + phoneNumber;
//        ContentResolver resolver = this.getContentResolver();
//        resolver.notifyChange(CallLog.Calls.CONTENT_URI, new CallLogObserver());
//        resolver.delete(CallLog.Calls.CONTENT_URI, queryString, null);
//
//
//    }

    public enum CallType {
        CALL_IN,
        CALL_OUT,
    }
}
