package com.github.midros.checkappforeground;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.support.annotation.RequiresApi;

import java.util.Objects;

public class Permission {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Boolean hasUsageStatsPermission(Context context){
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = Objects.requireNonNull(appOps).checkOpNoThrow("android:get_usage_stats", Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
