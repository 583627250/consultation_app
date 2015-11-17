package com.consultation.app;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.consultation.app.activity.CaseInfoNewActivity;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

public class ConsultionApplication extends Application {

    private PushAgent mPushAgent;

    @Override
    public void onCreate() {
        mPushAgent=PushAgent.getInstance(this);
        mPushAgent.enable();
        mPushAgent.setDebugMode(false);
        //应用打开是否接收
        mPushAgent.setNotificaitonOnForeground(false);
        //是否合并显示，true桌面图片只会显示一条   最新的  false  通知栏显示多条，桌面图标显示多条
        mPushAgent.setMergeNotificaiton(false);

        UmengMessageHandler messageHandler=new UmengMessageHandler() {

            @Override
            public Notification getNotification(Context context, UMessage msg) {
//                if(Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
//                    BadgeUtil.setBadgeCount(context, 5, BadgeUtil.Platform.mi);
//                } else if(Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
//                    BadgeUtil.setBadgeCount(context, 5, BadgeUtil.Platform.samsung);
//                } else if(Build.MANUFACTURER.equalsIgnoreCase("htc")) {
//                    BadgeUtil.setBadgeCount(context, 5, BadgeUtil.Platform.htc);
//                } else if(Build.MANUFACTURER.equalsIgnoreCase("lg")) {
//                    BadgeUtil.setBadgeCount(context, 5, BadgeUtil.Platform.lg);
//                } else if(Build.MANUFACTURER.equalsIgnoreCase("sony")) {
//                    BadgeUtil.setBadgeCount(context, 5, BadgeUtil.Platform.sony);
//                }
                return super.getNotification(context, msg);
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        UmengNotificationClickHandler notificationClickHandler=new UmengNotificationClickHandler() {

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Intent intent=new Intent(context, CaseInfoNewActivity.class);
                intent.putExtra("caseId", msg.custom);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("type", 1);
                intent.putExtra("isMsg", true);
                startActivity(intent);
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }
}
