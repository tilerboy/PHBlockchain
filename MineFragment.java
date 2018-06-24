package com.caihang.ylyim.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.caihang.core.base.BaseFragment;
import com.caihang.core.utils.SPUtils;
import com.caihang.ylyim.R;
import com.caihang.ylyim.activity.SettingActivity;
import com.caihang.ylyim.adapter.RecycleDataAdapter;
import com.caihang.ylyim.bean.RecyclerDataEntity;
import com.caihang.ylyim.chat.ChatManager;
import com.caihang.ylyim.common.SPConstant;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.elastos.carrier.UserInfo;

import java.util.ArrayList;
import java.util.Objects;


public class MineFragment extends BaseFragment {
    ImageView iv_avator;
    TextView tv_name;
    RecyclerView rv_content;
    private AlertDialog alertDialog;
    private UserInfo userInfo;
    private RecycleDataAdapter recycleDataAdapter;
    private ArrayList<RecyclerDataEntity> recyclerDatas;

    public static MineFragment newInstance(String title) {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        iv_avator = view.findViewById(R.id.iv_avator);
        tv_name = view.findViewById(R.id.tv_name);
        rv_content = view.findViewById(R.id.rv_content);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initData(View view, Bundle savedInstanceState) {
        setTitle(getArguments().getString("title"));
        recyclerDatas = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rv_content.setLayoutManager(linearLayoutManager);
        recycleDataAdapter = new RecycleDataAdapter(R.layout.item_recycle_data, recyclerDatas);
        recycleDataAdapter.setOnItemClickListener((adapter, view1, position) -> Objects.requireNonNull(recycleDataAdapter.getItem(position)).function.apply(null));
        rv_content.setAdapter(recycleDataAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInfo();
    }

    private void refreshInfo() {
        userInfo = ChatManager.getInstance().getInfo();
        if (userInfo == null) {
            return;
        }
        tv_name.setText(userInfo.getName());
        if ("女".equals(userInfo.getGender())) {
            Glide.with(mActivity).load(R.mipmap.girl).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(iv_avator);
        } else {
            Glide.with(mActivity).load(R.mipmap.boy).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(iv_avator);
        }
        recyclerDatas.clear();
        recyclerDatas.add(new RecyclerDataEntity("设置", "", R.mipmap.setting, o -> {
            startActivity(new Intent(mActivity, SettingActivity.class));
            return null;
        }, null));
        recyclerDatas.add(new RecyclerDataEntity("二维码", "", R.mipmap.code, o -> {
            showCodeDialog();
            return null;
        }, null));
        recycleDataAdapter.setNewData(recyclerDatas);
    }

    private void showCodeDialog() {
        String textContent = SPUtils.getInstance().getString(SPConstant.MY_ADD);
        Bitmap mBitmap = CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        ImageView imageView = new ImageView(mActivity);
        imageView.setImageBitmap(mBitmap);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        alertDialog = builder.setTitle("二维码").setIcon(R.mipmap.ic_launcher)
                .setMessage("扫我添加好友")
                .setView(imageView)
                .setPositiveButton("关闭", (dialog, which) -> alertDialog.dismiss()).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}
