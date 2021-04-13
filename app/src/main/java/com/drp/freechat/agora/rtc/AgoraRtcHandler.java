package com.drp.freechat.agora.rtc;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;

public class AgoraRtcHandler extends IRtcEngineEventHandler {
    private static final String TAG = "AIPalm";
    private List<IRtcEngineEventHandler> mHandlers;

    public AgoraRtcHandler() {
        mHandlers = new ArrayList<>();
    }

    public void registerEventHandler(IRtcEngineEventHandler handler) {
        if (!mHandlers.contains(handler)) {
            mHandlers.add(handler);
        }
    }

    public void removeEventHandler(IRtcEngineEventHandler handler) {
        mHandlers.remove(handler);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.i(TAG, String.format("onJoinChannelSuccess(%s, %d,%d)", channel, uid, elapsed));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onJoinChannelSuccess(channel, uid, elapsed);
        }
    }

    @Override
    public void onLeaveChannel(RtcStats stats) {
        Log.i(TAG, "onLeaveChannel:" + stats.users);
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onLeaveChannel(stats);
        }
    }

    @Override
    public void onClientRoleChanged(int i, int i1) {
        Log.i(TAG, "onClientRoleChanged:" + i + "->" + i1);
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onClientRoleChanged(i, i1);
        }
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        Log.i(TAG, String.format("onRemoteVideoStateChanged(%d, %d,%d,%d)", uid, state, reason, elapsed));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onRemoteVideoStateChanged(uid, state, reason, elapsed);
        }
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.i(TAG, String.format("onUserJoined(%d,%d)", uid, elapsed));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onUserJoined(uid, elapsed);
        }
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.i(TAG, String.format("onUserOffline(%d,%d)", uid, reason));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onUserOffline(uid, reason);
        }
    }

    /*@Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        Log.i(TAG, String.format("onLocalVideoStats()"));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onLocalVideoStats(stats);
        }
    }*/

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
//        Log.i(TAG, String.format("onRtcStats(%d)", stats.users));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onRtcStats(stats);
        }
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
//        Log.i(TAG, String.format("onNetworkQuality(%d,%d,%d)", uid, txQuality, rxQuality));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onNetworkQuality(uid, txQuality, rxQuality);
        }
    }

   /* @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        Log.i(TAG, String.format("onRemoteVideoStats()"));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onRemoteVideoStats(stats);
        }
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        Log.i(TAG, String.format("onRemoteAudioStats()"));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onRemoteAudioStats(stats);
        }
    }*/

    @Override
    public void onLastmileQuality(int quality) {
        Log.i(TAG, String.format("onLastmileQuality(%d)", quality));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onLastmileQuality(quality);
        }
    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {
        Log.i(TAG, String.format("onLastmileProbeResult(%d)", result.state));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onLastmileProbeResult(result);
        }
    }

    @Override
    public void onChannelMediaRelayStateChanged(int state, int code) {
        Log.i(TAG, String.format("onChannelMediaRelayStateChanged(%d, %d)", state, code));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onChannelMediaRelayStateChanged(state, code);
        }
    }

    @Override
    public void onChannelMediaRelayEvent(int code) {
        Log.i(TAG, String.format("onChannelMediaRelayEvent(%d)", code));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onChannelMediaRelayEvent(code);
        }
    }

    @Override
    public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
        if (totalVolume > 0) {
            Log.i(TAG, String.format("onAudioVolumeIndication(%d)", totalVolume));
            for (IRtcEngineEventHandler handler : mHandlers) {
                handler.onAudioVolumeIndication(speakers, totalVolume);
            }
        }
    }

    @Override
    public void onAudioRouteChanged(int routing) {
        Log.i(TAG, String.format("onAudioRouteChanged(%d)", routing));
        for (IRtcEngineEventHandler handler : mHandlers) {
            handler.onAudioRouteChanged(routing);
        }
    }
}
