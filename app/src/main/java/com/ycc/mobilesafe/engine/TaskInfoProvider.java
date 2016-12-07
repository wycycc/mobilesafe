package com.ycc.mobilesafe.engine;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.ycc.mobilesafe.R;
import com.ycc.mobilesafe.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供手机里面的进程信息
 * @author Administrator
 *
 */
public class TaskInfoProvider {
    /**
     * 获取所有的进程信息
     * @param context 上下文
     * @return
     */
    public static List<TaskInfo> getTaskInfos(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        for(RunningAppProcessInfo processInfo : processInfos){
            TaskInfo taskInfo = new TaskInfo();
            //应用程序的包名。
            String packname = processInfo.processName;
            taskInfo.setPackname(packname);
            MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{processInfo.pid});
            long memsize = memoryInfos[0].getTotalPrivateDirty()*1024l;
            taskInfo.setMemsize(memsize);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
                Drawable icon = applicationInfo.loadIcon(pm);
                taskInfo.setIcon(icon);
                String name = applicationInfo.loadLabel(pm).toString();
                taskInfo.setName(name);
                if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) == 0){
                    //用户进程
                    taskInfo.setUserTask(true);
                }else{
                    //系统进程
                    taskInfo.setUserTask(false);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                //taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
                taskInfo.setName(packname);
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
