package com.halove.sonicwebview.core

import android.content.Context
import com.halove.sonicwebview.ui.state.StateViewConfig
import com.tencent.sonic.sdk.SonicConfig
import com.tencent.sonic.sdk.SonicRuntime
import com.tencent.sonic.sdk.SonicSessionClient
import com.tencent.sonic.sdk.SonicSessionConfig

/**
 * Created by yanglijun on 18-3-24.
 */
interface IConfig {

    fun getSonicRuntime(context: Context): SonicRuntime {
        return DefaultSonicRuntime(context)
    }

    fun getSonicConfig(): SonicConfig {
        return SonicConfig.Builder().build()
    }

    fun getSonicSessionConfig(): SonicSessionConfig {
        return SonicSessionConfig.Builder().build()
    }

    fun getSonicSessionClient(): SonicSessionClient {
        return DefaultSonicSessionClient()
    }

    fun getStateViewConfig(): StateViewConfig {
        return StateViewConfig()
    }

    abstract fun getUrl(): String
}