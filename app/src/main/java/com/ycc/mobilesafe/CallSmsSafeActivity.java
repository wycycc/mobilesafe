package com.ycc.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ycc.mobilesafe.db.dao.BlackNumberDao;
import com.ycc.mobilesafe.domain.BlackNumberInfo;

import java.util.List;

public class CallSmsSafeActivity extends Activity {

    private static final String TAG = "CallSmsSafeActivity";
    private ListView lv_callsms_safe;
    private List<BlackNumberInfo> infos;
    private BlackNumberDao dao;
    private CallSmsSafeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);
        lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
        dao = new BlackNumberDao(this);
        infos = dao.findAll();
        adapter = new CallSmsSafeAdapter();
        lv_callsms_safe.setAdapter(adapter);
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
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
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
                holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
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
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("确定要删除这条记录么");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //System.out.println("删除："+position);
                            //删除数据库内容
                            dao.delete(infos.get(position).getNumber());
                            //更新界面
                            infos.remove(position);
                            //通知listview数据适配更新
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            });
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
        ImageView iv_delete;
    }

    private EditText et_blacknumber;
    private CheckBox cb_phone;
    private CheckBox cb_sms;
    private Button bt_ok;
    private Button bt_cancel;

    public void addBlackNumber(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View contentView = View.inflate(this,R.layout.dialog_add_blacknumber,null);
        et_blacknumber = (EditText) contentView.findViewById(R.id.et_blacknumber);
        cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
        cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
        bt_ok = (Button) contentView.findViewById(R.id.ok);
        bt_cancel = (Button) contentView.findViewById(R.id.cancel);
        dialog.setView(contentView,0,0,0,0);
        dialog.show();
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String blacknumber = et_blacknumber.getText().toString().trim();
                if(TextUtils.isEmpty(blacknumber)){
                    Toast.makeText(getApplicationContext(),"黑名单号码不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                String mode = "3";
                if(cb_phone.isChecked()&& cb_sms.isChecked()){
                    //全部拦截
                    mode = "3";
                }else if(cb_phone.isChecked()){
                    //电话拦截
                    mode = "1";
                }else if(cb_sms.isChecked()){
                    mode = "2";
                }else{
                    Toast.makeText(getApplicationContext(),"请选择拦截模式",Toast.LENGTH_LONG).show();
                    return;
                }
                //数据被加到数据库
                dao.add(blacknumber,mode);
                //更新listview里面的内容
                BlackNumberInfo info = new BlackNumberInfo();
                info.setMode(mode);
                info.setNumber(blacknumber);
                infos.add(0,info);
                //通知listview数据适配器更新了
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }
}
