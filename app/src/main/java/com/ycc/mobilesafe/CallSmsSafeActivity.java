package com.ycc.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ycc.mobilesafe.db.dao.BlackNumberDao;
import com.ycc.mobilesafe.domain.BlackNumberInfo;

import java.util.List;

public class CallSmsSafeActivity extends Activity {

    private static final String TAG = "CallSmsSafeActivity";
    private ListView lv_callsms_safe;
    private List<BlackNumberInfo> infos;
    private BlackNumberDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);
        lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
        dao = new BlackNumberDao(this);
        infos = dao.findAll();
        lv_callsms_safe.setAdapter(new CallSmsSafeAdapter());
    }

    private class CallSmsSafeAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        //有多少条目被显示，这个方法就会被调用多少次
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view;
            ViewHolder holder;
            //1.减少内存中view对象创建的个数
            if(convertView==null){
                //把一个布局文件转化成view对象
                //Log.i(TAG,"position:"+position+"convertView:"+convertView);
                view = View.inflate(getApplicationContext(),R.layout.list_item_callsms,null);
                //2.减少子孩子查询的次数  //内存中对象的地址
                //Log.i(TAG,"position:"+position+"view:"+view);
                holder = new ViewHolder();
                holder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
                holder.tv_mode = (TextView) view.findViewById(R.id.tv_black_mode);
                //当孩子生出来的时候找到他们的引用，存放在记事本里，放在父亲的口袋里
                view.setTag(holder);
            }else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            //2.减少子孩子查询的次数  //内存中对象的地址
            //Log.i(TAG,"position:"+position+"view:"+view);
            holder.tv_number.setText(infos.get(position).getNumber());
            String mode = infos.get(position).getMode();
            if("1".equals(mode)){
                holder.tv_mode.setText("电话拦截");
            }else if("2".equals(mode)){
                holder.tv_mode.setText("短信拦截");
            }else {
                holder.tv_mode.setText("全部拦截");
            }
            return view;
        }
    }

    /**
     * view对象的容器
     * 记录孩子的内存地址
     * 相当于一个记事本
     */
    class ViewHolder{
        TextView tv_number;
        TextView tv_mode;
    }
}
