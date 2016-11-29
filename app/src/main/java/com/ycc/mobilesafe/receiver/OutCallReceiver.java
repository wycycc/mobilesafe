package com.ycc.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ycc.mobilesafe.db.dao.AddressQueryDao;

public class OutCallReceiver extends BroadcastReceiver {
    public OutCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // 得到要打出去的电话号码
        String phone = getResultData();
        //根据号码查询号码归属地
        String address = AddressQueryDao.numberQuery(phone);
        Toast.makeText(context,address,Toast.LENGTH_LONG).show();
    }
}
