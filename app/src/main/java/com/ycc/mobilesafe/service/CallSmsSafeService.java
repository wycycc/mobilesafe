package com.ycc.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ycc.mobilesafe.db.dao.BlackNumberDao;

public class CallSmsSafeService extends Service {
    private static final String TAG = "CallSmsSafeService";
    private InnerSmsReceiver receiver;
    private BlackNumberDao dao;
    public CallSmsSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class InnerSmsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"短信到来");
            //检查发件人是不是黑名单号码,并设置了短信拦截或全部拦截
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for(Object obj:objs){
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])obj);
                //得到短信发件人
                String sender = smsMessage.getOriginatingAddress();
                String result = dao.findMode(sender);
                if("2".equals(result)||"3".equals(result)){
                    Log.i(TAG,"拦截短信");
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        dao = new BlackNumberDao(this);
        receiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephoney.SMS_RECEIVED");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(receiver,new IntentFilter("android.provider.Telephoney.SMS_RECEIVED"));
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }
}
