package com.halove.sonicwebview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halove.sonicwebview.ui.base.BaseWebFragment;

import org.jetbrains.annotations.NotNull;

/**
 * Created by yanglijun on 18-4-10.
 */

public class TestBrowseFragment extends BaseWebFragment {
    @NotNull
    @Override
    public String getUrl() {
        return "http://map.baidu.com";
    }

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_test_browse, container, false);
        return view;
    }
}
