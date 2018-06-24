package com.caihang.ylyim.chat;

import android.content.Context;
import android.util.Log;

import com.caihang.core.utils.SPUtils;
import com.caihang.ylyim.common.SPConstant;
import com.caihang.ylyim.data.ChatMsg;
import com.caihang.ylyim.ela.ElaOptions;
import com.google.gson.Gson;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.CarrierHandler;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.PresenceStatus;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.List;

public class ChatImpl implements IChat {
    @Override
    public void init(Context context, CarrierHandler handler, ChatListener.InitSuccessListener initSuccessListener) {
        try {
            ElaOptions elaOptions = new ElaOptions(getAppPath(context));
            Carrier carrierInst = Carrier.getInstance(elaOptions, handler);
            carrierInst.start(1000);
            SPUtils.getInstance().put(SPConstant.MY_USERID, carrierInst.getUserId());
            SPUtils.getInstance().put(SPConstant.MY_ADD, carrierInst.getAddress());
            initSuccessListener.initSuccess();
        } catch (Exception e) {
            initSuccessListener.initFailure(e);
            e.printStackTrace();
        }
    }

    @Override
    public List<FriendInfo> getFiends() {
        try {
            return Carrier.getInstance().getFriends();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FriendInfo getFiend(String uesrId) {
        try {
            return Carrier.getInstance().getFriend(uesrId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserInfo getInfo() {
        try {
            return Carrier.getInstance().getSelfInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setInfo(UserInfo userInfo) {
        try {
            Carrier.getInstance().setSelfInfo(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFriend(String address) {
        try {
            Carrier.getInstance().addFriend(address, "auto-accepted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delFriend(String uesrId) {
        try {
            Carrier.getInstance().removeFriend(uesrId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void agreeFriend(String userId) {
        try {
            Carrier.getInstance().AcceptFriend(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String userId, ChatMsg chatMsg, ChatListener.SendMessageListener sendMessageListener) {
        try {
            Carrier.getInstance().sendFriendMessage(userId, new Gson().toJson(chatMsg).trim());
            sendMessageListener.sendMessageSuccess(chatMsg);
        } catch (Exception e) {
            sendMessageListener.sendMessageFailure(e);
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage(String userId, ChatMsg chatMsg, ChatListener.ReceiveMessageListener receiveMessageListener) {

    }

    private String getAppPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

}
