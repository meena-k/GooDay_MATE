package com.example.mate.gooday_mate;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
// import android.webkit.WebViewClient;
import com.example.mate.gooday_mate.service.Config;

public class GoogleChartTextActivity extends AppCompatActivity {

    private WebView textWebView;
    private WebSettings set_text;
    final Handler handler = new Handler();
    int handlerTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_chart_text);
        handlerTest = 0;
        Log.i("zzzHandlerCounttextoncreate", "oncreate");
        initView();
    }


    private void initView() {

        /*온도 맥박 텍스트*/
        textWebView = findViewById(R.id.textWebView);
        textWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        textWebView.setWebViewClient(new WebViewClient());
        textWebView.setInitialScale(190);
        set_text = textWebView.getSettings();
        set_text.setJavaScriptEnabled(true);
        set_text.setBuiltInZoomControls(true);
        //set_temp.setLoadWithOverviewMode(true);
        textWebView.loadUrl(Config.URL + "/google_chart_data_text.php");


        //핸들러로 webView Reload
        reloadView();

    }

    private void reloadView() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handlerTest++;
                Log.i("zzzHandlerCounttext", String.valueOf(handlerTest));
                textWebView.reload();
                reloadView();
            }
        }, 5000);

    }

    /*Activity focus 잃었을 때 핸들러 종료*/
    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeMessages(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadView();
    }


}