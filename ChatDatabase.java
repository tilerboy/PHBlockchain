package com.caihang.ylyim.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.caihang.ylyim.data.ChatMsg;

@Database(entities = {ChatMsg.class}, version = 1)
public abstract class ChatDatabase extends RoomDatabase {

    private static ChatDatabase INSTANCE;

    public abstract ChatMsgsDao chatMsgDao();

    private static final Object sLock = new Object();

    public static ChatDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        ChatDatabase.class, "ChatMsg.db")
                        .build();
            }
            return INSTANCE;
        }
    }

}
