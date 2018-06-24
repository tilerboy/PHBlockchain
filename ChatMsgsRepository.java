package com.caihang.ylyim.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.caihang.ylyim.data.ChatMsg;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChatMsgsRepository implements ChatMsgsDataSource {

    private static ChatMsgsRepository INSTANCE = null;

    private final ChatMsgsDataSource mTasksRemoteDataSource;

    private final ChatMsgsDataSource mTasksLocalDataSource;

    Map<String, ChatMsg> mCachedTasks;

    boolean mCacheIsDirty = false;

    private ChatMsgsRepository(@NonNull ChatMsgsDataSource tasksRemoteDataSource,
                               @NonNull ChatMsgsDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    public static ChatMsgsRepository getInstance(ChatMsgsDataSource tasksRemoteDataSource,
                                                 ChatMsgsDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ChatMsgsRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getChatMsgs(String my,String other,@NonNull final LoadChatMsgsCallback callback) {
        checkNotNull(callback);
        if (mCachedTasks != null && !mCacheIsDirty) {
            callback.onChatMsgsLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }
        if (mCacheIsDirty) {
            getTasksFromRemoteDataSource(my,other,callback);
        } else {
            mTasksLocalDataSource.getChatMsgs(my,other,new LoadChatMsgsCallback() {
                @Override
                public void onChatMsgsLoaded(List<ChatMsg> chatMsgs) {
                    refreshCache(chatMsgs);
                    callback.onChatMsgsLoaded(new ArrayList<>(mCachedTasks.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(my,other,callback);
                }
            });
        }
    }

    @Override
    public void saveChatMsg(@NonNull ChatMsg chatMsg) {
        checkNotNull(chatMsg);
        mTasksRemoteDataSource.saveChatMsg(chatMsg);
        mTasksLocalDataSource.saveChatMsg(chatMsg);
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(chatMsg.getId(), chatMsg);
    }

    @Override
    public void getChatMsg(@NonNull final String taskId, @NonNull final GetChatMsgCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);
        ChatMsg cachedChatMsg = getTaskWithId(taskId);
        if (cachedChatMsg != null) {
            callback.onChatMsgLoaded(cachedChatMsg);
            return;
        }
        mTasksLocalDataSource.getChatMsg(taskId, new GetChatMsgCallback() {
            @Override
            public void onChatMsgLoaded(ChatMsg chatMsg) {
                if (mCachedTasks == null) {
                    mCachedTasks = new LinkedHashMap<>();
                }
                mCachedTasks.put(chatMsg.getId(), chatMsg);
                callback.onChatMsgLoaded(chatMsg);
            }

            @Override
            public void onDataNotAvailable() {
                mTasksRemoteDataSource.getChatMsg(taskId, new GetChatMsgCallback() {
                    @Override
                    public void onChatMsgLoaded(ChatMsg chatMsg) {
                        if (mCachedTasks == null) {
                            mCachedTasks = new LinkedHashMap<>();
                        }
                        mCachedTasks.put(chatMsg.getId(), chatMsg);
                        callback.onChatMsgLoaded(chatMsg);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void refreshChatMsgs() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllChatMsgs() {
        mTasksRemoteDataSource.deleteAllChatMsgs();
        mTasksLocalDataSource.deleteAllChatMsgs();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    @Override
    public void deleteChatMsg(@NonNull String taskId) {
        mTasksRemoteDataSource.deleteChatMsg(checkNotNull(taskId));
        mTasksLocalDataSource.deleteChatMsg(checkNotNull(taskId));
        mCachedTasks.remove(taskId);
    }

    private void getTasksFromRemoteDataSource(String my,String other,@NonNull final LoadChatMsgsCallback callback) {
        mTasksRemoteDataSource.getChatMsgs(my,other,new LoadChatMsgsCallback() {
            @Override
            public void onChatMsgsLoaded(List<ChatMsg> chatMsgs) {
                refreshCache(chatMsgs);
                refreshLocalDataSource(chatMsgs);
                callback.onChatMsgsLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<ChatMsg> chatMsgs) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (ChatMsg chatMsg : chatMsgs) {
            mCachedTasks.put(chatMsg.getId(), chatMsg);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<ChatMsg> chatMsgs) {
        mTasksLocalDataSource.deleteAllChatMsgs();
        for (ChatMsg chatMsg : chatMsgs) {
            mTasksLocalDataSource.saveChatMsg(chatMsg);
        }
    }

    @Nullable
    private ChatMsg getTaskWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(id);
        }
    }
}
