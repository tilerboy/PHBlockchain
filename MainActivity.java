package com.caihang.ylyim.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.caihang.core.base.BaseActivity;
import com.caihang.ylyim.R;
import com.caihang.ylyim.adapter.ViewPagerAdapter;
import com.caihang.ylyim.chat.ChatListener;
import com.caihang.ylyim.fragment.ContactFragment;
import com.caihang.ylyim.fragment.MainFragment;
import com.caihang.ylyim.fragment.MineFragment;
import com.caihang.ylyim.service.ChatService;

public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        navigation = findViewById(R.id.navigation);
        viewPager = findViewById(R.id.viewpager);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void initData() {
        bindService(new Intent(mActivity, ChatService.class), connection, BIND_AUTO_CREATE);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChatService.MyBinder binder = (ChatService.MyBinder) service;
            ChatService chatService = binder.getService();
            chatService.init(new ChatListener.InitSuccessListener() {
                @Override
                public void initSuccess() {
                    showSuccessTip("聊天服务初始化成功");
                    runOnUiThread(() -> initContent());
                }

                @Override
                public void initFailure(Exception e) {
                    showFailedTip("聊天服务初始化失败");
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void initContent() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = navigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MainFragment.newInstance("首页"));
        adapter.addFragment(ContactFragment.newInstance("联系人"));
        adapter.addFragment(MineFragment.newInstance("我的"));
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
