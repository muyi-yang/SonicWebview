package com.halove.sonicwebview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.halove.sonicwebview.ui.BaseWebActivity;
import com.halove.sonicwebview.ui.state.StateViewConfig;

import org.jetbrains.annotations.NotNull;

/**
 * Created by yanglijun on 18-3-7.
 */

public class TestBrowseActivity extends BaseWebActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_browse);
    }

    @NotNull
    @Override
    public String getUrl() {
        return "http://www.baidu.com";
    }

    @NotNull
    @Override
    public StateViewConfig getStateViewConfig() {
        StateViewConfig config = new StateViewConfig();
//        config.setShowLoadProgress(false);//显示加载进度
//        config.setShowLoadErrorLayout(false);//显示加载错误布局

//        config.setShowErrorIcon(false);//显示错误icon图标
        config.setLoadErrorIconRes(R.drawable.ic_selected);//加载错误icon提醒,也可以调用setLoadErrorIconBitmap方法
//        config.setLoadErrorIconBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_unselected));

//        config.setShowErrorText(false);//显示加载错误提醒文字
        config.setLoadErrorText("aaaaaaaaaa");//设置加载错误文字
        config.setLoadErrorTextColor(Color.RED);//加载错误文字颜色

//        config.setShowErrorBtn(false);//显示错误提醒按钮
        config.setLoadErrorBtnTextColor(Color.YELLOW);//设置加载错误提醒按钮颜色
        config.setLoadErrorBtnText("sss");//设置加载错误提醒按钮文字
        config.setLoadErrorBtnBgRes(R.drawable.ic_selected);//设置加载错误提醒按钮背景

        config.setFullScreenRefreshMode(true);//设置点击全屏刷新模式，只在加载错误后，显示错误页面起效
        return config;
    }
}
