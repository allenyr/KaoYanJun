package com.sharon.allen.a18_sharon.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class WebActivity extends BaseActivity {

	private WebView webview;
	private TextView txtTitle;
	private ProgressBar loadingProgress;
	private String mytitle;

	@Override
	public void initView() {
		setContentView(R.layout.activity_web);
		webview = (WebView) findViewById(R.id.wv);
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		loadingProgress = (ProgressBar) findViewById(R.id.loadingProgress);// 此控件为一个进度条控件，属于自己添加的控件
	}

	@Override
	public void initListener() {
		webListener();
	}

	@Override
	public void initData() {
		// 设置WebView属性，能够执行Javascript脚本
		webview.getSettings().setJavaScriptEnabled(true);
		// 设置可以支持缩放
		webview.getSettings().setSupportZoom(true);
		// 设置出现缩放工具
		webview.getSettings().setBuiltInZoomControls(true);
		//扩大比例的缩放
		webview.getSettings().setUseWideViewPort(true);
		//自适应屏幕
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.getSettings().setLoadWithOverviewMode(true);

		//新页面接收数据
		Bundle bundle = this.getIntent().getExtras();
		//接收imageurl值
		String weburl = bundle.getString("weburl");
		// 加载需要显示的网页
		webview.loadUrl(weburl);
	}

	@Override
	public void processClick(View view) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack(); // goBack()表示返回WebView的上一页面
			webListener();
			return true;
		}
//		Toast.makeText(WebActivity.this, "正在退出", Toast.LENGTH_LONG).show();
		return super.onKeyDown(keyCode, event);
	}

	//webview拦截
	public void webListener(){
		// 此处能拦截超链接的url,即拦截href请求的内容.
		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				mytitle = title;
				super.onReceivedTitle(view, title);
			}

			@Override
			public void onProgressChanged(WebView view, int progress) {
				txtTitle.setText("");
				loadingProgress.setVisibility(View.VISIBLE);
				loadingProgress.setProgress(progress * 100);
				if (progress == 100) {
					loadingProgress.setVisibility(View.GONE);
					txtTitle.setText(mytitle);
				}
				super.onProgressChanged(view, progress);
			}

		});
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}

}
