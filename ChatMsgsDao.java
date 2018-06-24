package com.caihang.ylyim.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.caihang.ylyim.data.ChatMsg;

import java.util.List;

@Dao
public interface ChatMsgsDao {

    @Query("SELECT * FROM chatMsgs WHERE my = :my and other = :other")
    List<ChatMsg> getChatMsgs(String my,String other);

    @Query("SELECT * FROM chatMsgs WHERE id = :taskId")
    ChatMsg getChatMsgById(String taskId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChatMsg(ChatMsg chatMsg);

    @Query("DELETE FROM chatMsgs WHERE id = :taskId")
    int deleteChatMsgById(String taskId);

    @Query("DELETE FROM chatMsgs")
    void deleteChatMsgs();

}
