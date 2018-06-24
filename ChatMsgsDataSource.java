package com.caihang.ylyim.data.source;

import android.support.annotation.NonNull;

import com.caihang.ylyim.data.ChatMsg;

import java.util.List;

public interface ChatMsgsDataSource {

    interface LoadChatMsgsCallback {

        void onChatMsgsLoaded(List<ChatMsg> chatMsgs);

        void onDataNotAvailable();
    }

    interface GetChatMsgCallback {

        void onChatMsgLoaded(ChatMsg chatMsg);

        void onDataNotAvailable();
    }

    void getChatMsgs(String my,String other,@NonNull LoadChatMsgsCallback callback);

    void getChatMsg(@NonNull String taskId, @NonNull GetChatMsgCallback callback);

    void saveChatMsg(@NonNull ChatMsg chatMsg);

    void refreshChatMsgs();

    void deleteAllChatMsgs();

    void deleteChatMsg(@NonNull String taskId);
}
