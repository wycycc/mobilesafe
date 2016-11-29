package com.ycc.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2016/11/29.
 */
public class ServiceUtils {

    /**
     * 校验某个服务是否还在运行
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName){
        //它不只可以管理Activity，还可以管理Service
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo info:infos){
            //得到正在运行的服务的名字
            String name = info.service.getClassName();
            if(serviceName.equals(name)){
                return true;
            }
        }
        return false;
    }
}
