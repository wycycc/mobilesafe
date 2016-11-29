package com.ycc.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ycc.mobilesafe.R;
import com.ycc.mobilesafe.db.dao.AddressQueryDao;

public class AddressService extends Service {

    /**
     * 窗体管理者
     */
    private WindowManager wm;
    private View view;

    private static final String TAG = "AddressService";
    /**
     * 监听来电
     */
    private TelephonyManager tm;
    private MyPhoneStateListener listener;
    private OutCallReceiver receiver;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    /**
     * 在服务里定义一个广播接收者监听去电，内部类
     */
    class OutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            Log.i(TAG,"内部类的广播接收者");
            // 得到要打出去的电话号码
            String phone = getResultData();
            //根据号码查询号码归属地
            String address = AddressQueryDao.numberQuery(phone);
            //Toast.makeText(context,address,Toast.LENGTH_LONG).show();
            myToast(address);
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            //第一个参数是状态，第二个参数是电话号码
            super.onCallStateChanged(state, incomingNumber);

            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://电话铃声响起的时候，其实也是来电的时候
                    //根据得到的电话号码查询它的归属地，并显示在土司里
                    String address = AddressQueryDao.numberQuery(incomingNumber);
                    //Toast.makeText(getApplicationContext(),address,Toast.LENGTH_LONG).show();
                    myToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE://电话的空闲状态：挂电话、来电拒绝
                    //把这个View移除
                    if(view!=null) {
                        wm.removeView(view);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 自定义土司
     * @param address
     */
    private void myToast(String address) {
        view = View.inflate(this, R.layout.address_show,null);
        TextView textView = (TextView) view.findViewById(R.id.tv_address);
        textView.setText(address);
        textView.setBackgroundResource(R.drawable.call_locate_gray);

        /**view = new TextView(getApplicationContext());
        view.setText(address);
        view.setTextSize(22);
        view.setTextColor(Color.RED);*/

        //窗体的参数就设置好了
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        wm.addView(view, params);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册一个广播接收者
        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(receiver,filter);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //监听一个来电
        listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        //实例化窗体
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //代码取消注册一个广播接收者
        unregisterReceiver(receiver);
        receiver = null;

        //取消监听来电
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        listener = null;
    }
}
