package com.ycc.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.ycc.mobilesafe.R;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private SharedPreferences sp;

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //写接收短信的代码
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        for(Object obj:objs){
            //具体的某一条短信
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
            //发送者
            String sender = sms.getOriginatingAddress();            //15532335556

            String safenumber = sp.getString("safenumber","");
            Toast.makeText(context,sender,Toast.LENGTH_LONG).show();//5556
            Log.i(TAG,"=============================="+sender);
            String content = sms.getMessageBody();

            if(sender.contains(safenumber)){
                if("#*location*#".equals(content)){
                    //得到手机的GPS
                    Log.i(TAG,"得到手机的GPS");
                    //把这个广播终止掉
                    abortBroadcast();
                }else if("#*alarm*#".equals(content)){
                    //播放报警影音
                    Log.i(TAG,"播放报警影音");
                    MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                    player.setLooping(false);
                    player.setVolume(1.0f,1.0f);
                    player.start();
                    abortBroadcast();
                }else if("#*wipedata*#".equals(content)){
                    //远程清除数据
                    Log.i(TAG,"远程清除数据");
                    abortBroadcast();
                }else if("#*lockscreen*#".equals(content)){
                    //远程锁屏
                    Log.i(TAG,"远程锁屏");
                    abortBroadcast();
                }
            }
        }

    }
}
