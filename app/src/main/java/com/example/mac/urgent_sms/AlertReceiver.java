package com.example.mac.urgent_sms;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mac on 01/10/2018.
 */

public class AlertReceiver extends BroadcastReceiver {
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (isRunning(context)) {
            //app is running
        }
        else {

            Intent i = context.getPackageManager().getLaunchIntentForPackage("com.example.mac.urgent_sms");
            context.startActivity(i);

        }
        sharedPrefs.setHasTimerEnableApp(true,context);

    }


    public static boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }
        return false;
    }
}
