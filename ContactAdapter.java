package com.caihang.ylyim.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.caihang.ylyim.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;

import java.util.List;

public class ContactAdapter extends BaseQuickAdapter<FriendInfo, BaseViewHolder> {

    public ContactAdapter(int layoutResId, @Nullable List<FriendInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendInfo item) {
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_desc, item.getDescription());
        helper.setImageResource(R.id.iv_avatar, R.mipmap.ic_launcher);
        if ("女".equals(item.getGender())) {
            Glide.with(mContext).load(R.mipmap.girl).apply(RequestOptions.bitmapTransform(new CircleCrop())).into((ImageView) helper.getView(R.id.iv_avatar));
        } else {
            Glide.with(mContext).load(R.mipmap.boy).apply(RequestOptions.bitmapTransform(new CircleCrop())).into((ImageView) helper.getView(R.id.iv_avatar));
        }
        if (item.getConnectionStatus() == ConnectionStatus.Connected) {
            helper.setText(R.id.tv_presence, "[在线]");
        } else {
            helper.setText(R.id.tv_presence, "[离线]");
        }
    }
}
