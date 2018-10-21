

package com.horizon.base.ui;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;

import com.horizon.doodle.Doodle;
import com.horizon.task.lifecycle.LifeEvent;
import com.horizon.task.lifecycle.LifecycleManager;
import com.horizon.task.lifecycle.LifeListener;

import java.util.Observable;
import java.util.Observer;


/**
 * 此类实现了对 Activity onDestroy 的监听，
 * 以确保在 Activity 销毁之前 dismiss。
 */
public class BaseDialog extends Dialog implements LifeListener {
    protected final String TAG = this.getClass().getSimpleName();

    private int hostHash;

    public BaseDialog(@NonNull Activity activity) {
        super(activity);
        hostHash = System.identityHashCode(activity);
        LifecycleManager.register(hostHash, this);
    }

    public BaseDialog(@NonNull Activity activity, @StyleRes int themeResId) {
        super(activity, themeResId);
        hostHash = System.identityHashCode(activity);
        LifecycleManager.register(hostHash, this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        LifecycleManager.unregister(hostHash, this);
        LifecycleManager.notify(this, LifeEvent.DESTROY);
    }

    @Override
    public void onEvent(int event) {
        if(event == LifeEvent.DESTROY){
            hostHash = 0;
            if (isShowing()) {
                dismiss();
            }
        }
    }
}
