package com.drp.freechat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drp.freechat.adapter.SmsConversationRecyclerAdapter;
import com.drp.freechat.agora.rtm.ARtmCallEventListener;
import com.drp.freechat.agora.rtm.RtmMessageManager;
import com.drp.freechat.entity.SmsConversations;
import com.drp.freechat.greendao.SmsConversationsDao;
import com.drp.freechat.util.Constants;
import com.drp.freechat.util.SoftKeyBoardListener;
import com.drp.freechat.view.LoadingDialog;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author durui
 * @date 2020/8/25
 * @description
 */
public class MainActivity extends AppCompatActivity implements SmsConversationRecyclerAdapter.SmsItemClickListener {
    private static final int NOT_NOTICE = 100;
    private static final String TAG = "freechat";
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rv_chat_list)
    RecyclerView rvChatList;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.iv_send)
    ImageView ivSend;
    private int mRole;
    private String userId;
    private String peerId;
    private RtmClient rtmClient;
    private Context mContext;
    private SmsConversationsDao dao;
    private List<SmsConversations> list;
    private SmsConversationRecyclerAdapter smsConversationRecyclerAdapter;

    private ARtmCallEventListener mARtmCallEventListener = new ARtmCallEventListener() {
        @Override
        public void onRemoteInvitationReceived(RemoteInvitation remoteInvitation) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().postSticky(remoteInvitation);
                    Intent intent = new Intent(mContext, FullscreenActivity.class);
                    intent.putExtra(Constants.KEY_TYPE, FullscreenActivity.TYPE_CALLED);
                    intent.putExtra(Constants.KEY_USER_ID, userId);
                    intent.putExtra(Constants.KEY_PEER_ID, peerId);
                    startActivity(intent);
                }
            });
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ButterKnife.bind(this);
        mRole = getIntent().getIntExtra(Constants.EXTRA_ROLE, 1);
        if (mRole == 1) {
            tvName.setText("老婆大人");
            userId = "drp";
            peerId = "ajj";
        } else {
            tvName.setText("鹏哥哥");
            userId = "ajj";
            peerId = "drp";
        }
        requestPermisson();
        App.the().registerCallEventListener(mARtmCallEventListener);
        dao = App.the().getDaoSession().getSmsConversationsDao();
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        rvChatList.setLayoutManager(manager);
        smsConversationRecyclerAdapter = new SmsConversationRecyclerAdapter(this, list, this);
        rvChatList.setAdapter(smsConversationRecyclerAdapter);
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                rvChatList.scrollToPosition(list.size() - 1);
            }

            @Override
            public void keyBoardHide(int height) {

            }
        });
    }

    @OnClick({R.id.iv_add, R.id.iv_send, R.id.iv_see})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                Toast.makeText(this, "建设中...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_send:
                sendMsg();
                break;
            case R.id.iv_see:
                Intent intent = new Intent(this, FullscreenActivity.class);
                intent.putExtra(Constants.KEY_TYPE, FullscreenActivity.TYPE_CALLER);
                intent.putExtra(Constants.KEY_USER_ID, userId);
                intent.putExtra(Constants.KEY_PEER_ID, peerId);
                startActivity(intent);
                break;
        }
    }

    private void sendMsg() {
        String message = etContent.getText().toString();
        RtmMessageManager.instance().sendPeerMessage(peerId, message, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SmsConversations conversations = new SmsConversations(null, Constants.TYPE_SEND, Constants.STATUS_SUCCESS,
                        System.currentTimeMillis(), message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
                        etContent.setText("");
                        list.add(conversations);
                        smsConversationRecyclerAdapter.setData(list);
                        smsConversationRecyclerAdapter.notifyDataSetChanged();
                    }
                });
                dao.insert(conversations);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "sendPeerMessage onFailure: " + errorInfo.getErrorCode());
                SmsConversations conversations = new SmsConversations(null, Constants.TYPE_SEND, Constants.STATUS_FAIL,
                        System.currentTimeMillis(), message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        etContent.setText("");
                        Toast.makeText(mContext, "发送失败：" + errorInfo.getErrorDescription(), Toast.LENGTH_SHORT).show();
                        list.add(conversations);
                        smsConversationRecyclerAdapter.setData(list);
                        smsConversationRecyclerAdapter.notifyDataSetChanged();
                    }
                });
                dao.insert(conversations);
            }
        });
    }

    private void loginRtm() {
        LoadingDialog loadingDialog = new LoadingDialog(this).setMessage("数据加载中，请稍后");
        loadingDialog.show();
        rtmClient = App.the().rtmClient();
        rtmClient.logout(null);
        rtmClient.login(null, userId, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(App.TAG, "login rtm success: " + userId);
                loadingDialog.dismiss();
                loadSmsList();
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        Log.i(App.TAG, "login failed: " + errorInfo.getErrorCode());
                        Toast.makeText(mContext, "rtm服务器登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadSmsList() {
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<List<SmsConversations>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SmsConversations>> emitter) throws Exception {
                List<SmsConversations> smsConversations = dao.loadAll();
                emitter.onNext(smsConversations);
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<SmsConversations>>() {
                    @Override
                    public void accept(List<SmsConversations> smsConversations) throws Exception {
                        list = smsConversations;
                        smsConversationRecyclerAdapter.setData(list);
                        smsConversationRecyclerAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void requestPermisson() {
        RxPermissions rxPermission = new RxPermissions(this);
        Disposable subscribe = rxPermission.requestEachCombined(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储权限
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH
        ).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted) {
                    loginRtm();
                } else if (permission.shouldShowRequestPermissionRationale) {
                    // Denied permission without ask never again
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("permission")
                            .setMessage("点击允许才可以使用我们的app哦")
                            .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                    requestPermisson();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                } else {
                    // Denied permission with ask never again
                    // Need to go to the settings
                    Toast.makeText(mContext, permission.name, Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("permission")
                            .setMessage("点击允许才可以使用我们的app哦")
                            .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
                                    intent.setData(uri);
                                    startActivityForResult(intent, NOT_NOTICE);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOT_NOTICE) {
            requestPermisson();
        }
    }

    @Override
    public void onSmsItemClick() {

    }
}
