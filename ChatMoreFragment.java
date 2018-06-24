package com.caihang.ylyim.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.caihang.core.base.BaseFragment;
import com.caihang.ylyim.R;
import com.caihang.ylyim.activity.ChatActivity;

public class ChatMoreFragment extends BaseFragment {
    private ImageView iv_chat_doc;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_more;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        iv_chat_doc= view.findViewById(R.id.iv_chat_doc);
    }

    @Override
    protected void initData(View view, Bundle savedInstanceState) {
        iv_chat_doc.setOnClickListener(v -> ((ChatActivity)mActivity).sendDoc());
    }
}
