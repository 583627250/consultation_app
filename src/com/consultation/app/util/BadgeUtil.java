package com.consultation.app.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class BadgeUtil {

    public enum Platform {
        samsung, lg, htc, mi, sony
    };

    public static void setBadgeCount(Context context, int count, Platform platform) {
        Intent badgeIntent=null;
        if(platform.equals(Platform.samsung)) {
            badgeIntent=new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            badgeIntent.putExtra("badge_count", count);
            badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
            badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
        }

        if(platform.equals(Platform.mi)) {
            badgeIntent=new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            badgeIntent.putExtra("android.intent.extra.update_application_component_name", getLauncherClassName(context));
            badgeIntent.putExtra("android.intent.extra.update_application_message_text", count);
            context.sendBroadcast(badgeIntent);
        }

        if(platform.equals(Platform.sony)) {
            badgeIntent=new Intent();
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
            badgeIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", getLauncherClassName(context));
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", count);
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
        }
        if(platform.equals(Platform.htc)) {
            badgeIntent=new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
            badgeIntent.putExtra("packagename", getLauncherClassName(context));
            badgeIntent.putExtra("count", count);
        }

        if(platform.equals(Platform.lg)) {
            badgeIntent=new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
            badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
            badgeIntent.putExtra("badge_count", count);
        }
        context.sendBroadcast(badgeIntent);
    }

    public static void resetBadgeCount(Context context, Platform platform) {
        setBadgeCount(context, 0, platform);
    }

    private static String getLauncherClassName(Context context) {
        PackageManager packageManager=context.getPackageManager();
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ResolveInfo info=packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if(info == null) {
            info=packageManager.resolveActivity(intent, 0);
        }
        return info.activityInfo.name;
    }
}
