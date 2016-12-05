package com.ycc.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 短信的工具类
 * Created by Administrator on 2016/12/5.
 */
public class SmsUtils {

    /**
     * 备份短信的回调接口
     */
    public interface BackUpCallBack{
        /**
         * 开始备份时，设置进度的最大值
         * @param max
         */
        public void beforeBackup(int max);

        /**
         * 备份过程中，增加进度
         * @param progress
         */
        public void onSmsBackup(int progress);
    }

    /**
     * 备份用户的短信
     * @param callBack
     */
    public static void backupSms(Context context, BackUpCallBack callBack) throws IOException, InterruptedException {
        ContentResolver resolver = context.getContentResolver();
        File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
        FileOutputStream fos = new FileOutputStream(file);
        //把用户的短信一条一条的读出来，按一定格式写到文件里
        XmlSerializer serializer = Xml.newSerializer();//获取xml文件的生成器（序列化）
        //初始化生成器
        serializer.setOutput(fos,"utf-8");
        serializer.startDocument("utf-8",true);
        serializer.startTag(null,"smss");
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri,new String[]{"body","address","type","date"},null,null,null);
        //开始备份时，设置进度条的最大值
        int max = cursor.getCount();
        //pd.setMax(max);
        callBack.beforeBackup(max);
        serializer.attribute(null,"max",max+"");
        int process = 0;
        while(cursor.moveToNext()){
            Thread.sleep(500);
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);
            serializer.startTag(null,"sms");
            serializer.startTag(null,"body");
            serializer.text(body);
            serializer.endTag(null,"body");

            serializer.startTag(null,"address");
            serializer.text(address);
            serializer.endTag(null,"address");

            serializer.startTag(null,"type");
            serializer.text(type);
            serializer.endTag(null,"type");

            serializer.startTag(null,"date");
            serializer.text(date);
            serializer.endTag(null,"date");
            serializer.endTag(null,"sms");
            //备份过程中增加进度
            process++;
            //pd.setProgress(process);
            callBack.onSmsBackup(process);
        }
        cursor.close();
        serializer.endTag(null,"smss");
        serializer.endDocument();
        fos.close();
    }

    /**
     * 还源短信
     * @param context
     * @param flag
     */
    public static void restoreSms(Context context,boolean flag){
        Uri uri = Uri.parse("content://sms/");
        if(flag){
            context.getContentResolver().delete(uri,null,null);
        }
        //1.读取sd卡上的xml文件
        //Xml.newPullParser();
        ContentValues values = new ContentValues();
        values.put("body","aaa");
        values.put("date","12121212122");
        values.put("type","1");
        values.put("address","5556");
        context.getContentResolver().insert(uri,values);
        //2.读取max

        //3.读取每一条短信信息 body date type address

        //4.把短信插入到系统短信应用
    }
}
