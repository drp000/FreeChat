package com.drp.freechat.agora.rtm;

import io.agora.rtm.LocalInvitation;
import io.agora.rtm.RemoteInvitation;
import io.agora.rtm.RtmCallEventListener;

/**
 * @author durui
 * @date 2020/8/5
 * @description
 */
public abstract class ARtmCallEventListener implements RtmCallEventListener {

    @Override
    public void onLocalInvitationReceivedByPeer(LocalInvitation localInvitation) {

    }

    @Override
    public void onLocalInvitationAccepted(LocalInvitation localInvitation, String s) {

    }

    @Override
    public void onLocalInvitationRefused(LocalInvitation localInvitation, String s) {

    }

    @Override
    public void onLocalInvitationCanceled(LocalInvitation localInvitation) {

    }

    @Override
    public void onLocalInvitationFailure(LocalInvitation localInvitation, int i) {

    }

    @Override
    public void onRemoteInvitationReceived(RemoteInvitation remoteInvitation) {

    }

    @Override
    public void onRemoteInvitationAccepted(RemoteInvitation remoteInvitation) {

    }

    @Override
    public void onRemoteInvitationRefused(RemoteInvitation remoteInvitation) {

    }

    @Override
    public void onRemoteInvitationCanceled(RemoteInvitation remoteInvitation) {

    }

    @Override
    public void onRemoteInvitationFailure(RemoteInvitation remoteInvitation, int i) {

    }
}
