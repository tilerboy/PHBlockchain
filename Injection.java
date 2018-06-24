package com.caihang.ylyim.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.caihang.ylyim.data.source.ChatMsgsRepository;
import com.caihang.ylyim.data.source.local.ChatMsgsLocalDataSource;
import com.caihang.ylyim.data.source.local.ChatDatabase;
import com.caihang.ylyim.data.source.remote.ChatMsgsRemoteDataSource;
import com.caihang.ylyim.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {

    public static ChatMsgsRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        ChatDatabase database = ChatDatabase.getInstance(context);
        return ChatMsgsRepository.getInstance(ChatMsgsRemoteDataSource.getInstance(),
                ChatMsgsLocalDataSource.getInstance(new AppExecutors(), database.chatMsgDao()));
    }
}
