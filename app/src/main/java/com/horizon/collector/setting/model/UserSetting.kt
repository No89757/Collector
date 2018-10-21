package com.horizon.collector.setting.model

import com.horizon.base.config.GlobalConfig
import com.horizon.collector.config.GlobalLogger
import com.horizon.lightkv.KVData
import com.horizon.lightkv.LightKV

object UserSetting : KVData() {
    override val data: LightKV
        get() = LightKV.Builder(GlobalConfig.getAppContext(), "user_setting")
                .logger(GlobalLogger.getInstance())
                .async()

    var showHidden by boolean(1)
    var huabanChannels by string(2)
    var unsplashChannels by string(3)
    var collectPath by string(4)
    var lastShowingFragment by string(5)
}