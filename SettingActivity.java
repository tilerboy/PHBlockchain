package com.caihang.ylyim.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.caihang.core.base.BaseActivity;
import com.caihang.core.utils.SPUtils;
import com.caihang.ylyim.R;
import com.caihang.ylyim.adapter.RecycleDataAdapter;
import com.caihang.ylyim.bean.RecyclerDataEntity;
import com.caihang.ylyim.chat.ChatManager;
import com.caihang.ylyim.common.SPConstant;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.elastos.carrier.UserInfo;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

public class SettingActivity extends BaseActivity {

    RecyclerView rv_setting;
    private AlertDialog alertDialog;
    private UserInfo userInfo;
    private ArrayList<RecyclerDataEntity> recyclerDatas;
    private RecycleDataAdapter recycleDataAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        rv_setting = findViewById(R.id.rv_setting);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initData() {
        initTitle("设置", true);
        recyclerDatas = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rv_setting.setLayoutManager(linearLayoutManager);
        recycleDataAdapter = new RecycleDataAdapter(R.layout.item_recycle_data, recyclerDatas);
        recycleDataAdapter.setOnItemClickListener((adapter, view1, position) -> Objects.requireNonNull(recycleDataAdapter.getItem(position)).function.apply(null));
        rv_setting.setAdapter(recycleDataAdapter);
        refreshInfo();
    }

    private void refreshInfo() {
        userInfo = ChatManager.getInstance().getInfo();
        if (userInfo == null) {
            return;
        }
        recyclerDatas.clear();
        recyclerDatas.add(new RecyclerDataEntity("昵称", userInfo.getName(), R.mipmap.account, o -> {
            showEditDialog("修改昵称");
            return null;
        }, null));
        recyclerDatas.add(new RecyclerDataEntity("性别", userInfo.getGender(), R.mipmap.gender, o -> {
            showGenderDialog();
            return null;
        }, null));
        recyclerDatas.add(new RecyclerDataEntity("简介", userInfo.getDescription(), R.mipmap.profile, o -> {
            showEditDialog("修改简介");
            return null;
        }, null));
        recyclerDatas.add(new RecyclerDataEntity("邮箱", userInfo.getEmail(), R.mipmap.email, o -> {
            showEditDialog("修改邮箱");
            return null;
        }, null));
        recyclerDatas.add(new RecyclerDataEntity("手机", userInfo.getPhone(), R.mipmap.mobile, o -> {
            showEditDialog("修改手机");
            return null;
        }, null));
        recyclerDatas.add(new RecyclerDataEntity("地区", userInfo.getRegion(), R.mipmap.location, o -> {
            showEditDialog("修改地区");
            return null;
        }, null));
        recycleDataAdapter.setNewData(recyclerDatas);
    }

    private void showEditDialog(String title) {
        EditText editText = new EditText(mActivity);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        alertDialog = builder.setTitle(title).setIcon(R.mipmap.ic_launcher)
                .setMessage("请输入内容")
                .setView(editText)
                .setNegativeButton("取消", (dialog, which) -> alertDialog.dismiss())
                .setPositiveButton("确定", (dialog, which) -> {
                    String edit = editText.getText().toString();
                    if (TextUtils.isEmpty(edit)) {
                        return;
                    }
                    switch (title) {
                        case "修改昵称":
                            userInfo.setName(edit);
                            break;
                        case "修改简介":
                            userInfo.setDescription(edit);
                            break;
                        case "修改邮箱":
                            userInfo.setEmail(edit);
                            break;
                        case "修改手机":
                            userInfo.setPhone(edit);
                            break;
                        case "修改地区":
                            userInfo.setRegion(edit);
                            break;
                    }
                    ChatManager.getInstance().setInfo(userInfo);
                    refreshInfo();
                    alertDialog.dismiss();
                }).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void showGenderDialog() {
        String[] genders = new String[]{"男", "女"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setIcon(R.mipmap.ic_launcher).setTitle("请选择性别")
                .setSingleChoiceItems(genders, 0, (dialog1, which) -> {
                    userInfo.setGender(genders[which]);
                    ChatManager.getInstance().setInfo(userInfo);
                    refreshInfo();
                    dialog1.dismiss();
                }).create();
        dialog.show();
    }
}
