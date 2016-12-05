package com.ycc.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ycc.mobilesafe.utils.SmsUtils;

public class AtoolsActivity extends Activity {

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 点击事件,进入号码归属地查询的页面
     * @param view
     */
    public void numberQuery(View view){
        Intent intent = new Intent(this,NumberAddressQueryActivity.class);
        startActivity(intent);
    }

    /**
     * 点击事件，短信的备份
     * @param vie
     */
    public void smsBackup(View vie){
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在备份短信");
        pd.show();
       new Thread(){
           @Override
           public void run() {
               try {
                   SmsUtils.backupSms(AtoolsActivity.this,pd);
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           Toast.makeText(AtoolsActivity.this,"备份成功",Toast.LENGTH_LONG).show();
                       }
                   });
               } catch (Exception e) {
                   e.printStackTrace();
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           Toast.makeText(AtoolsActivity.this,"备份失败",Toast.LENGTH_LONG).show();
                       }
                   });
               } finally {
                   pd.dismiss();
               }
           };
       }.start();
    }

    /**
     * 点击事件，短信的还源
     * @param vie
     */
    public void smsRestore(View vie){

    }
}
