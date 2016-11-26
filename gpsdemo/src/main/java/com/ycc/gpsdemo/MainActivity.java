package com.ycc.gpsdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {

    //用到位置服务
    private LocationManager lm;
    private MyLocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        //得到定位方式
        List<String> provider = lm.getAllProviders();
        for (String l : provider) {
            System.out.println("-----------" + l);
        }
        listener = new MyLocationListener();
        //注册监听位置服务
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //注册监听位置服务
        //给位置提供者设置条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String proveder = lm.getBestProvider(criteria,true);
        lm.requestLocationUpdates(proveder, 0, 0, listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消监听位置服务
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.removeUpdates(listener);
        listener = null;
    }

    class MyLocationListener implements LocationListener{

        /**
         * 当位置发生改变时候回调
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            String longitude = "经度:" + location.getLongitude();
            String latitude = "纬度:" + location.getLatitude();
            String accuracy = "精确度:" + location.getAccuracy();
            TextView textView = new TextView(MainActivity.this);
            textView.setText(longitude+"\n"+latitude+"\n"+accuracy);
            setContentView(textView);
        }

        /**
         * 当状态发生改变的时候回调 开启--关闭；关闭--开启
         * @param s
         * @param i
         * @param bundle
         */
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        /**
         * 某一个位置提供者可以使用了
         * @param s
         */
        @Override
        public void onProviderEnabled(String s) {

        }

        /**
         * 栽一个位置提供者不可以使用了
         * @param s
         */
        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
