package com.rxmapple.mytools;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


public class UnreadEventTestActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "UnreadEventTestActivity";
    private static final boolean DEBUG = false;

    private static final String KEY_SELECT_APP = "select_app";
    private static final String KEY_INPUT_UNREAD_COUNT = "unread_count";
    private static final String KEY_SEND_UNREADINFO = "send_unread_info";

    private static final String ACTION_UNREAD_CHANGED = "com.sprd.action.UNREAD_CHANGED";
    private static final String EXTRA_UNREAD_COMPONENT = "com.sprd.intent.extra.UNREAD_COMPONENT";
    private static final String EXTRA_UNREAD_NUMBER = "com.sprd.intent.extra.UNREAD_NUMBER";

    private SelectAppPreference mSelectAppPreference;
    private EditTextPreference mUnreadCountPreference;

    private SharedPreferences mSharedPrefs;
    private ComponentName mSelectedComponentName = null;
    private int mSetUnreadCount = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        addPreferencesFromResource(R.xml.select_apps);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        init();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate( savedInstanceState );
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).
                getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).
                inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        initPreferences();
        loadSetting();
    }

    private void initPreferences() {
        mSelectAppPreference = (SelectAppPreference) findPreference( KEY_SELECT_APP );
        mSelectAppPreference.setOnPreferenceChangeListener( this );
        mSelectAppPreference.setOnPreferenceClickListener( this );

        mUnreadCountPreference = (EditTextPreference) findPreference(KEY_INPUT_UNREAD_COUNT);
        mUnreadCountPreference.setOnPreferenceChangeListener( this );
        mUnreadCountPreference.setOnPreferenceClickListener( this );

        Preference mSendPreference = findPreference( KEY_SEND_UNREADINFO );
        mSendPreference.setOnPreferenceChangeListener( this );
        mSendPreference.setOnPreferenceClickListener( this );
    }

    private boolean loadSetting () {

        if (null == mSharedPrefs) {
            return false;
        }

        String savedAppInfo = mSharedPrefs.getString( KEY_SELECT_APP, null );

        if (null != savedAppInfo) {
            ComponentName cn  = ComponentName.unflattenFromString( savedAppInfo );
            ApplicationInfo info = null;
            try {
                info = getPackageManager().getApplicationInfo( cn.getPackageName(), 0 );
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "get app failed, e:" + e);
            }

            if (info != null) {
                mSelectedComponentName = cn;
                mSelectAppPreference.setSummary( info.loadLabel( getPackageManager() ) );
            }
        }

        String savedUnreadInfo = mSharedPrefs.getString( KEY_INPUT_UNREAD_COUNT, null );
        if (null != savedUnreadInfo) {
            mSetUnreadCount = Integer.valueOf( savedUnreadInfo );
            mUnreadCountPreference.setSummary( savedUnreadInfo );
        }

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        boolean result = false;
        final String key = preference.getKey();

        if (DEBUG) {
            Log.d( TAG, "onPreferenceClick, key:" + key);
        }

        switch (key) {
            case KEY_SEND_UNREADINFO:
                if (mSelectedComponentName == null) {
                    Toast.makeText( this, R.string.appinfo_error, Toast.LENGTH_SHORT ).show();
                } else if (mSetUnreadCount < 0) {
                    Toast.makeText( this, R.string.unread_count_title_error, Toast.LENGTH_SHORT ).show();
                } else {
                    sendUnreadBroadcast( mSetUnreadCount, mSelectedComponentName );
                    Toast.makeText( this, R.string.send_success, Toast.LENGTH_SHORT ).show();
                }
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;
        final String key = preference.getKey();

        if (DEBUG) {
            Log.d( TAG, "onPreferenceChange, key:" + key);
        }
        if (newValue == null) {
            return false;
        }

        switch (key) {
            case KEY_SELECT_APP:
                final String appComponentString = (String) newValue;
                if (TextUtils.isEmpty( appComponentString )) {
                    return false;
                }

                mSelectedComponentName = ComponentName.unflattenFromString( appComponentString );

                if (DEBUG) {
                    Log.d( TAG, "onPreferenceChange, mSelectedComponentName:"
                            + mSelectedComponentName.toShortString() );
                }

                mSelectAppPreference.setValue( appComponentString );
                final CharSequence appName = mSelectAppPreference.getEntry();
                mSelectAppPreference.setSummary( appName );
                result = true;
                break;
            case KEY_INPUT_UNREAD_COUNT:
                String unreadStr = newValue.toString();
                mSetUnreadCount = Integer.valueOf( unreadStr );
                mUnreadCountPreference.setSummary( unreadStr );
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    private void sendUnreadBroadcast(final int unReadNum, final ComponentName cn) {

        Intent intent = new Intent(ACTION_UNREAD_CHANGED);
        intent.putExtra(EXTRA_UNREAD_NUMBER, unReadNum);
        intent.putExtra(EXTRA_UNREAD_COMPONENT, cn);
        sendBroadcast(intent);
        Log.d(TAG, "sendUnreadBroadcast(), cn:" + cn.toShortString() + " unReadNum:" + unReadNum);
    }

}
