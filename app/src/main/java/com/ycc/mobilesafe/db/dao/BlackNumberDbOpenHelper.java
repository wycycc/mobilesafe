package com.ycc.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/11/30.
 */
public class BlackNumberDbOpenHelper extends SQLiteOpenHelper{

    /**
     * 数据库创建的构造方法，数据库的名称blacknumber.db
     * @param context
     *
     */
    public BlackNumberDbOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    //初始化数据库的表结构
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id integer primary key autoincrement,number varchar(20),mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
