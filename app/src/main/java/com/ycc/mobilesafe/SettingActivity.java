package com.ycc.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ycc.mobilesafe.service.AddressService;
import com.ycc.mobilesafe.ui.SettingclickView;
import com.ycc.mobilesafe.ui.SettingitemView;
import com.ycc.mobilesafe.utils.ServiceUtils;

public class SettingActivity extends Activity {

    //设置是否自动更新
    private SettingitemView siv_update;
    /**
     * 用来保存软件的参数
     */
    private SharedPreferences sp;
    //设置是否开启来电归属地显示
    private SettingitemView siv_show_addr;
    private Intent showAddressIntent;

    //设置归属地显示框背景
    private SettingclickView scv_changebg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        siv_show_addr = (SettingitemView) findViewById(R.id.siv_show_addr);
        //设置是否开启自动升级
        siv_update = (SettingitemView) findViewById(R.id.siv_update);

        boolean update = sp.getBoolean("update",false);
        if(update){
            //自动升级已经开启
            siv_update.setChecked(true);
            //siv_update.setDesc("自动升级已经开启");
        }else{
            //自动升级已经关闭
            siv_update.setChecked(false);
            //siv_update.setDesc("自动升级已经关闭");
        }
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                //判断是否选中
                //已经打开自动升级
                if(siv_update.isChecked()){
                    siv_update.setChecked(false);
                    //siv_update.setDesc("自动升级已经关闭");
                    editor.putBoolean("update",false);
                }else{
                    //没有打开自动升级
                    siv_update.setChecked(true);
                    //siv_update.setDesc("自动升级已经开启");
                    editor.putBoolean("update",true);
                }
                editor.commit();
            }
        });
        //设置是否开启来电归属地显示
        showAddressIntent = new Intent(this, AddressService.class);
        boolean isRunning = ServiceUtils.isServiceRunning(this,"com.ycc.mobilesafe.service.AddressService");
        if(isRunning){
            //监听来电的服务是运行的
            siv_show_addr.setChecked(true);
        }else{
            //监听来电的服务是关闭的
            siv_show_addr.setChecked(false);
        }

        siv_show_addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //监听来电显示的服务已经开启了
                if(siv_show_addr.isChecked()){
                    //关闭
                    stopService(showAddressIntent);
                    siv_show_addr.setChecked(false);
                }else{
                    //开启
                    startService(showAddressIntent);
                    siv_show_addr.setChecked(true);
                }
            }
        });
        //设置号码归属地的背景
        scv_changebg = (SettingclickView) findViewById(R.id.scv_changebg);
        scv_changebg.setTitle("归属地提示框风格");
        final String[] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
        int which = sp.getInt("which",0);
        scv_changebg.setDesc(items[which]);
        scv_changebg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dd = sp.getInt("which",0);
                //弹出一个对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("归属地提示框风格");
                builder.setSingleChoiceItems(items,dd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //保存选择参数
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("which",which);
                        editor.commit();
                        scv_changebg.setDesc(items[which]);
                        //取消对话框
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("cancel",null);
                builder.show();
            }
        });
    }
}
