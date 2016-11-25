package com.ycc.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class LostFindActivity extends Activity {

    private TextView tv_safenumber;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        //判断是否做过设置向导，如果没有做过就跳转到设置向导页面去设置，否则就留在当前页面
        boolean configed = sp.getBoolean("configed",false);
        if(configed){
            //就在手机防盗页面
            setContentView(R.layout.activity_lost_find);
            tv_safenumber = (TextView) findViewById(R.id.tv_safenumber);
            //得到我们设置的安全号码
            String safenumber = sp.getString("safenumber", "");
            if(!TextUtils.isEmpty(safenumber)){
                tv_safenumber.setText(safenumber);
            }
        }else{
            //还没有做过设置向导
            Intent intent = new Intent(this,Setup1Activity.class);
            startActivity(intent);
            //关闭当前页面
            finish();
        }
    }

    /**
     * 重新进入手机防盗设置向导页面
     * @param view
     */
    public void reEnterSetup(View view){
        Intent intent = new Intent(this,Setup1Activity.class);
        startActivity(intent);
        //关闭当前页面
        finish();
    }
}
