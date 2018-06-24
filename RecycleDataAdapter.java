package com.caihang.ylyim.adapter;

import android.support.annotation.Nullable;
import com.caihang.ylyim.R;
import com.caihang.ylyim.bean.RecyclerDataEntity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;


public class RecycleDataAdapter extends BaseQuickAdapter<RecyclerDataEntity, BaseViewHolder> {

    public RecycleDataAdapter(int layoutResId, @Nullable List<RecyclerDataEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final RecyclerDataEntity item) {
        helper.setText(R.id.tv_title, item.title);
        helper.setText(R.id.tv_content, item.content);
        helper.setImageResource(R.id.iv_icon, item.itemIcon);
    }

}