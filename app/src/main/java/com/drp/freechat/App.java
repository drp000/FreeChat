package com.drp.freechat;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.drp.freechat.agora.AgoraEngine;
import com.drp.freechat.agora.rtm.ARtmCallEventListener;
import com.drp.freechat.greendao.DaoMaster;
import com.drp.freechat.greendao.DaoSession;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtm.RtmClient;

/**
 * @author durui
 * @date 2020/8/25
 * @description
 */
public class App extends Application {
    public static final String TAG = "AIPalm";
    private static App sInstance;
    private AgoraEngine mAgoraEngine;

    public static App the() {
        return sInstance;
    }

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initEngine(getString(R.string.agora_app_id));
        initGreenDao();
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "freechat.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public void registerRtcEventHandler(IRtcEngineEventHandler handler) {
        mAgoraEngine.registerRtcHandler(handler);
    }

    public void removeRtcEventHandler(IRtcEngineEventHandler handler) {
        mAgoraEngine.removeRtcHandler(handler);
    }

    public void registerCallEventListener(ARtmCallEventListener listener) {
        mAgoraEngine.registerRtmCallListener(listener);
    }

    public void removeCallEventListener(ARtmCallEventListener listener) {
        mAgoraEngine.removeRtmCallListener(listener);
    }

    public void initEngine(String appId) {
        mAgoraEngine = new AgoraEngine(this, appId);
    }

    public RtcEngine rtcEngine() {
        return mAgoraEngine.rtcEngine();
    }

    public RtmClient rtmClient() {
        return mAgoraEngine.rtmClient();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }
}
