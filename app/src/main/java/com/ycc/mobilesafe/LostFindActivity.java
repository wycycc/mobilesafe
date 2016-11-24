package com.ycc.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LostFindActivity extends Activity {

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
        }else{
            //还没有做过设置向导
            Intent intent = new Intent(this,Setup1Activity.class);
            startActivity(intent);
            //关闭当前页面
            finish();
        }
    }
}
