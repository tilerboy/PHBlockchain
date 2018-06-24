package com.caihang.ylyim.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.caihang.core.base.BaseActivity;
import com.caihang.ylyim.R;
import com.caihang.ylyim.adapter.RecycleDataAdapter;
import com.caihang.ylyim.bean.RecyclerDataEntity;
import com.caihang.ylyim.chat.ChatManager;

import org.elastos.carrier.FriendInfo;

import java.util.ArrayList;
import java.util.Objects;

public class UserInfoActivity extends BaseActivity {

    private RecyclerView rv_content;
    private ImageView iv_avator;
    private String userId;
    private AlertDialog alertDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initView() {
        rv_content = findViewById(R.id.rv_content);
        iv_avator = findViewById(R.id.iv_avator);
    }

    @Override
    protected void initData() {
        userId = getIntent().getStringExtra("userId");
        setRightText("删除", v -> showDelDialog());
        FriendInfo friendInfo = ChatManager.getInstance().getFiend(userId);
        if (friendInfo == null) {
            return;
        }
        initTitle(friendInfo.getName(), true);
        if ("女".equals(friendInfo.getGender())) {
            Glide.with(mActivity).load(R.mipmap.girl).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(iv_avator);
        } else {
            Glide.with(mActivity).load(R.mipmap.boy).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(iv_avator);
        }
        ArrayList<RecyclerDataEntity> recyclerDatas = new ArrayList<>();
        recyclerDatas.add(new RecyclerDataEntity("简介", friendInfo.getDescription(), R.mipmap.profile, null, null));
        recyclerDatas.add(new RecyclerDataEntity("邮箱", friendInfo.getEmail(), R.mipmap.email, null, null));
        recyclerDatas.add(new RecyclerDataEntity("手机", friendInfo.getPhone(), R.mipmap.mobile, null, null));
        recyclerDatas.add(new RecyclerDataEntity("地区", friendInfo.getRegion(), R.mipmap.location, null, null));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rv_content.setLayoutManager(linearLayoutManager);
        final RecycleDataAdapter recycleDataAdapter = new RecycleDataAdapter(R.layout.item_recycle_data, recyclerDatas);
        rv_content.setAdapter(recycleDataAdapter);
    }

    private void showDelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        alertDialog = builder.setTitle("警告").setIcon(R.mipmap.ic_launcher)
                .setMessage("确定删除好友？")
                .setNegativeButton("取消", (dialog, which) -> alertDialog.dismiss())
                .setPositiveButton("确定", (dialog, which) -> {
                    removeFriend(userId);
                    alertDialog.dismiss();
                }).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static void start(Context context, String userId) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    public void send(View view) {
        ChatActivity.start(mActivity, userId);
    }

    private void removeFriend(String userId) {
        ChatManager.getInstance().delFriend(userId);
        finish();
    }
}
