package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebViewClient;

public class WebView extends AppCompatActivity {

    private android.webkit.WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent= getIntent();
        if(intent!= null)
        {
            String url= intent.getStringExtra("url");
            if(url!= null)
            {
                webView= findViewById(R.id.webView);
                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(url);
            }

        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack())
            getObbDir();
        else
            super.onBackPressed();
    }
}