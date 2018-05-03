package com.halove.sonicwebview;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;

import com.halove.sonicwebview.tools.BackHandlerHelper;
import com.halove.sonicwebview.ui.BrowseActivity;
import com.halove.sonicwebview.ui.BrowseFragment;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BrowseActivity.class);
//                intent.putExtra(Constants.INSTANCE.getPARAM_URL(), "http://www.halove.com");
                intent.putExtra(Constants.INSTANCE.getPARAM_URL(), "https://www.halove.com/manual/v1su");
//                intent.putExtra(Constants.INSTANCE.getPARAM_ACTIONBAR_SHOW(), false);//是否显示actionBar
//                intent.putExtra(Constants.INSTANCE.getPARAM_ACTIONBAR_BACK_SHOW(), false);//是否显示actionBar上的返回按钮
                intent.putExtra(Constants.INSTANCE.getPARAM_BACK_FORCE_FINISH(), true);//按actionBar的返回键是否强制关闭页面
//                intent.putExtra(Constants.INSTANCE.getPARAM_TITLE(), "ssss");
                startActivity(intent);
            }
        });

        findViewById(R.id.test1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TestBrowseActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.test2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new TestBrowseFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();
            }
        });

        Fragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INSTANCE.getPARAM_URL(), "www.qq.com");
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();

    }

    @Override
    public void onBackPressed() {
        //如果使用fragment，要支持back处理，则需调用BackHandlerHelper.INSTANCE.handleBackPress
        if(!BackHandlerHelper.INSTANCE.handleBackPress(this)){
            super.onBackPressed();
        }
    }

}
