package com.ycc.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ycc.mobilesafe.db.dao.NumberAddressQueryUtils;

public class NumberAddressQueryActivity extends Activity {

    private static final String TAG = "NumberAddressQueryActivity";
    private EditText et_phone;
    private TextView tv_result;

    /**
     * 系统提供的振动服务
     */
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);
        et_phone = (EditText) findViewById(R.id.et_phone);
        tv_result = (TextView) findViewById(R.id.tv_result);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        et_phone.addTextChangedListener(new TextWatcher() {

            /**
             * 当文本发生变化的时候回调
             * @param charSequence
             * @param i
             * @param i1
             * @param i2
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence!=null && charSequence.length()>2){
                    //查询数据库，并且显示结果
                    String address = NumberAddressQueryUtils.queryNumber(charSequence.toString());
                    tv_result.setText(address);
                }
            }

            /**
             * 当文本发生变化之前回调
             * @param charSequence
             * @param i
             * @param i1
             * @param i2
             */
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             * 当文本发生变化时回调
             * @param editable
             */
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 查询号码归属地
     * @param view
     */
    public void numberAddressQuery(View view){
        String phone = et_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"号码为空",Toast.LENGTH_LONG).show();
            Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
            shake.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float v) {
                    //函数的运算
                    return 0;
                }
            });
            et_phone.startAnimation(shake);
            //当电话号码为空时，就振动手机提醒用户
            //vibrator.vibrate(2000);
            long[] pattern = {200,200,300,300,1000,2000};
            vibrator.vibrate(pattern,-1);//-1:不重复  0:循环振动
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
