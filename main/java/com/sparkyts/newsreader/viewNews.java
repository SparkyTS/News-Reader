package com.sparkyts.newsreader;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class viewNews extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        final ProgressDialog pd = ProgressDialog.show(this, "", "Loading Page...", true);
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            /**The below code slows down a process since it will only allow user to surf content after all the content of web-page is loaded.*/
//            public void onReceivedError(WebView view, int errCode, String description, String failingUrl) {
//                Toast.makeText(viewNews.this, description, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                pd.show();
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                pd.dismiss();
//            }
        });
        String url = getIntent().getStringExtra("news-url");
        webView.loadUrl(url);

        /**
         * I find, it usually takes 3 second to start rendering web-page content so, just stopping for 3secs to partially load the page.*/
        pd.show();
        new CountDownTimer(3000,1000){
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                pd.dismiss();
                pd.cancel();
            }
        }.start();
    }
}
