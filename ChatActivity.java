package com.caihang.ylyim.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.caihang.core.base.BaseActivity;
import com.caihang.core.http.FileApiManager;
import com.caihang.core.utils.SPUtils;
import com.caihang.ylyim.R;
import com.caihang.ylyim.adapter.ChatAdapter;
import com.caihang.ylyim.bean.ChatFileBean;
import com.caihang.ylyim.chat.ChatListener;
import com.caihang.ylyim.chat.ChatManager;
import com.caihang.ylyim.chat.SimpleChatFriendChangeListener;
import com.caihang.ylyim.chat.SimpleChatMsgChangeListener;
import com.caihang.ylyim.common.ChatConstant;
import com.caihang.ylyim.common.SPConstant;
import com.caihang.ylyim.data.ChatMsg;
import com.caihang.ylyim.data.Injection;
import com.caihang.ylyim.data.source.ChatMsgsDataSource;
import com.caihang.ylyim.data.source.ChatMsgsRepository;
import com.caihang.ylyim.fragment.ChatMoreFragment;
import com.caihang.ylyim.util.AESUtil;
import com.caihang.ylyim.util.FilePathUtil;
import com.caihang.ylyim.util.SoftInputUtil;
import com.google.gson.Gson;

import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.Log;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.subscribers.ResourceSubscriber;
import okhttp3.ResponseBody;


public class ChatActivity extends BaseActivity {

    private RecyclerView rv_chat;
    private ChatAdapter chatAdapter;
    private List<ChatMsg> data;
    private ImageView iv_send;
    private FrameLayout fl_more;
    private EditText et_text;
    private String userId;
    private ChatMsgsRepository chatMsgsRepository;

    @Override
    protected void initStatusBar() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        rv_chat = findViewById(R.id.rv_chat);
        iv_send = findViewById(R.id.iv_send);
        et_text = findViewById(R.id.et_text);
        fl_more = findViewById(R.id.fl_more);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initData() {
        userId = getIntent().getStringExtra("userId");
        Log.e("ChatActivity", "userId: " + userId);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_more, new ChatMoreFragment());
        fragmentTransaction.commit();
        fl_more.setVisibility(View.GONE);
        data = new ArrayList<>();
        chatMsgsRepository = Injection.provideTasksRepository(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        rv_chat.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(data);
        rv_chat.setAdapter(chatAdapter);
        iv_send.setImageResource(R.mipmap.more);
        iv_send.setOnClickListener(v -> more());
        rv_chat.setOnTouchListener((v, event) -> {
            fl_more.setVisibility(View.GONE);
            return false;
        });
        et_text.setOnTouchListener((v, event) -> {
            fl_more.setVisibility(View.GONE);
            return false;
        });
        et_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    iv_send.setImageResource(R.mipmap.more);
                    iv_send.setOnClickListener(v -> more());
                } else {
                    iv_send.setImageResource(R.mipmap.send);
                    iv_send.setOnClickListener(v -> sendTextMsg());
                }
            }
        });
        ChatManager.getInstance().addChatMsgChangeListener(new SimpleChatMsgChangeListener() {
            @Override
            public void addMessage(ChatMsg chatMsg) {
                refreshChatMsgs();
            }
        });
        ChatManager.getInstance().addChatFriendChangeListener(new SimpleChatFriendChangeListener() {
            @Override
            public void updateFriend(FriendInfo friendInfo) {
                if (friendInfo != null && userId.equals(friendInfo.getUserId())) {
                    runOnUiThread(() -> refreshFriendStatus(friendInfo));
                }
            }
        });
        refreshChatMsgs();
        FriendInfo friendInfo = ChatManager.getInstance().getFiend(userId);
        if (friendInfo == null) {
            return;
        }
        refreshFriendStatus(friendInfo);
    }

    private void more() {
        SoftInputUtil.hideSoftInputView(mActivity);
        rv_chat.smoothScrollToPosition(rv_chat.getBottom());
        fl_more.setVisibility(View.VISIBLE);
    }

    private void refreshFriendStatus(FriendInfo friendInfo) {
        initTitle(friendInfo.getName(), true);
        if (friendInfo.getConnectionStatus() == ConnectionStatus.Connected) {
            setSubTitle("[在线]");
        } else {
            setSubTitle("[离线]");
        }
    }

    public static void start(Context context, String userId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    private void sendTextMsg() {
        String content = et_text.getText().toString();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        ChatMsg chatMsg = new ChatMsg(UUID.randomUUID().toString(), content, ChatConstant.CONTENT_TYPE_TEXT, ChatConstant.DIRECTION_SEND, System.currentTimeMillis(), SPUtils.getInstance().getString(SPConstant.MY_USERID), userId);
        ChatManager.getInstance().sendMessage(userId, chatMsg, new ChatListener.SendMessageListener() {
            @Override
            public void sendMessageSuccess(ChatMsg chatMsg) {
                et_text.setText("");
            }

            @Override
            public void sendMessageFailure(Exception e) {
                showFailedTip("对方不在线，发送失败");
            }
        });
    }

    public void sendDoc() {
        getWordFile();
    }

    private void sendWordMsg(ChatFileBean chatFileBean) {
        ChatMsg chatMsg = new ChatMsg(UUID.randomUUID().toString(), new Gson().toJson(chatFileBean), ChatConstant.CONTENT_TYPE_WORD, ChatConstant.DIRECTION_SEND, System.currentTimeMillis(), SPUtils.getInstance().getString(SPConstant.MY_USERID), userId);
        ChatManager.getInstance().sendMessage(userId, chatMsg, new ChatListener.SendMessageListener() {
            @Override
            public void sendMessageSuccess(ChatMsg chatMsg) {
                et_text.setText("");
            }

            @Override
            public void sendMessageFailure(Exception e) {
                showFailedTip("对方不在线，发送失败");
            }
        });
    }

    private void refreshChatMsgs() {
        chatMsgsRepository.getChatMsgs(SPUtils.getInstance().getString(SPConstant.MY_USERID), userId, new ChatMsgsDataSource.LoadChatMsgsCallback() {
            @Override
            public void onChatMsgsLoaded(List<ChatMsg> chatMsgs) {
                runOnUiThread(() -> {
                    chatAdapter.setNewData(chatMsgs);
                    rv_chat.smoothScrollToPosition(rv_chat.getBottom());
                });

            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    public void getWordFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                String path = uri.getPath();
                return;
            }
            String path = FilePathUtil.getPath(this, uri);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            uploadFile(path);
        }
    }


    private void uploadFile(String path) {
        File file = new File(path);
        FileApiManager.uploadFile(file, (currentSize, totalSize, done) -> {
            int progress = (int) (100D * currentSize / totalSize);
            progressDialog.setProgress(progress);
        }).subscribe(new ResourceSubscriber<String>() {

            @Override
            public void onNext(String str) {
                try {
                    byte[] aesKey = AESUtil.initKey();
                    Log.e("DDD", "密钥" + new String(aesKey));
                    byte[] encryptResult = AESUtil.encrypt(str.getBytes(), aesKey);
                    String s = new String(encryptResult);
                    Log.e("DDD", "加密后" + s);
                    byte[] decryptResult = AESUtil.decrypt(encryptResult, aesKey);
                    String s2 = new String(decryptResult);
                    Log.e("DDD", "解密后" + s2);
                    ChatFileBean chatFileBean = new ChatFileBean(file.getName(), file.length(), s);
                    sendWordMsg(chatFileBean);
                    progressDialog.dismiss();
                    showSuccessTip("上传成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                progressDialog.dismiss();
                showFailedTip(t.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }


}
