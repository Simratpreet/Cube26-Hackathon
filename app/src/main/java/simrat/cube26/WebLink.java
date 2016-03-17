package simrat.cube26;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by simrat on 12/3/16.
 */
public class WebLink extends AppCompatActivity {
    private String DEBUG_TAG = this.getClass().getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_link);

        Intent data = getIntent();

        String googleDocs = "https://docs.google.com/viewer?url=";
        String url = data.getExtras().getString("url");
        Log.d(DEBUG_TAG, url);
        //WebView myWebView = (WebView) findViewById(R.id.webview);
        //myWebView.loadUrl(googleDocs + url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
