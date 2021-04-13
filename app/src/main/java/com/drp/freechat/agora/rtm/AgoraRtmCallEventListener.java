package com.drp.freechat.agora.rtm;

import android.util.Log;

import java.util.ArrayList;

import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.RtmCallEventListener;

/**
 * @author durui
 * @date 2020/8/5
 * @description
 */
public class AgoraRtmCallEventListener implements RtmCallEventListener {
    public static final String TAG = "AIPalmCallEventListener";
    private ArrayList<ARtmCallEventListener> mListeners = new ArrayList<>();

    @Override
    public void onLocalInvitationReceivedByPeer(LocalInvitation localInvitation) {
        Log.d(TAG, "返回给主叫：被叫已收到呼叫邀请");
        for (RtmCallEventListener mListener : mListeners) {
            mListener.onLocalInvitationReceivedByPeer(localInvitation);
        }
    }

    @Override
    public void onLocalInvitationAccepted(LocalInvitation localInvitation, String s) {
        Log.d(TAG, "返回给主叫：被叫已接受呼叫邀请");
        for (RtmCallEventListener mListener : mListeners) {
            mListener.onLocalInvitationAccepted(localInvitation, s);
        }
    }

    @Override
    public void onLocalInvitationRefused(LocalInvitation localInvitation, String s) {
        Log.d(TAG, "返回给主叫：被叫已拒绝呼叫邀请");
        for (RtmCallEventListener mListener : mListeners) {
            mListener.onLocalInvitationRefused(localInvitation, s);
        }
    }

    @Override
    public void onLocalInvitationCanceled(LocalInvitation localInvitation) {
        Log.d(TAG, "返回给主叫：呼叫邀请已被取消");
        for (RtmCallEventListener mListener : mListeners) {
            mListener.onLocalInvitationCanceled(localInvitation);
        }
    }

    @Override
    public void onLocalInvitationFailure(LocalInvitation localInvitation, int i) {
        Log.d(TAG, "返回给主叫：呼叫邀请进程失败");
        for (RtmCallEventListener mListener : mListeners) {
            mListener.onLocalInvitationFailure(localInvitation, i);
        }
    }



    @Override
    public void onRemoteInvitationReceived(RemoteInvitation remoteInvitation) {
        Log.d(TAG, "返回给被叫：收到一个呼叫邀请");
        for (RtmCallEventListener mListener : mListeners) {
            mListener.onRemoteInvitationReceived(remoteInvitation);
        }
    }

    @Override
    public void onRemoteInvitationAccepted(RemoteInvitation remoteInvitation) {
        Log.d(TAG, "返回给被叫：接受呼叫邀请成功");
        for (RtmCallEventListener mListener : mListeners) {
            mListener.onRemoteInvitationAccepted(remoteInvitation);
        }
    }

    @Override
    public void onRemoteInvitationRefused(RemoteInvitation remoteInvitation) {
        Log.d(TAG, "返回给被叫：拒绝呼叫邀请成功");
        for (RtmCallEventListener mListener : mListeners) {
            mListener.onRemoteInvitationRefused(remoteInvitation);
        }
    }

    @Override
    public void onRemoteInvitationCanceled(RemoteInvitation remoteInvitation) {
        Log.d(TAG, "返回给被叫：主叫已取消呼叫邀请");
        for (RtmCallEventListener mListener : mListeners) {
            mListener.onRemoteInvitationCanceled(remoteInvitation);
        }
    }

    @Override
    public void onRemoteInvitationFailure(RemoteInvitation remoteInvitation, int i) {
        Log.d(TAG, "返回给被叫：来自主叫的呼叫邀请进程失败");
        for (RtmCallEventListener mListener : mListeners) {
            mListener.onRemoteInvitationFailure(remoteInvitation, i);
        }
    }

    public void addListener(ARtmCallEventListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(ARtmCallEventListener listener) {
        mListeners.remove(listener);
    }
}
