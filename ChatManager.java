package com.caihang.ylyim.chat;

import android.content.Context;
import android.util.Log;

import com.caihang.ylyim.data.ChatMsg;
import com.caihang.ylyim.util.AppExecutors;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.CarrierHandler;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.PresenceStatus;
import org.elastos.carrier.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class ChatManager implements IChat {

    private static ChatManager INSTANCE = null;
    private static IChat iChat = new ChatImpl();
    private AppExecutors appExecutors = new AppExecutors();
    private List<ChatMsgChangeListener> chatMsgChangeListeners = new ArrayList<>();
    private List<ChatFriendChangeListener> chatFriendChangeListeners = new ArrayList<>();

    private ChatManager() {
    }

    public static ChatManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatManager();
        }
        if (iChat == null) {
            iChat = new ChatImpl();
        }
        return INSTANCE;
    }

    @Override
    public void init(Context context, CarrierHandler handler, ChatListener.InitSuccessListener initSuccessListener) {
        appExecutors.networkIO().execute(() -> iChat.init(context, handler, initSuccessListener));
    }

    @Override
    public List<FriendInfo> getFiends() {
        if (iChat != null) {
            return iChat.getFiends();
        } else {
            return null;
        }
    }

    @Override
    public FriendInfo getFiend(String uesrId) {
        if (iChat != null) {
            return iChat.getFiend(uesrId);
        } else {
            return null;
        }
    }

    @Override
    public UserInfo getInfo() {
        if (iChat != null) {
            return iChat.getInfo();
        } else {
            return null;
        }
    }

    @Override
    public void setInfo(UserInfo userInfo) {
        if (iChat != null) {
            iChat.setInfo(userInfo);
        }
    }

    @Override
    public void addFriend(String address) {
        appExecutors.networkIO().execute(() -> iChat.addFriend(address));
    }

    @Override
    public void delFriend(String uesrId) {
        appExecutors.networkIO().execute(() -> iChat.delFriend(uesrId));
    }

    @Override
    public void agreeFriend(String userId) {
        appExecutors.networkIO().execute(() -> iChat.agreeFriend(userId));
    }

    @Override
    public void sendMessage(String userId, ChatMsg chatMsg, ChatListener.SendMessageListener sendMessageListener) {
        appExecutors.networkIO().execute(() -> iChat.sendMessage(userId, chatMsg, new ChatListener.SendMessageListener() {
            @Override
            public void sendMessageSuccess(ChatMsg chatMsg) {
                callOnAddChatMsg(chatMsg);
                appExecutors.mainThread().execute(() -> sendMessageListener.sendMessageSuccess(chatMsg));
            }

            @Override
            public void sendMessageFailure(Exception e) {
                appExecutors.mainThread().execute(() -> sendMessageListener.sendMessageFailure(e));
            }
        }));
    }

    @Override
    public void receiveMessage(String userId, ChatMsg chatMsg, ChatListener.ReceiveMessageListener receiveMessageListener) {
        appExecutors.networkIO().execute(() -> iChat.receiveMessage(userId, chatMsg, this::callOnAddChatMsg));
    }

    public void addChatMsgChangeListener(ChatMsgChangeListener chatMsgChangeListener) {
        chatMsgChangeListeners.add(chatMsgChangeListener);
    }

    public void removeChatMsgChangeListener(ChatMsgChangeListener chatMsgChangeListener) {
        chatMsgChangeListeners.remove(chatMsgChangeListener);
    }

    public void callOnAddChatMsg(ChatMsg chatMsg) {
        if (chatMsgChangeListeners.size() > 0) {
            for (ChatMsgChangeListener chatMsgChangeListener : chatMsgChangeListeners) {
                chatMsgChangeListener.addMessage(chatMsg);
            }
        }
    }

    public void addChatFriendChangeListener(ChatFriendChangeListener chatFriendChangeListener) {
        chatFriendChangeListeners.add(chatFriendChangeListener);
    }

    public void removeChatFriendChangeListener(ChatFriendChangeListener chatFriendChangeListener) {
        chatFriendChangeListeners.remove(chatFriendChangeListener);
    }

    public void callOnAddFriend(FriendInfo friendInfo) {
        if (chatMsgChangeListeners.size() > 0) {
            for (ChatFriendChangeListener chatFriendChangeListener : chatFriendChangeListeners) {
                chatFriendChangeListener.addFriend(friendInfo);
            }
        }
    }

    public void callOnDelFriend(FriendInfo friendInfo) {
        if (chatMsgChangeListeners.size() > 0) {
            for (ChatFriendChangeListener chatFriendChangeListener : chatFriendChangeListeners) {
                chatFriendChangeListener.delFriend(friendInfo);
            }
        }
    }

    public void callOnUpdateFriend(FriendInfo friendInfo) {
        if (chatMsgChangeListeners.size() > 0) {
            for (ChatFriendChangeListener chatFriendChangeListener : chatFriendChangeListeners) {
                chatFriendChangeListener.updateFriend(friendInfo);
            }
        }
    }

}
