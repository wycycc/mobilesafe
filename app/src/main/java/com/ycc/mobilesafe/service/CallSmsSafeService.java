package com.ycc.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.ycc.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class CallSmsSafeService extends Service {
    private static final String TAG = "CallSmsSafeService";
    private InnerSmsReceiver receiver;
    private BlackNumberDao dao;
    private TelephonyManager tm;
    private MyListener listener;

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
                //演示代码
                String body = smsMessage.getMessageBody();
                if(body.contains("fapiao")){
                    Log.i(TAG,"拦截短信发票");
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        dao = new BlackNumberDao(this);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
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
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    private class MyListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://零响模式
                    String result = dao.findMode(incomingNumber);
                    if("1".equals(result)||"3".equals(result)){
                        Log.i(TAG,"挂断电话......");
                        //1.5版本后，endCall不再应用
                        endCall();
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void endCall() {
        //IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
        try {
            //加载ServiceManager的字节码
            Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService",String.class);
            IBinder iBinder = (IBinder) method.invoke(null,TELEPHONY_SERVICE);
            ITelephony.Stub.asInterface(iBinder).endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
