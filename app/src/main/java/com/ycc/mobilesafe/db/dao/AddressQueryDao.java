package com.ycc.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016/11/29.
 */
public class AddressQueryDao {
    private static String path = "/data/data/com.ycc.mobilesafe/files/address.db";
    /**
     * 号码归属地查询
     * @param phone
     * @return
     */
    public static String numberQuery(String phone){
        String address = phone;
        //把数据address.db拷贝到/data/data/包名/files/address.db

        //手机号码
        //13x 14x 15x 17x 18x
        //手机号码的正则表达式
        if(phone.matches("^1[34578]\\d{9}$")){
            SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = db.rawQuery(
                    "select location from data2 where id = (select outkey from data1 where id = ?)",
                    new String[] { phone.substring(0, 7)});

            if(cursor.moveToNext()){
                String location = cursor.getString(0);
                address = location;
            }
            cursor.close();
        }else{
            //其他号码
            switch (phone.length()) {
                //110 119
                case 3:
                    address = "匪警号码";

                    break;
                case 4:
                    address = "模拟器";
                    break;

                case 5:
                    address = "客服号码";
                    break;

                case 7:
                    address = "本地号码";
                    break;

                case 8:
                    address = "本地号码";
                    break;

                default:
                    //长途号码
                    if(phone.length()>=10&&phone.startsWith("0")){
                        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
                        Cursor  cursor = db.rawQuery(
                                "select location from data2 where area = ?",
                                new String[] { phone.substring(1, 3)});

                        if(cursor.moveToNext()){
                            String location = cursor.getString(0);
                            address = location.substring(0, location.length()-2);
                        }
                        cursor.close();

                        SQLiteDatabase dbs = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
                        Cursor  cursors = dbs.rawQuery(
                                "select location from data2 where area = ?",
                                new String[] { phone.substring(1, 4)});

                        if(cursors.moveToNext()){
                            String location = cursors.getString(0);
                            address = location.substring(0, location.length()-2);
                        }
                        dbs.close();
                    }

                    break;
            }
        }

        return address;

    }
}
