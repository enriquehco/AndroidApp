package com.example.myapplication;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DisplayDialogFlow extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_dialogflow);

        WebView webView;
        webView = (WebView) findViewById(R.id.chatbotview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://console.dialogflow.com/api-client/demo/embedded/7d72ba09-0b8e-46c9-8205-bdb2db811b97");
    }
}
