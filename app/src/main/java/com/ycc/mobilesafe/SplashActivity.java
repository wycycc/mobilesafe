package com.ycc.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.ycc.mobilesafe.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 系统启动动画页面
 */
public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    private static final int SHOW_UPDATE_DIALOG = 0;
    public static final int ENT_HOME = 1;
    private static final int URL_ERROR = 2;
    private static final int NETWORK_ERROR = 3;
    private static final int JSON_ERROR = 4;
    private TextView tv_splash_version;
    private TextView tv_update_info;
    /**
     * 新版本下载信息
     */
    private String description;
    private String apkurl;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号 " + getVersionName());
        tv_update_info = (TextView) findViewById(R.id.tv_update_info);

        boolean update = sp.getBoolean("update",false);
        
        installShortCut();

        //拷贝数据库
        copyDB();

        if(update){
            //检查升级
            checkUpdate();
        }else{
            //自动升级已经关闭
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //进入主页面
                    enterHome();
                }
            },2000);
        }
        AlphaAnimation aa = new AlphaAnimation(0.2f,1.0f);
        aa.setDuration(500);
        findViewById(R.id.rl_root_splash).startAnimation(aa);
    }

    /**
     * 创建快捷图标
     */
    private void installShortCut() {
        boolean shortcut = sp.getBoolean("shortcut", false);
        if(shortcut)
            return;
        SharedPreferences.Editor editor = sp.edit();
        //发送广播的意图， 大吼一声告诉桌面，要创建快捷图标了
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //快捷方式  要包含3个重要的信息 1，名称 2.图标 3.干什么事情
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机小卫士");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        //桌面点击图标对应的意图。
        Intent shortcutIntent = new Intent();
        shortcutIntent.setAction("android.intent.action.MAIN");
        shortcutIntent.addCategory("android.intent.category.LAUNCHER");
        shortcutIntent.setClassName(getPackageName(), "com.itheima.mobilesafe.SplashActivity");
//		shortcutIntent.setAction("com.itheima.xxxx");
//		shortcutIntent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        sendBroadcast(intent);
        editor.putBoolean("shortcut", true);
        editor.commit();
    }

    /**
     *  //path 把address.db这个数据库拷贝到data/data/包名/files/address.db
     */
    private void copyDB() {
        //只要拷贝了一次，就不用再拷贝了
        try {
            File file = new File(getFilesDir(),"address.db");
            if(file.exists()&&file.length()>0){
                //正常，不需要拷贝了
                Log.i(TAG, "正常，不需要拷贝了");
            }else{
                InputStream is = getAssets().open("address.db");

                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while((len = is.read(buffer))!=-1){
                    fos.write(buffer,0,len);
                }
                is.close();
                fos.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPDATE_DIALOG://显示升级的对话框
                    Log.i(TAG, "显示升级对话框");
                    showUpdateDialog();
                    break;
                case ENT_HOME://进入主页面
                    enterHome();
                    break;
                case URL_ERROR://url错误
                    enterHome();
                    Toast.makeText(getApplicationContext(), "url错误", Toast.LENGTH_LONG).show();
                    break;
                case NETWORK_ERROR://网络异常
                    enterHome();
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_LONG).show();
                    break;
                case JSON_ERROR://json解析出错
                    enterHome();
                    Toast.makeText(SplashActivity.this, "json解析出错", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };



    /**
     * 检查是否有新版本，如果有就升级
     */
    private void checkUpdate() {
        new Thread() {
            public void run() {
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //URL url = new URL(getString(R.string.serverurl));
                    URL url = new URL(getString(R.string.serverurl));
                    //联网
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("contentType", "GBK");
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(4000);
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        //联网成功
                        InputStream is = conn.getInputStream();
                        //InputStreamReader isr = new InputStreamReader(is,"UTF-8");
                        //把流转成String
                        String result = StreamTools.readFromStreamReader(is);
                        Log.i(TAG, "联网成功" + result);
                        //json解析
                        JSONObject obj = new JSONObject(result);
                        //得到服务器的版本信息
                        String version = (String) obj.get("version");
                        description = (String) obj.get("description");
                        apkurl = (String) obj.get("apkurl");

                        //校验是否有新版本
                        if (getVersionName().equals(version)) {
                            //版本一致，没有新版本,进入主页面
                            msg.what = ENT_HOME;
                        } else {
                            //有新版本，弹出一升级对话框
                            msg.what = SHOW_UPDATE_DIALOG;
                        }
                    }
                } catch (MalformedURLException e) {
                    msg.what = URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what = NETWORK_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    // 花了多少时间
                    long dTime = endTime - startTime;
                    //2000
                    if(dTime<2000){
                        try {
                            Thread.sleep(2000 - dTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 弹出升级对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示升级");
        //builder.setCancelable(false);//强制升级
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //进入主页面
                enterHome();
                dialogInterface.dismiss();
            }
        });
        builder.setMessage(description);
        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //下载apk,并且替换安装
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    //sdcard存在
                    FinalHttp finalHttp = new FinalHttp();
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    finalHttp.download(apkurl, path + "/mobilesafe2.0.apk", new AjaxCallBack<File>() {

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            t.printStackTrace();
                            Toast.makeText(getApplicationContext(),"下载失败",Toast.LENGTH_LONG).show();
                            super.onFailure(t, errorNo, strMsg);
                        }

                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            tv_update_info.setVisibility(View.VISIBLE);
                            //当前下载的百分比
                            int progress = (int) (current*100/count);
                            tv_update_info.setText("下载进度:"+progress+"%");
                        }

                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            installApk(file);
                        }

                        /**
                         * 安装apk
                         * @param file
                         */
                        private void installApk(File file) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

                            startActivity(intent);
                        }

                    });
                }else{
                    Toast.makeText(getApplicationContext(),"没有sdcard,请安装后再试",Toast.LENGTH_LONG).show();
                    return ;
                }
            }
        });
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                //取消对话框，进入主界面
                dialog.dismiss();
                enterHome();
            }
        });
        builder.show();
    }

    /**
     * 激活主页
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //关闭当前页
        finish();
    }

    /**
     * 得到应用程序的版本名称
     *
     * @return
     */
    private String getVersionName() {
        //用来管理手机apk
        PackageManager manager = getPackageManager();
        //得到知道apk的功能清单文件
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionNmae = info.versionName;
        return versionNmae;
    }
}