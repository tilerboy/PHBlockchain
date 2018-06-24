package com.caihang.ylyim.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caihang.core.base.BaseActivity;
import com.caihang.core.utils.SPUtils;
import com.caihang.ylyim.R;
import com.caihang.ylyim.chat.ChatListener;
import com.caihang.ylyim.common.SPConstant;
import com.caihang.ylyim.service.ChatService;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CodeUtils;


public class SplashActivity extends BaseActivity {


    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        requestPermission();
    }


    private void toMain() {
        MainActivity.start(this);
    }


    private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.INTERNET, Manifest.permission.VIBRATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            if (aBoolean) {
                new Handler().postDelayed(() -> {
                    toMain();
                    finish();
                }, 3000);
            } else {
                showFailedTip("授权失败");
                finish();
            }
        });
    }

}
