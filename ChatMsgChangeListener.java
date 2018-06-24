package com.caihang.ylyim.chat;

import com.caihang.ylyim.data.ChatMsg;

public interface ChatMsgChangeListener {

    void addMessage(ChatMsg chatMsg);

    void delMessage(ChatMsg chatMsg);

    void updateMessage(ChatMsg chatMsg);

    void recvPresence(ChatMsg chatMsg);
}
