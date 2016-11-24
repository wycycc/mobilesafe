package com.ycc.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ycc.mobilesafe.R;

/**
 * 自定义的组合控件，它里面有两个TextView,还有一个CheckBox,还有一个View
 * Created by Administrator on 2016/11/23.
 */
public class SettingitemView extends RelativeLayout{

    private CheckBox cb_status;
    private TextView tv_desc;
    private TextView tv_title;

    private String desc_on;
    private String desc_off;

    private void  iniView(Context context){
        //把一个布局文件-->View 并且加载在SettingItemView
        View.inflate(context, R.layout.setting_item_view,this);
        cb_status = (CheckBox) this.findViewById(R.id.cb_status);
        tv_desc = (TextView) this.findViewById(R.id.tv_desc);
        tv_title = (TextView) this.findViewById(R.id.tv_title);
    }
    public SettingitemView(Context context) {
        super(context);
        iniView(context);
    }

    /**
     * 带有两个参数的构造方法，布局文件使用的时候进行调用
     * @param context
     * @param attrs
     */
    public SettingitemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniView(context);
        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.ycc.mobilesafe","title");
        desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.ycc.mobilesafe","desc_on");
        desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.ycc.mobilesafe","desc_off");
        tv_title.setText(title);

    }

    public SettingitemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView(context);
    }

    /**
     * 校验组合控件是否选中
     */
    public boolean isChecked(){
        return cb_status.isChecked();
    }

    /**
     * 设置组合控件的状态
     */
    public void setChecked(boolean checked){
        if(checked){
            setDesc(desc_on);
        }else{
            setDesc(desc_off);
        }
        cb_status.setChecked(checked);
    }

    /**
     * 设置组合控件的描述信息
     */
    public void setDesc(String text){
        tv_desc.setText(text);
    }
}
