package com.drp.freechat.agora;

import android.app.Application;

import androidx.annotation.NonNull;

import com.drp.freechat.agora.rtc.AgoraRtcHandler;
import com.drp.freechat.agora.rtm.ARtmCallEventListener;
import com.drp.freechat.agora.rtm.AgoraRtmCallEventListener;
import com.drp.freechat.agora.rtm.RtmMessageManager;
import com.drp.freechat.util.UserUtil;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtm.RtmClient;

public class AgoraEngine {
    private static final String TAG = AgoraEngine.class.getSimpleName();

    private RtcEngine mRtcEngine;
    private AgoraRtcHandler mRtcEventHandler = new AgoraRtcHandler();
    private AgoraRtmCallEventListener mRtmCallEventListener = new AgoraRtmCallEventListener();
    private RtmClient mRtmClient;

    public AgoraEngine(@NonNull Application application, String appId) {
        try {
            mRtcEngine = RtcEngine.create(application, appId, mRtcEventHandler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableVideo();
            mRtcEngine.enableAudioVolumeIndication(200, 3, false);
            mRtcEngine.enableDualStreamMode(false);
            mRtcEngine.setLogFile(UserUtil.rtcLogFilePath(application));

            mRtmClient = RtmClient.createInstance(application, appId, RtmMessageManager.instance());
            mRtmClient.setLogFile(UserUtil.rtmLogFilePath(application));
            mRtmClient.getRtmCallManager().setEventListener(mRtmCallEventListener);
            RtmMessageManager.instance().init(mRtmClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public RtmClient rtmClient() {
        return mRtmClient;
    }

    public void registerRtcHandler(IRtcEngineEventHandler handler) {
        if (mRtcEventHandler != null) mRtcEventHandler.registerEventHandler(handler);
    }

    public void removeRtcHandler(IRtcEngineEventHandler handler) {
        if (mRtcEventHandler != null) mRtcEventHandler.removeEventHandler(handler);
    }

    public void release() {
        if (mRtcEngine != null) RtcEngine.destroy();
        if (mRtmClient != null) {
            mRtmClient.logout(null);
            mRtmClient.release();
        }
    }

    public void registerRtmCallListener(ARtmCallEventListener listener) {
        mRtmCallEventListener.addListener(listener);
    }

    public void removeRtmCallListener(ARtmCallEventListener listener) {
        mRtmCallEventListener.removeListener(listener);
    }
}
