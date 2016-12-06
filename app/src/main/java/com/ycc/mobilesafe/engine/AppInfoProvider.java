package com.ycc.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.ycc.mobilesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务方法 提供手机里面安装的所有应用程序信息
 * Created by Administrator on 2016/12/6.
 */
public class AppInfoProvider {
    /**
     * 获取所有的安装的应用程序信息
     * @param context 上下文
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context){
        PackageManager pm = context.getPackageManager();
        //所有的安装在系统上的应用程序包信息
        List<PackageInfo> packInfos =  pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        for(PackageInfo packInfo:packInfos){
            AppInfo info = new AppInfo();
            //packInfo 相当于一个应用程序apk包的清单文件
            String packname = packInfo.packageName;
            Drawable icon = packInfo.applicationInfo.loadIcon(pm);
            String name = packInfo.applicationInfo.loadLabel(pm).toString();

            int flags = packInfo.applicationInfo.flags;//应用程序信息的标记,相当于用户提交的答卷
            if((flags& ApplicationInfo.FLAG_SYSTEM)==0){
                //用户程序
                info.setUserApp(true);
            }else{
                //系统程序
                info.setUserApp(false);
            }
            if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
                //手机的内存
                info.setInRom(true);
            }else{
                //手机外存
                info.setInRom(false);
            }
            info.setPackname(packname);
            info.setIcon(icon);
            info.setName(name);
            appInfos.add(info);
        }
        return appInfos;
    }
}
