package com.ycc.mobilesafe;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ycc.mobilesafe.domain.AppInfo;
import com.ycc.mobilesafe.engine.AppInfoProvider;
import com.ycc.mobilesafe.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AppManagerActivity";
    private TextView tv_avail_rom;
    private TextView tv_avail_sd;

    private ListView lv_app_manager;
    private LinearLayout ll_loading;

    /**
     * 所有的应用程序包信息
     */
    private List<AppInfo> appInfos;

    /**
     * 用户应用程序的集合
     */
    private List<AppInfo> userAppInfos;

    /**
     * 系统应用程序的集合
     */
    private List<AppInfo> sysAppInfos;

    /**
     * 当前程序信息的状态
     */
    private TextView tv_status;

    /**
     * 弹出悬浮窗体
     */
    private PopupWindow popupWindow;

    private LinearLayout ll_start;//开启
    private LinearLayout ll_share;//分享
    private LinearLayout ll_uninstall;//卸载

    /**
     * 被点击的条目
     */
    private  AppInfo appInfo;
    private AppManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
        tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);

        lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_status = (TextView) findViewById(R.id.tv_status);

        long sdsize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        long romsize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
        tv_avail_sd.setText("SD卡可用空间:"+ Formatter.formatFileSize(this,sdsize));
        tv_avail_rom.setText("内存可用空间:"+Formatter.formatFileSize(this,romsize));

        //设置数据
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
                userAppInfos = new ArrayList<AppInfo>();
                sysAppInfos = new ArrayList<AppInfo>();
                for(AppInfo info:appInfos){
                    if(info.isUserApp()){
                        userAppInfos.add(info);
                    }else{
                        sysAppInfos.add(info);
                    }
                }
                //加载listview的数据适配器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lv_app_manager.setAdapter(new AppManagerAdapter());
                        ll_loading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();

        //给listview注册一个滚动的监听器
        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            //滚动的时候调用的方法
            //firstVisibleItem 第一个可见条目在listview集合里的位置
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopupWindow();
                if(userAppInfos!=null&&sysAppInfos!=null){
                    if(firstVisibleItem>userAppInfos.size()){
                        tv_status.setText("系统程序:"+sysAppInfos.size()+"个");
                    }else{
                        tv_status.setText("用户程序:"+userAppInfos.size()+"个");
                    }
                }
            }
        });
        /**
         * 设置listview的点击事件
         */
        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo appInfo;
                if(position==0){
                    return;
                }else if(position==(userAppInfos.size()+1)){
                    return;
                }else if(position<=userAppInfos.size()){
                    //用户程序
                    int newposition = position - 1;
                    appInfo = userAppInfos.get(newposition);
                }else{
                    //系统程序
                    int newposition = position - 1 - userAppInfos.size() - 1;
                    appInfo = sysAppInfos.get(newposition);
                }
                System.out.println(appInfo.getPackname());
                dismissPopupWindow();

                View contentView = View.inflate(getApplicationContext(),R.layout.popup_app_item,null);
                ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
                ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
                ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);

                ll_start.setOnClickListener(AppManagerActivity.this);
                ll_share.setOnClickListener(AppManagerActivity.this);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);

                //TextView contentView = new TextView(getApplicationContext());
                //contentView.setText(appInfo.getPackname());
                //contentView.setTextColor(Color.BLACK);

                popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.WRAP_CONTENT,-2);
                //动画效果的播放必须要求窗体有背景颜色
                //透明颜色也是颜色
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[] location = new int[2];
                view.getLocationInWindow(location);
                //在代码里设置的宽高长单位为像素
                int dip = 60;
                int px = DensityUtils.dip2px(getApplicationContext(),dip);
                popupWindow.showAtLocation(parent, Gravity.LEFT|Gravity.TOP,px,location[1]);
                ScaleAnimation sa = new ScaleAnimation(0.3f,1.0f,0.3f,1.0f, Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0.5f);
                sa.setDuration(300);
                AlphaAnimation aa = new AlphaAnimation(0.5f,1.0f);
                aa.setDuration(300);
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(aa);
                set.addAnimation(sa);
                contentView.startAnimation(set);
            }
        });
    }

    private void dismissPopupWindow() {
        //把旧的弹出窗体关闭掉
        if(popupWindow!=null&&popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }


    private class AppManagerAdapter extends BaseAdapter{

        //控制listview有多少个条目
        @Override
        public int getCount() {
            //return appInfos.size();
            return userAppInfos.size()+1+sysAppInfos.size()+1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /**TextView tv = new TextView(getApplicationContext());
            tv.setText(appInfos.get(position).toString());
            return tv;*/

            if(position==0){//显示的是用户程序有多少个小标签
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户程序:"+userAppInfos.size()+"个");
                return tv;
            }else if(position==userAppInfos.size()+1){
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统程序:"+sysAppInfos.size()+"个");
                return tv;
            }else if(position<=userAppInfos.size()){//用户程序
                int newposition = position - 1;//因为多了一个textView的文本占用了位置
                appInfo = userAppInfos.get(newposition);
            }else{
                //系统程序
                int newposition = position - 1 - userAppInfos.size() - 1;
                appInfo = sysAppInfos.get(newposition);
            }

            View view;
            ViewHolder holder;

            /**if(position<userAppInfos.size()){//这些位置是留给用户程序显示的
                appInfo = userAppInfos.get(position);
            }else{//留给系统程序的
                int newposition = position - userAppInfos.size();
                appInfo = sysAppInfos.get(newposition);
            }*/
            if(convertView!=null && convertView instanceof RelativeLayout){//不仅需要检查是否为空，还要判断是否合适的类型去复用
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else{
                view = View.inflate(getApplicationContext(),R.layout.list_item_appinfo,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tv_location = (TextView) view.findViewById(R.id.tv_app_location);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
                view.setTag(holder);
            }

            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getName());
            if(appInfo.isInRom()){
                holder.tv_location.setText("手机内存");
            }else{
                holder.tv_location.setText("外部存储");
            }

            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    static class ViewHolder{
        TextView tv_name;
        TextView tv_location;
        ImageView iv_icon;
    }

    /**
     * 获取某个目录的可用空间
     * @param path
     * @return
     */
    private long getAvailSpace(String path){
        StatFs statf = new StatFs(path);
        statf.getBlockCount();//获取分区的个数
        long size = statf.getBlockSize();//获取分区的大小
        long count = statf.getAvailableBlocks();//获取可用的区块的个数
        return size*count;
    }

    @Override
    protected void onDestroy() {
        dismissPopupWindow();
        super.onDestroy();
    }

    /**
     * 布局对应的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        dismissPopupWindow();
        switch (v.getId()){
            case R.id.ll_share:
                Log.i(TAG,"分享:"+appInfo.getName());
                shareApplication();
                break;
            case R.id.ll_start:
                Log.i(TAG,"启动:"+appInfo.getName());
                startApplication();
                break;
            case R.id.ll_uninstall:
                if (appInfo.isUserApp()) {
                    Log.i(TAG, "卸载：" + appInfo.getName());
                    uninstallApplication();
                }else{
                    Toast.makeText(this, "系统应用只有获取root权限才可以卸载", 0).show();
                    //Runtime.getRuntime().exec("");
                }
                break;
        }
    }

    /**
     * 分享一个应用程序
     */
    private void shareApplication() {
        // Intent { act=android.intent.action.SEND typ=text/plain flg=0x3000000 cmp=com.android.mms/.ui.ComposeMessageActivity (has extras) } from pid 256
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件,名称叫："+appInfo.getName());
        startActivity(intent);
    }

    /**
     * 卸载应用
     */
    private void uninstallApplication() {
        // <action android:name="android.intent.action.VIEW" />
        // <action android:name="android.intent.action.DELETE" />
        // <category android:name="android.intent.category.DEFAULT" />
        // <data android:scheme="package" />
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + appInfo.getPackname()));
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        // 刷新界面。
        fillData();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 开启应用程序
     */
    private void startApplication() {
        //查询这个应用程序的入口activity,把它开启起来
        PackageManager pm = getPackageManager();
        /**Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        //查询出来了所有手机上具有启动能力的activity
        List<ResolveInfo> infos = pm.queryIntentActivities(intent,PackageManager.GET_INTENT_FILTERS);*/
        Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
        if(intent!=null){
            startActivity(intent);
        }else{
            Toast.makeText(this,"不能启动当前应用",Toast.LENGTH_LONG).show();
        }

    }

    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
                userAppInfos = new ArrayList<AppInfo>();
                sysAppInfos = new ArrayList<AppInfo>();
                for (AppInfo info : appInfos) {
                    if (info.isUserApp()) {
                        userAppInfos.add(info);
                    } else {
                        sysAppInfos.add(info);
                    }
                }
                // 加载listview的数据适配器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter == null) {
                            adapter = new AppManagerAdapter();
                            lv_app_manager.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        ll_loading.setVisibility(View.INVISIBLE);
                    }
                });
            };
        }.start();
    }

}
