package app.gree.finalapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class FullNews extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_news);
        Bundle bundle=getIntent().getExtras();
        String link=bundle.getString("url_link");
        webView=(WebView) findViewById(R.id.web_view);
        webView.loadUrl(link);
    }

}
