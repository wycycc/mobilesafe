package com.ycc.readcontact;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private ListView select_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        select_contact = (ListView) findViewById(R.id.select_contact);
        List<Map<String,String>> data = getContactInfo();
        select_contact.setAdapter(new SimpleAdapter(this,data,R.layout.contact_item_view,new String[]{"name","phone"},new int[]{R.id.tv_name,R.id.tv_phone}));
    }

    /**
     * 读取手机里面的联系人
     * @return
     */
    private List<Map<String,String>> getContactInfo() {
        //把所有的联系人
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        /**
         * 得到一个内容解析器
         */
        ContentResolver resolver = getContentResolver();
        //raw_contacts uri
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");

        Cursor cursor = resolver.query(uri,new String[]{"contact_id"},null,null,null);
        while(cursor.moveToNext()){
            String contact_id = cursor.getString(0);
            if(contact_id!=null){
                //具体的某一个联系人
                Map<String,String> map = new HashMap<String,String>();
                Cursor dataCursor = resolver.query(uriData,new String[]{"data1","mimetype"},"contact_id=?",new String[]{contact_id},null);
                while(dataCursor.moveToNext()){
                    String data1 = dataCursor.getString(0);
                    String mimetype = dataCursor.getString(1);
                    System.out.println("data1="+data1+",mimetype="+mimetype);
                    if("vnd.android.cursor.item/name".equals(mimetype)){
                        //联系人的姓名
                        map.put("name",data1);
                    }else if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                        //联系人的电话号码
                        map.put("phone",data1);
                    }
                }
                list.add(map);
                dataCursor.close();
            }
        }
        cursor.close();
        return list;
    }
}
