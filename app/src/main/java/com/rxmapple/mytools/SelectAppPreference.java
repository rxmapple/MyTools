package com.rxmapple.mytools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rxmapple on 2016/9/28.
 */
public class SelectAppPreference extends AppListPreference {

    private static final String TAG = "SelectAppPreference";
    private static final boolean DEBUG = false;
    final private PackageManager mPm;

    public SelectAppPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPm = context.getPackageManager();
        refreshSelectApps();
    }

    public void refreshSelectApps() {
        List<String> apps = resolveLauncherApps();
        setApplications(apps.toArray(new String[apps.size()]), null);
    }

    private static List<ResolveInfo> getLauncherActivities(PackageManager pm) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    private List<String> resolveLauncherApps() {
        List<String> result = new ArrayList<>();

        // Resolve that intent and check that the handleAllWebDataURI boolean is set
        List<ResolveInfo> list = getLauncherActivities(mPm);

        final int count = list.size();
        for (int i=0; i<count; i++) {
            ResolveInfo info = list.get(i);
            ActivityInfo aInfo = info.activityInfo;
            if (aInfo == null) {
                continue;
            }

            String componentStr = new ComponentName(aInfo.packageName, aInfo.name).flattenToString();

            if (result.contains( componentStr )) {
                continue;
            }
            if (DEBUG) {
                Log.d(TAG, "activity<<" + componentStr + ">> is added!");
            }
            result.add( componentStr );
        }

        Log.d(TAG, "resolveLauncherApps end,size: " + result.size());

        return result;
    }
}
