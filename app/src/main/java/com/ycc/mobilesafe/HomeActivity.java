package com.ycc.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ycc.mobilesafe.utils.MD5Utils;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";
    private GridView list_home;
    private MyAdapter adapter;
    private static String[] names = {
            "手机防盗","通讯卫士","软件管理","进程管理","流量统计",
            "手机杀毒","缓存清理","高级工具","设置中心"
    };
    private SharedPreferences sp;
    private static int[] ids = {
        R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
            R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
            R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        list_home = (GridView) findViewById(R.id.list_home);
        adapter = new MyAdapter();
        list_home.setAdapter(adapter);
        list_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //TODO 主页面分发
                Intent intent;
                switch (position){
                    case 0://进入手机防盗页面
                        ShowLostFindDialog();
                        break;
                    case 1://加载黑名单拦截界面
                        intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
                        startActivity(intent);
                        break;
                    case 2://软件管理器
                        intent = new Intent(HomeActivity.this,AppManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 7://进入高级工具
                        intent = new Intent(HomeActivity.this,AtoolsActivity.class);
                        startActivity(intent);
                        break;
                    case 8://进入设置中心
                        intent = new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void ShowLostFindDialog() {
        //判断是否设置过密码
        if(isSetupPwd()){
            //已经设置密码了，弹出的是输入对话框
            showEnterDialog();
        }else{
            //没有设置密码，弹出设置密码对话框
            showSetupPwdDialog();
        }
    }

    private EditText et_setup_pwd;
    private EditText et_setup_confirm;
    private Button ok;
    private Button cancel;
    private AlertDialog dialog;

    /**
     * 设置密码对话框
     */
    private void showSetupPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        //自定义一个布局文件
        View view = View.inflate(HomeActivity.this,R.layout.dialog_setup_pwd,null);
        et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
        ok = (Button) view.findViewById(R.id.ok);
        cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //把这个对话框取消掉
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //取出密码
                String pwd = et_setup_pwd.getText().toString().trim();
                String pwd_confirm = et_setup_confirm.getText().toString().trim();
                if(TextUtils.isEmpty(pwd)||TextUtils.isEmpty(pwd_confirm)){
                    Toast.makeText(HomeActivity.this,"密码为空",Toast.LENGTH_LONG).show();
                    return;
                }
                //判断是否一致，才去保存
                if(pwd.equals(pwd_confirm)){
                    //一致的话，就保存密码，把对话框关闭，还要进入手机防盗页面
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("password", MD5Utils.md5Password(pwd));
                    editor.commit();
                    dialog.dismiss();
                    Log.i(TAG,"一致的话，就保存密码，把对话框关闭，还要进入手机防盗页面");
                    Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(HomeActivity.this,"密码不一致",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        //builder.setView(view);
        dialog = builder.create();
        dialog.setView(view,0,0,0,0);
        dialog.show();
    }

    /**
     * 输入密码对话框
     */
    private void showEnterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        //自定义一个布局文件
        View view = View.inflate(HomeActivity.this,R.layout.dialog_enter_pwd,null);
        et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        ok = (Button) view.findViewById(R.id.ok);
        cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //把这个对话框取消掉
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //取出密码
                String pwd = et_setup_pwd.getText().toString().trim();
                String savePwd = sp.getString("password","");//加密后的
                if(TextUtils.isEmpty(pwd)){
                    Toast.makeText(HomeActivity.this,"密码为空",Toast.LENGTH_LONG).show();
                    return ;
                }
                if(MD5Utils.md5Password(pwd).equals(savePwd)){
                    //输入的密码是我之前设置的密码
                    //把对话框消掉，进入主页面
                    dialog.dismiss();
                    Log.i(TAG,"把对话框消掉，进入手机防盗页面");
                    Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(HomeActivity.this,"密码错误",Toast.LENGTH_LONG).show();
                    et_setup_pwd.setText("");
                    return;
                }
            }
        });

        //builder.setView(view);
        dialog = builder.create();
        dialog.setView(view,0,0,0,0);
        dialog.show();
    }

    /**
     * 判断是否设置过密码
     * @return
     */
    private boolean isSetupPwd(){
        String pwd = sp.getString("password",null);
        return !TextUtils.isEmpty(pwd);
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View v = View.inflate(HomeActivity.this,R.layout.list_item_home,null);
            ImageView iv_item = (ImageView) v.findViewById(R.id.iv_item);
            TextView tv_item = (TextView) v.findViewById(R.id.tv_item);
            tv_item.setText(names[position]);
            iv_item.setImageResource(ids[position]);
            return v;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }
}
