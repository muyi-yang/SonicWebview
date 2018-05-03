# SonicWebview使用文档
[TOC]
## 简介
SonicWebview是android平台的网址访问库，其内核是腾讯[VasSonic](https://github.com/Tencent/VasSonic)，可以提高网址浏览速度，对于提速原理有兴趣的可以查看[VasSonic android 文档](https://github.com/Tencent/VasSonic/tree/master/sonic-android/docs)。在此基础上封装了一些常规操作和配置，可使快速集成webview，不必在乎各种配置细节。库全部使用kotlin实现，可以和java无缝使用，如果遇到使用问题请当面咨询。
## 配置
在项目build.gradle中添加依赖：
``` gradle
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'http://nexus.hz.halove.org/nexus/content/repositories/android/' }
    }
}
dependencies {
    compile "com.halove.android.tools:SonicWebLib:0.0.6_alpha"
}
```
## 快速使用
#### 使用默认Activity打开URL
BrowseActivity可一定程度的定制ActionBar，具体参数请看下面代码：
``` java
Intent intent = new Intent(MainActivity.this, BrowseActivity.class);
intent.putExtra(Constants.INSTANCE.getPARAM_URL(), "http://www.halove.com");//必需，访问的地址
intent.putExtra(Constants.INSTANCE.getPARAM_BACK_FORCE_FINISH(), false);//可选，点击头部的返回按钮是否强制结束Activity，默认false
intent.putExtra(Constants.INSTANCE.getPARAM_ACTIONBAR_SHOW(), false);//可选，是否显示Activity ActionBar，默认true
intent.putExtra(Constants.INSTANCE.getPARAM_ACTIONBAR_BACK_SHOW(), false);//可选，是否显示ActionBar中的返回按钮，默认true
intent.putExtra(Constants.INSTANCE.getPARAM_TITLE(), "测试title");//可选，给定title，否则自动获取网页title
startActivity(intent);
```
#### 继承BaseWebActivity，实现自定义页面
继承BaseWebActivity实现自定义页面，需遵守以下几个规则：
1. setContentView布局中必需包含一个id为@+id/webview的WebView控件，类似于ListActivity的设计。例如：
``` xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
	<xxx />
    <WebView
        android:id="@+id/webview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"></WebView>
    <xxx />
</LinearLayout>
```
2. 子类必需重写getUrl()方法，以确认访问地址。
``` java
@NotNull
@Override
public String getUrl() {
    return "http://www.halove.com";
}
```

#### 使用默认Fragment打开URL
``` java
Fragment fragment = new BrowseFragment();
Bundle args = new Bundle();
args.putString(Constants.INSTANCE.getPARAM_URL(), "http://www.halove.com");//必需，访问的地址
fragment.setArguments(args);
//fragment怎么添加到界面，根据你的需求而定
```
#### 继承BaseWebFragment，实现自定义页面
继承BaseWebFragment，实现自定义页面，实现方式和规则跟BaseWebActivity一样，布局中必需包含一个id为@+id/webview的WebView控件，并重写getUrl()方法。请参考BaseWebActivity用法。

-----------
## 扩展使用
#### 配置加载样式及无网提醒
配置加载样式及无网提醒需要子类重写getWebViewStyleConfig()方法，返回WebViewStyleConfig样式配置实体。Activity和Fragment都一样。
``` java
@NotNull
@Override
public WebViewStyleConfig getWebViewStyleConfig() {
    WebViewStyleConfig config = new WebViewStyleConfig();
//        config.setShowLoadProgress(false);//显示加载进度
//        config.setShowLoadErrorLayout(false);//显示加载错误布局

//        config.setShowErrorIcon(false);//显示错误icon图标
    config.setLoadErrorIconRes(R.drawable.ic_error);//加载错误icon提醒,也可以调用setLoadErrorIconBitmap方法
//        config.setLoadErrorIconBitmap(errorBitmap);

//        config.setShowErrorText(false);//显示加载错误提醒文字
    config.setLoadErrorText("加载错误，请检查网络");//设置加载错误文字
    config.setLoadErrorTextColor(Color.RED);//加载错误文字颜色

//        config.setShowErrorBtn(false);//显示错误提醒按钮
    config.setLoadErrorBtnTextColor(Color.YELLOW);//设置加载错误提醒按钮颜色
    config.setLoadErrorBtnText("重新加载");//设置加载错误提醒按钮文字
    config.setLoadErrorBtnBgRes(R.drawable.ic_selected);//设置加载错误提醒按钮背景

    config.setFullScreenRefreshMode(true);//设置点击全屏刷新模式，只在加载错误后，显示错误页面起效
    return config;
}
```
#### VasSonic相关的配置
一般情况下不需要用到VasSonic相关的配置，如果需要，请确保你清楚VasSonic的原理及使用，[VasSonic使用指南](https://github.com/Tencent/VasSonic/blob/master/sonic-android/docs/Sonic%E6%8E%A5%E5%85%A5%E6%8C%87%E5%BC%95.md)。重写以下这些方法实现自定义功能（以下为kotlin实现，java类似）：
``` java
fun getSonicRuntime(context: Context): SonicRuntime {
    return CustomSonicRuntime(context)//自定义SonicRuntime
}

fun getSonicSessionClient(): SonicSessionClient {
    return CustomSonicSessionClient()//自定义SonicSessionClient
}

fun getSonicConfig(): SonicConfig {
    return SonicConfig.Builder().build()//手动配置SonicConfig
}

fun getSonicSessionConfig(): SonicSessionConfig {
    return SonicSessionConfig.Builder().build()//手动配置SonicSessionConfig
}
```
#### WebViewManager的介绍
WebViewManager是webview管理封装类，管理着VasSonic的使用和webview本身的一些配置，及加载错误时提醒的页面样式。WebViewManager对象在BaseWebActivity和BaseWebFragment中是保护型的，子类可随时使用。还有一个对象同样是保护型的，就是webview对象，此处设计不是很合理，本想封死webview的引用，但是又担心有某些需求需要用到，所以在父类开放了。后续可能会封装死，在没有更好的设计前暂时开放。

**loadUrl(url: String)**：加载网页。

**setWebViewClient(webViewClient: WebViewClient)**：为webview设置WebViewClient，必须使用此方法，否则VasSonic的加速效果将无效。

**setWebChromeClient(webChromeClient: WebChromeClient)**：为webview设置WebChromeClient，必须使用此方法，否则会影响进度显示和加载错误提醒的view的显示。

**setWebViewStyleConfig(config: WebViewStyleConfig)**：设置加载错误提醒view风格配置。继承Base类不需要手动使用此方法，请重写父类的getWebViewStyleConfig方法，只有整个页面都自己实现时才会用到。不建议这么做，否则就没必要集成此库，尽量使用默认页面或继承Base类实现，如果遇到问题可及时反馈，扩展库来达到要求。

**goBack()**：调用其返回上一页浏览，继承Base类不需要手动使用此方法，只有整个页面都自己实现时才会用到。不建议这么做，尽量使用默认页面或继承Base类实现，如果遇到问题可及时反馈，扩展库来达到要求。

**onDestroy()**：释放VasSonic的一些资源。继承Base类不需要手动使用此方法，只有整个页面都自己实现时才会用到，在页面销毁时调用。不建议这么做，尽量使用默认页面或继承Base类实现，如果遇到问题可及时反馈，扩展库来达到要求。
## 源码及demo
地址：[https://github.com/muyi-yang/SonicWebview](https://github.com/muyi-yang/SonicWebview)
