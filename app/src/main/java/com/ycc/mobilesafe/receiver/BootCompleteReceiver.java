package com.ycc.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/11/25.
 */
public class BootCompleteReceiver extends BroadcastReceiver{

    private SharedPreferences sp;
    private TelephonyManager tm;

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //读取之前保存的sim信息
        String saveSim = sp.getString("sim","");
        //读取当前的sim卡信息
        String realSim = tm.getSimSerialNumber();
        //比较是否一样
        if(saveSim.equals(realSim)){
            //sim没有变更，还是同一个人

        }else{
            //sim已经变更，发一个短信给安全号码
            System.out.println("sim已经变更，发一个短信给安全号码");
            Toast.makeText(context,"sim已经变更，发一个短信给安全号码",Toast.LENGTH_LONG).show();
        }
    }
}
