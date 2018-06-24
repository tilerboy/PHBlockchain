package com.caihang.ylyim.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.caihang.ylyim.common.ChatConstant;
import com.chad.library.adapter.base.entity.MultiItemEntity;

@Entity(tableName = "chatMsgs")
public final class ChatMsg implements MultiItemEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private final String id;

    @Nullable
    @ColumnInfo(name = "content")
    private final String content;

    @ColumnInfo(name = "contentType")
    private final int contentType;

    @ColumnInfo(name = "direction")
    private final int direction;

    @ColumnInfo(name = "time")
    private final long time;


    @Nullable
    @ColumnInfo(name = "my")
    private final String my ;

    @Nullable
    @ColumnInfo(name = "other")
    private final String other ;

    public ChatMsg(@NonNull String id, @Nullable String content, int contentType,  int direction, long time, @Nullable String my, @Nullable String other) {
        this.id = id;
        this.content = content;
        this.contentType = contentType;
        this.direction = direction;
        this.time = time;
        this.my = my;
        this.other = other;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @Nullable
    public String getContent() {
        return content;
    }

    public int getContentType() {
        return contentType;
    }

    public int getDirection() {
        return direction;
    }

    public long getTime() {
        return time;
    }

    @Nullable
    public String getMy() {
        return my;
    }

    @Nullable
    public String getOther() {
        return other;
    }

    @Override
    public int getItemType() {
        switch (getContentType()){
            case ChatConstant.CONTENT_TYPE_TEXT:
                if(getDirection() == ChatConstant.DIRECTION_SEND){
                    return ChatConstant.TEXT_SEND;
                }else {
                    return ChatConstant.TEXT_RECV;
                }
            case ChatConstant.CONTENT_TYPE_WORD:
                if(getDirection() == ChatConstant.DIRECTION_SEND){
                    return ChatConstant.WORD_SEND;
                }else {
                    return ChatConstant.WORD_RECV;
                }
        }
        return 0;
    }
}
