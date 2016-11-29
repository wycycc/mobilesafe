package com.ycc.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
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
    private SharedPreferences sp;

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

    private WindowManager.LayoutParams params;

    /**
     * 自定义土司
     * @param address
     */
    private void myToast(String address) {
        view = View.inflate(this, R.layout.address_show,null);
        TextView textView = (TextView) view.findViewById(R.id.tv_address);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //给view对象设置一个触摸监听器
        view.setOnTouchListener(new View.OnTouchListener() {
            int startX,startY;//定义手指的初始化位置
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN://手指按下屏幕
                        startX = (int) event.getRawX();
                        startY = (int)event.getRawY();
                        Log.i(TAG,"开始位置:"+startX+","+startY);
                        break;
                    case MotionEvent.ACTION_MOVE://手指在屏幕上移动
                        int newX = (int)event.getRawX();
                        int newY = (int)event.getRawY();
                        Log.i(TAG,"新的位置:"+newX+","+newY);
                        int dx = newX - startX;
                        int dy = newY - startY;
                        Log.i(TAG,"手指的偏移量:"+dx+","+dy);
                        Log.i(TAG,"更新imageview在窗体的位置，偏移量："+dx+","+dy);
                        params.x += dx;
                        params.y += dy;
                        //考虑边界问题
                        if(params.x<0){
                            params.x = 0;
                        }
                        if(params.y<0){
                            params.y = 0;
                        }
                        if(params.x>(wm.getDefaultDisplay().getWidth()-view.getWidth())){
                            params.x = (wm.getDefaultDisplay().getWidth()-view.getWidth());
                        }
                        if(params.y>(wm.getDefaultDisplay().getHeight()-view.getHeight())){
                            params.y = (wm.getDefaultDisplay().getHeight()-view.getHeight());
                        }
                        wm.updateViewLayout(view,params);
                        //重新初始化手指的开始位置和结束位置
                        startX = (int)event.getRawX();
                        startY = (int)event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP://手指离开屏幕一瞬间
                        //记录控件距离屏幕左上解的坐标
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("lastx",params.x);
                        editor.putInt("lasty",params.y);
                        editor.commit();
                        break;
                }
                return true;//事件处理完毕 了，不让父控件父布局触摸事件了
            }
        });
        //"半透明","活力橙","卫士蓝","金属灰","苹果绿"
        int[] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};
        textView.setText(address);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        view.setBackgroundResource(ids[sp.getInt("which",0)]);

        /**view = new TextView(getApplicationContext());
        view.setText(address);
        view.setTextSize(22);
        view.setTextColor(Color.RED);*/

        //窗体的参数就设置好了
       params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //与窗体左上解对齐
        params.gravity = Gravity.TOP;
        //指定窗体距离左边100，上边100个像素
        params.x = sp.getInt("lastx",0);
        params.y = sp.getInt("lasty",0);

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //android系统里具有电话优先级的一种窗体类型,需添加权限
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
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
