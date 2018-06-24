package com.caihang.ylyim.chat;

import android.content.Context;

import com.caihang.ylyim.data.ChatMsg;

import org.elastos.carrier.CarrierHandler;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.UserInfo;

import java.util.List;

public interface IChat {

    void init(Context context, CarrierHandler handler, ChatListener.InitSuccessListener initSuccessListener);

    List<FriendInfo> getFiends();

    FriendInfo getFiend(String uesrId);

    UserInfo getInfo();

    void setInfo(UserInfo userInfo);

    void addFriend(String address);

    void delFriend(String uesrId);

    void agreeFriend(String userId);

    void sendMessage(String userId, ChatMsg chatMsg, ChatListener.SendMessageListener sendMessageListener);

    void receiveMessage(String userId,  ChatMsg chatMsg,ChatListener.ReceiveMessageListener receiveMessageListener);

}
