package com.horizon.collector.config;

import com.horizon.base.util.LogUtil;
import com.horizon.collector.BuildConfig;
import com.horizon.lightkv.LightKV;
import com.horizon.task.base.TaskLogger;

import org.jetbrains.annotations.NotNull;

public class GlobalLogger implements TaskLogger,LightKV.Logger {
    private static final  GlobalLogger INSTANCE = new GlobalLogger();
    private GlobalLogger(){
    }

    public static GlobalLogger getInstance(){
        return INSTANCE;
    }
    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public void e(@NotNull String tag, @NotNull Throwable e) {
        LogUtil.e(tag, e);
    }
}
