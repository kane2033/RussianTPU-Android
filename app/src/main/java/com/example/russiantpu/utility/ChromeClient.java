package com.example.russiantpu.utility;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.webkit.WebChromeClient;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

//вспомогателльный класс для
//компонента WebView
public class ChromeClient extends WebChromeClient {

    private WebView webView;
    private FrameLayout fullScreenContainer;
    private View fullScreenView;
    private CustomViewCallback fullScreenViewCallback;

    public ChromeClient(WebView webView, FrameLayout fullScreenView) {
        this.webView = webView;
        this.fullScreenContainer = fullScreenView;
    }

    @Override
    public void onShowCustomView(View v, CustomViewCallback c) {
        webView.setVisibility(View.GONE);
        fullScreenContainer.setVisibility(View.VISIBLE);
        fullScreenContainer.addView(v); //??? было view

        fullScreenView = v;
        fullScreenViewCallback = c;
    }

    @Override
    public void onHideCustomView() {
        fullScreenContainer.removeView(fullScreenView);
        fullScreenViewCallback.onCustomViewHidden();
        fullScreenView = null;

        webView.setVisibility(View.VISIBLE);
        fullScreenContainer.setVisibility(View.GONE);
    }
}
