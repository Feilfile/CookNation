package com.ema.cooknation.ui

import android.webkit.WebView

import android.webkit.WebViewClient

class myWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }
}