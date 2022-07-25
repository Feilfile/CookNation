package com.ema.cooknation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {


    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view)

        val webView : WebView = findViewById(R.id.webView)
        webView.setWebViewClient(myWebViewClient())
        webView.apply {
            loadUrl("https://www.facebook.com/Cooknation-107542382025670")
            settings.javaScriptEnabled = true
            settings.safeBrowsingEnabled = true
            settings.domStorageEnabled = true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}