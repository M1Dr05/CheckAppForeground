package com.github.midros.checkappforeground;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Objects;

import static com.github.midros.checkappforeground.Permission.hasUsageStatsPermission;

public class CheckAppForeground implements InterfaceCheck {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public String getForegroundPostLollipop(Context context) {
        if(!hasUsageStatsPermission(context))
            return null;

        String foregroundApp = null;

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Service.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        UsageEvents usageEvents = Objects.requireNonNull(mUsageStatsManager).queryEvents(time - 1000 * 1000, time);
        UsageEvents.Event event = new UsageEvents.Event();
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            if(event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                foregroundApp = event.getPackageName();
            }
        }

        return foregroundApp ;
    }

    @Override
    public String getForegroundPreLollipop(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am != null ? am.getRunningTasks(1).get(0) : null;
        String foregroundTaskPackageName = foregroundTaskInfo != null ? foregroundTaskInfo.topActivity.getPackageName() : null;
        PackageManager pm = context.getPackageManager();
        PackageInfo foregroundAppPackageInfo = null;
        try {
            foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        if(foregroundAppPackageInfo != null) return foregroundAppPackageInfo.applicationInfo.packageName;
        else return null;
    }
}
