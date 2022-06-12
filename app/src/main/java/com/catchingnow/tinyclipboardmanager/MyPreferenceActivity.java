package com.catchingnow.tinyclipboardmanager;

import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Created by heruoxin on 15/2/28.
 * TODO: should use {@link android.preference.PreferenceFragment}
 */
public class MyPreferenceActivity extends PreferenceActivity {

    //Fix LG support V7 bug:
    //https://code.google.com/p/android/issues/detail?id=78154
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND)) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MyActionBarActivity.ACTIVITY_CLOSED));
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MyActionBarActivity.ACTIVITY_OPENED));
        super.onResume();
    }
}
