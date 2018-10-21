
package com.horizon.base.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.horizon.base.config.GlobalConfig;
import com.horizon.base.util.ResUtil;
import com.horizon.event.EventManager;
import com.horizon.event.Observer;

import org.jetbrains.annotations.NotNull;

public abstract class BaseActivity extends AppCompatActivity implements Observer {
    private static final int[] EMPTY_EVENTS = new int[0];

    protected final String TAG = this.getClass().getSimpleName();

    protected final BaseActivity mActivity = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventManager.register(this);
        if (GlobalConfig.DEBUG) {
            Log.d(TAG, "onCreate");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);
    }

    public void startActivity(Class<?> activityClazz) {
        Intent intent = new Intent(this, activityClazz);
        startActivity(intent);
    }

    protected static String getStr(int resId) {
        return ResUtil.getStr(resId);
    }

    @Override
    public void onEvent(int event, @NotNull Object... args) {

    }

    @NonNull
    @Override
    public int[] listEvents() {
        return EMPTY_EVENTS;
    }
}
