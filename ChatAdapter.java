package com.caihang.ylyim.adapter;

import com.caihang.ylyim.R;
import com.caihang.ylyim.bean.ChatFileBean;
import com.caihang.ylyim.common.ChatConstant;
import com.caihang.ylyim.data.ChatMsg;
import com.caihang.ylyim.util.FileSizeUtil;
import com.caihang.ylyim.util.TimeUtil;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public class ChatAdapter extends BaseMultiItemQuickAdapter<ChatMsg, BaseViewHolder> {

    public ChatAdapter(List<ChatMsg> data) {
        super(data);
        addItemType(ChatConstant.TEXT_SEND, R.layout.item_chat_text_send);
        addItemType(ChatConstant.TEXT_RECV, R.layout.item_chat_text_recv);
        addItemType(ChatConstant.WORD_SEND, R.layout.item_chat_word_send);
        addItemType(ChatConstant.WORD_RECV, R.layout.item_chat_word_recv);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatMsg chatMsg) {
        try {
            switch (chatMsg.getItemType()) {
                case ChatConstant.TEXT_SEND:
                    helper.setText(R.id.tv_content, chatMsg.getContent());
                    helper.setText(R.id.tv_time, TimeUtil.getChatTime(chatMsg.getTime()));
                    break;
                case ChatConstant.TEXT_RECV:
                    helper.setText(R.id.tv_content, chatMsg.getContent());
                    helper.setText(R.id.tv_time, TimeUtil.getChatTime(chatMsg.getTime()));
                    break;
                case ChatConstant.WORD_SEND:
                    ChatFileBean chatSendFileBean = new Gson().fromJson(chatMsg.getContent(), ChatFileBean.class);
                    helper.setText(R.id.tv_name, chatSendFileBean.getName());
                    helper.setText(R.id.tv_size, FileSizeUtil.FormetFileSize(chatSendFileBean.getSize()));
                    helper.setText(R.id.tv_time, TimeUtil.getChatTime(chatMsg.getTime()));
                    break;
                case ChatConstant.WORD_RECV:
                    ChatFileBean chatRecvFileBean = new Gson().fromJson(chatMsg.getContent(), ChatFileBean.class);
                    helper.setText(R.id.tv_name, chatRecvFileBean.getName());
                    helper.setText(R.id.tv_size, FileSizeUtil.FormetFileSize(chatRecvFileBean.getSize()));
                    helper.setText(R.id.tv_time, TimeUtil.getChatTime(chatMsg.getTime()));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
