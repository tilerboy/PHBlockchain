package com.caihang.ylyim.chat;

import org.elastos.carrier.FriendInfo;

public interface ChatFriendChangeListener {

    void addFriend(FriendInfo friendInfo);

    void delFriend(FriendInfo friendInfo);

    void updateFriend(FriendInfo friendInfo);

}
