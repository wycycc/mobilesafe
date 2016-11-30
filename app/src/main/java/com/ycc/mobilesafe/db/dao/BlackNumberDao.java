package com.ycc.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ycc.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单数据库的crud业务类
 * Created by Administrator on 2016/11/30.
 */
public class BlackNumberDao {
    private BlackNumberDbOpenHelper helper;

    /**
     * 构造方法
     * @param context 上下文
     */
    public BlackNumberDao(Context context) {
        helper = new BlackNumberDbOpenHelper(context);
    }

    /**
     * 查询黑名单号码是否存在
     * @param number
     * @return
     */
    public boolean find(String number){
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from blacknumber where number=?",new String[]{number});
        if(cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 添加黑名单号码
     * @param number 黑名单号码
     * @param mode 拦截模式 1:电话拦截  2：短信拦截  3：全部拦截
     */
    public void add(String number,String mode){
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("number",number);
        values.put("mode",mode);
        db.insert("blacknumber",null,values);
        db.close();
    }

    /**
     * 修改黑名单号码的拦截模式
     * @param number 要修改的黑名单号码
     * @param mode 新的拦截模式
     */
    public void update(String number,String mode){
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode",mode);
        db.update("blacknumber",values,"number=?",new String[]{number});
        db.close();
    }
    /**
     * 删除黑名单号码
     * @param number 要删除的黑名单号码
     */
    public void delete(String number){
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete("blacknumber","number=?",new String[]{number});
        db.close();
    }
    /**
     * 查询全部黑名单号码是否存在
     * @return
     */
    public List<BlackNumberInfo> findAll(){
        List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber",null);
        while(cursor.moveToNext()){
           BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setNumber(number);
            info.setMode(mode);
            result.add(info);
        }
        cursor.close();
        db.close();
        return result;
    }

}
