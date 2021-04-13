package com.drp.freechat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.drp.freechat.agora.rtm.ARtmCallEventListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.RtmCallManager;
import io.agora.rtm.RtmClient;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
//            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    /**
     * 主叫
     */
    public static final int TYPE_CALLER = 0;
    /**
     * 被叫
     */
    public static final int TYPE_CALLED = 1;
    @BindView(R.id.tv_call_name)
    TextView tvCallName;
    @BindView(R.id.tv_call_tip)
    TextView tvCallTip;
    @BindView(R.id.iv_call_portrait)
    ImageView ivCallPortrait;
    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;
    @BindView(R.id.layout_caller_option)
    RelativeLayout layoutCallerOption;
    @BindView(R.id.ll_hangup)
    LinearLayout llHangup;
    @BindView(R.id.ll_answer)
    LinearLayout llAnswer;
    @BindView(R.id.layout_called_option)
    RelativeLayout layoutCalledOption;
    @BindView(R.id.layout_call)
    RelativeLayout layoutCall;
    @BindView(R.id.remote_video_view_container)
    RelativeLayout remoteVideoViewContainer;
    @BindView(R.id.local_video_view_container)
    FrameLayout localVideoViewContainer;
    @BindView(R.id.video_tv_time)
    TextView videoTvTime;
    @BindView(R.id.iv_mute_video)
    ImageView ivMuteVideo;
    @BindView(R.id.ll_mute_video)
    LinearLayout llMuteVideo;
    @BindView(R.id.iv_mute_audio)
    ImageView ivMuteAudio;
    @BindView(R.id.ll_mute_audio)
    LinearLayout llMuteAudio;
    @BindView(R.id.iv_mute_speaker)
    ImageView ivMuteSpeaker;
    @BindView(R.id.ll_mute_speaker)
    LinearLayout llMuteSpeaker;
    @BindView(R.id.ll_switch_camera)
    LinearLayout llSwitchCamera;
    @BindView(R.id.iv_mute_switch_camera)
    ImageView ivMuteSwitchCamera;
    @BindView(R.id.video_iv_end)
    ImageView videoIvEnd;
    @BindView(R.id.layout_video)
    RelativeLayout layoutVideo;
    @BindView(R.id.layout_conversation)
    FrameLayout layoutConversation;
    @BindView(R.id.iv_remote_avatar)
    ImageView ivRemoteAvatar;
    @BindView(R.id.tv_remote_name)
    TextView tvRemoteName;

    private int mType;
    private String userId;
    private String peerId;

    private RemoteInvitation mRemoteInvitation;
    private Context mContext;
    private RtmClient mRtmClient;
    private RtcEngine mRtcEngine;
    private RtmCallManager mCallManager;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;

    private IRtcEngineEventHandler eventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(int i, int i1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserJoined(i, i1);
                }
            });
        }

        @Override
        public void onRemoteVideoStateChanged(int i, int i1, int i2, int i3) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (i1 == Constants.REMOTE_VIDEO_STATE_STOPPED) {
                        removeRemoteVideo();
                    } else if (i1 == Constants.REMOTE_VIDEO_STATE_DECODING) {
                        setupRemoteVideo(i);
                    }
                }
            });
        }

        @Override
        public void onLocalVideoStateChanged(int i, int i1) {

        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onLeaveChannel(RtcStats rtcStats) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }

    };

    private ARtmCallEventListener mARtmCallEventListener = new ARtmCallEventListener() {
        @Override
        public void onLocalInvitationFailure(LocalInvitation localInvitation, int i) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "无人接听，请稍后再试", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        @Override
        public void onLocalInvitationAccepted(LocalInvitation localInvitation, String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvCallTip.setText("正在建立连接");
                    joinChannel();
                }
            });
        }

        @Override
        public void onLocalInvitationRefused(LocalInvitation localInvitation, String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "对方已拒绝", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        @Override
        public void onRemoteInvitationCanceled(RemoteInvitation remoteInvitation) {
            finish();
        }

        @Override
        public void onRemoteInvitationAccepted(RemoteInvitation remoteInvitation) {
            joinChannel();
        }

        @Override
        public void onRemoteInvitationFailure(RemoteInvitation remoteInvitation, int i) {
            finish();
        }

        @Override
        public void onRemoteInvitationRefused(RemoteInvitation remoteInvitation) {
            finish();
        }
    };
    private Disposable subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mContext = this;
        ButterKnife.bind(this);
        mType = getIntent().getIntExtra(com.drp.freechat.util.Constants.KEY_TYPE, TYPE_CALLER);
        userId = getIntent().getStringExtra(com.drp.freechat.util.Constants.KEY_USER_ID);
        peerId = getIntent().getStringExtra(com.drp.freechat.util.Constants.KEY_PEER_ID);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.content_view);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        layoutCall.setVisibility(View.VISIBLE);
        layoutConversation.setVisibility(View.GONE);
        if (mType == TYPE_CALLER) {
            layoutCallerOption.setVisibility(View.VISIBLE);
            layoutCalledOption.setVisibility(View.GONE);
            tvCallName.setText(peerId);
            tvRemoteName.setText(peerId);
            ivCallPortrait.setImageResource(R.mipmap.icon_default_avatar);
            ivRemoteAvatar.setImageResource(R.mipmap.icon_default_avatar);
            tvCallTip.setText("正在等待对方接受邀请");
        } else {
            layoutCallerOption.setVisibility(View.GONE);
            layoutCalledOption.setVisibility(View.VISIBLE);
            tvCallName.setText(peerId);
            tvRemoteName.setText(userId);
            ivCallPortrait.setImageResource(R.mipmap.icon_default_avatar);
            ivRemoteAvatar.setImageResource(R.mipmap.icon_default_avatar);
            tvCallTip.setText("邀请你进行视频通话");
        }

        ivMuteVideo.setActivated(false);
        ivMuteAudio.setActivated(true);
        ivMuteSpeaker.setActivated(true);
        ivMuteSwitchCamera.setEnabled(false);

        mRtmClient = App.the().rtmClient();
        mCallManager = mRtmClient.getRtmCallManager();
        mRtcEngine = App.the().rtcEngine();
        setupVideoConfig();

        App.the().registerRtcEventHandler(eventHandler);
        App.the().registerCallEventListener(mARtmCallEventListener);

        mRtcEngine.setEnableSpeakerphone(true);
        mRtcEngine.muteLocalAudioStream(false);
        setupLocalVideo(false);
        if (mType == TYPE_CALLER) {
            call();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onRemote(RemoteInvitation remoteInvitation) {
        mRemoteInvitation = remoteInvitation;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * 发送呼叫邀请
     */
    private void call() {
        LocalInvitation localInvitation = mCallManager.createLocalInvitation(peerId);
        localInvitation.setChannelId("dax" + userId);
        localInvitation.setContent("你好呀");
        mCallManager.sendLocalInvitation(localInvitation, null);
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableLocalVideo(false);
        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo(boolean isEnabled) {
        ivMuteSwitchCamera.setEnabled(isEnabled);
        mRtcEngine.enableLocalVideo(isEnabled);
        if (isEnabled) {
            // This is used to set a local preview.
            // The steps setting local and remote view are very similar.
            // But note that if the local user do not have a uid or do
            // not care what the uid is, he can set his uid as ZERO.
            // Our server will assign one and return the uid via the event
            // handler callback function (onJoinChannelSuccess) after
            // joining the channel successfully.
            mLocalView = RtcEngine.CreateRendererView(mContext);
            mLocalView.setZOrderMediaOverlay(true);
            localVideoViewContainer.removeAllViews();
            localVideoViewContainer.addView(mLocalView);
            // Initializes the local video view.
            // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
            mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
        } else {
            localVideoViewContainer.removeAllViews();
            View inflate = View.inflate(mContext, R.layout.layout_local_view, null);
            ImageView imageView = inflate.findViewById(R.id.iv_local_avatar);
            imageView.setImageResource(R.mipmap.icon_default_avatar);
            localVideoViewContainer.addView(inflate);
        }
    }

    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        String token = mContext.getResources().getString(R.string.agora_access_token);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
//        mRtcEngine.joinChannel(token, App.userId, "Extra Optional Data", 0);

        //	(String token,String channelName,String userAccount)
        String channelName;
        if (mType == TYPE_CALLER) {
            channelName = "dax" + userId;
        } else {
            channelName = mRemoteInvitation.getChannelId();
        }
        mRtcEngine.joinChannelWithUserAccount(token, channelName, userId);
    }

    private void onRemoteUserJoined(int i, int i1) {
        layoutCall.setVisibility(View.GONE);
        layoutConversation.setVisibility(View.VISIBLE);
        subscribe = Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        videoTvTime.setText("当前通话时间：" + aLong);
                    }
                });
    }

    private void onRemoteUserLeft() {
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
        removeRemoteVideo();
        Toast.makeText(mContext, "对方已挂断", Toast.LENGTH_SHORT).show();
        mRtcEngine.leaveChannel();
    }

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            remoteVideoViewContainer.removeView(mRemoteView);
        }
        // Destroys remote view
        mRemoteView = null;
    }

    private void setupRemoteVideo(int uid) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        int count = remoteVideoViewContainer.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            View v = remoteVideoViewContainer.getChildAt(i);
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
            }
        }

        if (view != null) {
            return;
        }
        layoutVideo.setVisibility(View.VISIBLE);
        mRemoteView = RtcEngine.CreateRendererView(App.the().getBaseContext());
        remoteVideoViewContainer.addView(mRemoteView);
        // Initializes the video view of a remote user.
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRemoteView.setTag(uid);
    }

    @OnClick({R.id.ll_cancel, R.id.ll_hangup, R.id.ll_answer, R.id.ll_mute_video, R.id.ll_mute_audio, R.id.ll_mute_speaker, R.id.ll_switch_camera, R.id.video_iv_end})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.ll_cancel) {
            finish();
        } else if (id == R.id.ll_hangup) {
            mCallManager.refuseRemoteInvitation(mRemoteInvitation, null);
        } else if (id == R.id.ll_answer) {//接听
            mCallManager.acceptRemoteInvitation(mRemoteInvitation, null);
            tvCallTip.setText("正在建立连接");
        } else if (id == R.id.ll_mute_video) {
            ivMuteVideo.setActivated(!ivMuteVideo.isActivated());
            boolean isActivated = ivMuteVideo.isActivated();
            setupLocalVideo(isActivated);
        } else if (id == R.id.ll_mute_audio) {
            ivMuteAudio.setActivated(!ivMuteAudio.isActivated());
            boolean selected = ivMuteAudio.isActivated();
            mRtcEngine.muteLocalAudioStream(!selected);
        } else if (id == R.id.ll_mute_speaker) {
            ivMuteSpeaker.setActivated(!ivMuteSpeaker.isActivated());
            boolean selected = ivMuteSpeaker.isActivated();
            if (selected) {
                Toast.makeText(mContext, "声音将通过扬声器播放", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "声音将通过听筒播放", Toast.LENGTH_SHORT).show();
            }
            mRtcEngine.setEnableSpeakerphone(selected);
        } else if (id == R.id.video_iv_end) {
            mRtcEngine.leaveChannel();
        } else if (id == R.id.ll_switch_camera) {
            mRtcEngine.switchCamera();
        }
    }
}