package com.caihang.ylyim.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.caihang.core.base.BaseFragment;
import com.caihang.ylyim.R;
import com.caihang.ylyim.activity.ChatActivity;
import com.caihang.ylyim.activity.UserInfoActivity;
import com.caihang.ylyim.adapter.ContactAdapter;
import com.caihang.ylyim.chat.ChatManager;
import com.caihang.ylyim.chat.SimpleChatFriendChangeListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.elastos.carrier.FriendInfo;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends BaseFragment {

    private static final int REQUEST_CODE = 10001;
    private RecyclerView rv_contacts;
    private ContactAdapter contactAdapter;
    private List<FriendInfo> data;
    private RefreshLayout refreshLayout;

    public static ContactFragment newInstance(String title) {
        Bundle args = new Bundle();
        ContactFragment fragment = new ContactFragment();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        rv_contacts = view.findViewById(R.id.rv_contacts);
        refreshLayout = view.findViewById(R.id.refreshLayout);
    }

    @Override
    protected void initData(View view, Bundle savedInstanceState) {
        setTitle(getArguments().getString("title"));
        setRightImg(R.mipmap.add_user, v -> toScanFriendCode());
        data = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        rv_contacts.setLayoutManager(linearLayoutManager);
        contactAdapter = new ContactAdapter(R.layout.item_contact, data);
        rv_contacts.setAdapter(contactAdapter);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mActivity));
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(refreshLayout -> refreshFriends());
        ChatManager.getInstance().addChatFriendChangeListener(new SimpleChatFriendChangeListener() {
            @Override
            public void updateFriend(FriendInfo friendInfo) {
                mActivity.runOnUiThread(() -> refreshFriends());
            }
        });
        refreshFriends();
    }

    private void refreshFriends() {
        try {
            List<FriendInfo> friends = ChatManager.getInstance().getFiends();
            if (friends != null && friends.size() > 0) {
                contactAdapter.setNewData(friends);
                contactAdapter.setOnItemClickListener((adapter, view1, position) -> UserInfoActivity.start(mActivity, friends.get(position).getUserId()));
            } else {
                contactAdapter.setNewData(new ArrayList<>());
            }
            refreshLayout.finishRefresh(true);
        } catch (Exception e) {
            refreshLayout.finishRefresh(false);
            e.printStackTrace();
        }
    }

    private void toScanFriendCode() {
        Intent intent = new Intent(mActivity, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    showSuccessTip("解析结果:" + result);
                    if (!TextUtils.isEmpty(result)) {
                        addFriend(result);
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    showFailedTip("解析二维码失败");
                }
            }
        }
    }

    private void addFriend(String address) {
        ChatManager.getInstance().addFriend(address);
    }

}
