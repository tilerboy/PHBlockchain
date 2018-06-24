package com.caihang.ylyim.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.caihang.ylyim.R;
import com.caihang.ylyim.activity.ChatActivity;
import com.caihang.ylyim.bean.ChatFileBean;
import com.caihang.ylyim.chat.ChatListener;
import com.caihang.ylyim.chat.ChatManager;
import com.caihang.ylyim.chat.SimpleChatMsgChangeListener;
import com.caihang.ylyim.common.ChatConstant;
import com.caihang.ylyim.data.ChatMsg;
import com.caihang.ylyim.data.Injection;
import com.caihang.ylyim.data.source.ChatMsgsRepository;
import com.caihang.ylyim.util.ChatAlertUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.PresenceStatus;
import org.elastos.carrier.UserInfo;

import java.util.List;

public class ChatService extends Service {

    private static final String TAG = ChatService.class.getSimpleName();
    private ChatMsgsRepository chatMsgsRepository;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private static final int NOTIFY_ID = 0;
    private static final String CHANNEL_ID = "app_chat_id";
    private static final CharSequence CHANNEL_NAME = "app_chat_channel";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }

    public void init(ChatListener.InitSuccessListener initSuccessListener) {
        ChatManager.getInstance().init(this, new ElaHandler(), initSuccessListener);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        chatMsgsRepository = Injection.provideTasksRepository(this);
        ChatManager.getInstance().addChatMsgChangeListener(new SimpleChatMsgChangeListener() {
            @Override
            public void addMessage(ChatMsg chatMsg) {
                Log.e(TAG, "save:" + chatMsg.getContent());
                chatMsgsRepository.saveChatMsg(chatMsg);
            }
        });
    }

    class ElaHandler extends AbstractCarrierHandler {

        private static final String TAG = "ElaHandler";

        @Override
        public void onIdle(Carrier carrier) {
//            Log.e(TAG, "onIdle:");
        }

        @Override
        public void onConnection(Carrier carrier, ConnectionStatus connectionStatus) {
            Log.e(TAG, "onConnection:" + connectionStatus.toString());
        }

        public void onReady(Carrier carrier) {
            Log.e(TAG, "onReady:");
        }

        @Override
        public void onSelfInfoChanged(Carrier carrier, UserInfo userInfo) {
            Log.e(TAG, "onSelfInfoChanged:" + userInfo.getUserId());
        }

        @Override
        public void onFriends(Carrier carrier, List<FriendInfo> list) {
            Log.e(TAG, "onFriends:" + list.size());
        }

        public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status) {
            Log.e(TAG, "Get a onFriendConnection (" + status + ") from (" + friendId + ")");
            FriendInfo friendInfo = ChatManager.getInstance().getFiend(friendId);
            ChatManager.getInstance().callOnUpdateFriend(friendInfo);
        }

        @Override
        public void onFriendInfoChanged(Carrier carrier, String s, FriendInfo friendInfo) {
            Log.e(TAG, "onFriendInfoChanged:" + friendInfo.getUserId());
        }

        @Override
        public void onFriendPresence(Carrier carrier, String s, PresenceStatus presenceStatus) {
            Log.e(TAG, "onFriendPresence:" + presenceStatus.toString());
        }

        @Override
        public void onFriendRequest(Carrier carrier, String s, UserInfo userInfo, String s1) {
            Log.e(TAG, "onFriendRequest:" + userInfo.getUserId());
            ChatManager.getInstance().agreeFriend(userInfo.getUserId());
        }

        @Override
        public void onFriendAdded(Carrier carrier, FriendInfo friendInfo) {
            Log.e(TAG, "onFriendAdded:" + friendInfo.getUserId());
        }

        @Override
        public void onFriendRemoved(Carrier carrier, String s) {
            Log.e(TAG, "onFriendRemoved:" + s);
        }

        public void onFriendMessage(Carrier whisper, String friendId, String message) {
            Log.e(TAG, "Get a message (" + message + ") from (" + friendId + ")");
            try {
                ChatMsg chatMsg = new Gson().fromJson(message.trim(), ChatMsg.class);
                ChatMsg newChatMsg = new ChatMsg(chatMsg.getId(), chatMsg.getContent(), chatMsg.getContentType(), ChatConstant.DIRECTION_RECV, chatMsg.getTime(), chatMsg.getOther(), chatMsg.getMy());
                ChatManager.getInstance().callOnAddChatMsg(newChatMsg);
                ChatAlertUtil.alert(ChatService.this);
                String topActivity = getTopActivity(ChatService.this);
                if (topActivity != null && topActivity.contains(ChatActivity.class.getSimpleName())) {
                    return;
                }
                setUpNotification(newChatMsg);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFriendInviteRequest(Carrier carrier, String s, String s1) {
            Log.e(TAG, "onFriendInviteRequest:" + s + " " + s1);
        }
    }

    private void setUpNotification(ChatMsg newChatMsg) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            //设置绕过免打扰模式
//            channel.setBypassDnd(false);
//            //检测是否绕过免打扰模式
//            channel.canBypassDnd();
//            //设置在锁屏界面上显示这条通知
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(true);
            channel.setVibrationPattern(new long[]{100, 200});
            channel.enableVibration(true);
            channel.enableLights(false);
            mNotificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", newChatMsg.getOther());
        PendingIntent contentIntent = PendingIntent.getActivity(this, NOTIFY_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setContentTitle(getName(newChatMsg))
                .setContentText(getContent(newChatMsg))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }

    private String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos;
        if (manager != null) {
            runningTaskInfos = manager.getRunningTasks(1);
            return runningTaskInfos.get(0).topActivity.toString();
        } else {
            return null;
        }
    }

    private String getName(ChatMsg newChatMsg) {
        String name = "";
        try {
            FriendInfo fiend = ChatManager.getInstance().getFiend(newChatMsg.getOther());
            name = fiend.getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    private String getContent(ChatMsg newChatMsg) {
        String content = "";
        try {
            switch (newChatMsg.getContentType()) {
                case ChatConstant.CONTENT_TYPE_TEXT:
                    content = newChatMsg.getContent();
                    break;
                case ChatConstant.CONTENT_TYPE_WORD:
                    ChatFileBean chatFileBean = new Gson().fromJson(newChatMsg.getContent(), ChatFileBean.class);
                    content = chatFileBean.getName();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

}
