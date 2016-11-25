package com.ycc.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;

public class Setup1Activity extends BaseSetupActivity {

    //定义一个手势识别器
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);

    }

    @Override
    public void showNext() {
        Intent intent = new Intent(this,Setup2Activity.class);
        startActivity(intent);
        finish();
        //要求在finish()方法或startActivity(intent)后面执行
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
    }

    @Override
    public void showPre() {

    }


}
