package com.caihang.ylyim.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.caihang.ylyim.data.ChatMsg;
import com.caihang.ylyim.data.source.ChatMsgsDataSource;
import com.caihang.ylyim.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChatMsgsLocalDataSource implements ChatMsgsDataSource {

    private static volatile ChatMsgsLocalDataSource INSTANCE;
    private ChatMsgsDao mChatMsgsDao;
    private AppExecutors mAppExecutors;

    private ChatMsgsLocalDataSource(@NonNull AppExecutors appExecutors,
                                    @NonNull ChatMsgsDao chatMsgsDao) {
        mAppExecutors = appExecutors;
        mChatMsgsDao = chatMsgsDao;
    }

    public static ChatMsgsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                      @NonNull ChatMsgsDao chatMsgsDao) {
        if (INSTANCE == null) {
            synchronized (ChatMsgsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ChatMsgsLocalDataSource(appExecutors, chatMsgsDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getChatMsgs(String my,String other,@NonNull final LoadChatMsgsCallback callback) {
        Runnable runnable = () -> {
            final List<ChatMsg> chatMsgs = mChatMsgsDao.getChatMsgs(my,other);
            mAppExecutors.mainThread().execute(() -> {
                if (chatMsgs.isEmpty()) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onChatMsgsLoaded(chatMsgs);
                }
            });
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getChatMsg(@NonNull final String taskId, @NonNull final GetChatMsgCallback callback) {
        Runnable runnable = () -> {
            final ChatMsg chatMsg = mChatMsgsDao.getChatMsgById(taskId);
            mAppExecutors.mainThread().execute(() -> {
                if (chatMsg != null) {
                    callback.onChatMsgLoaded(chatMsg);
                } else {
                    callback.onDataNotAvailable();
                }
            });
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveChatMsg(@NonNull final ChatMsg chatMsg) {
        checkNotNull(chatMsg);
        Runnable saveRunnable = () -> mChatMsgsDao.insertChatMsg(chatMsg);
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void refreshChatMsgs() {

    }

    @Override
    public void deleteAllChatMsgs() {
        Runnable deleteRunnable = () -> mChatMsgsDao.deleteChatMsgs();
        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteChatMsg(@NonNull final String taskId) {
        Runnable deleteRunnable = () -> mChatMsgsDao.deleteChatMsgById(taskId);

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }
}
