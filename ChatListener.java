package com.caihang.ylyim.chat;

import com.caihang.ylyim.data.ChatMsg;

public interface ChatListener {

    interface InitSuccessListener {

        void initSuccess();

        void initFailure(Exception e);
    }

    interface SendMessageListener {

        void sendMessageSuccess(ChatMsg chatMsg);

        void sendMessageFailure(Exception e);
    }

    interface ReceiveMessageListener {

        void receiveMessage(ChatMsg chatMsg);

    }

}
