package com.halove.sonicwebview

import android.graphics.Bitmap

/**
 * Created by yanglijun on 18-3-24.
 */
class WebViewStyleConfig {

    var isShowLoadProgress = true
    var isShowLoadErrorLayout = true

    var loadErrorIconRes = 0
    var loadErrorIconBitmap:Bitmap? = null

    var loadErrorText: String? = null
    var loadErrorTextColor = 0

    var loadErrorBtnTextColor = 0
    var loadErrorBtnText: String? = null
    var loadErrorBtnBgRes = 0

    var isShowErrorIcon = true
    var isShowErrorText = true
    var isShowErrorBtn = true

    var isFullScreenRefreshMode = false
}