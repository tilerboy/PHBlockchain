package com.caihang.ylyim.data.source.remote;

import android.support.annotation.NonNull;

import com.caihang.ylyim.data.ChatMsg;
import com.caihang.ylyim.data.source.ChatMsgsDataSource;


public class ChatMsgsRemoteDataSource implements ChatMsgsDataSource {

    private static ChatMsgsRemoteDataSource INSTANCE;


    public static ChatMsgsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatMsgsRemoteDataSource();
        }
        return INSTANCE;
    }

    private ChatMsgsRemoteDataSource() {
    }

    @Override
    public void getChatMsgs(String my,String other,final @NonNull LoadChatMsgsCallback callback) {
    }

    @Override
    public void getChatMsg(@NonNull String taskId, final @NonNull GetChatMsgCallback callback) {
    }

    @Override
    public void saveChatMsg(@NonNull ChatMsg chatMsg) {
    }


    @Override
    public void refreshChatMsgs() {

    }

    @Override
    public void deleteAllChatMsgs() {
    }

    @Override
    public void deleteChatMsg(@NonNull String taskId) {
    }
}
