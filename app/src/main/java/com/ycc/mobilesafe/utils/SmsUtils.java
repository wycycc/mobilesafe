package com.ycc.mobilesafe.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
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
     * 备份用户的短信
     * @param context 上下文
     * @param pd 进度条对话框
     */
    public static void backupSms(Context context, ProgressDialog pd) throws IOException, InterruptedException {
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
        pd.setMax(max);
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
            pd.setProgress(process);
        }
        cursor.close();
        serializer.endTag(null,"smss");
        serializer.endDocument();
        fos.close();
    }
}
