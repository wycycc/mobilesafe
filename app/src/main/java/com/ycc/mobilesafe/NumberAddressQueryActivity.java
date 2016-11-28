package com.ycc.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ycc.mobilesafe.db.dao.NumberAddressQueryUtils;

public class NumberAddressQueryActivity extends Activity {

    private static final String TAG = "NumberAddressQueryActivity";
    private EditText et_phone;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);
        et_phone = (EditText) findViewById(R.id.et_phone);
       tv_result = (TextView) findViewById(R.id.tv_result);
    }

    /**
     * 查询号码归属地
     * @param view
     */
    public void numberAddressQuery(View view){
        String phone = et_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"号码为空",Toast.LENGTH_LONG).show();
            return;
        }else{
            String address = NumberAddressQueryUtils.queryNumber(phone);
            tv_result.setText(address);
            //去数据库查询号码归属地
            //1.网络查询 //2.本地数据库--数据库
            //写一个工具类去查询数据库
            Log.i(TAG,"你要查询的号码："+phone);
        }
    }
}
