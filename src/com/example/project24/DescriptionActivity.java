package com.example.project24;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


public class DescriptionActivity extends Activity {
	WebView wb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.description_lyt);
		Intent i = getIntent();
		String url_link = i.getStringExtra("url_link");
		String title = getIntent().getStringExtra("title");
		String description = getIntent().getStringExtra("description");
		TextView  tv = (TextView) findViewById(R.id.title);
		tv.setText(title);
		
		wb = (WebView) findViewById(R.id.description);
		wb.getSettings().setJavaScriptEnabled(true);
		wb.loadUrl(url_link);
		wb.setWebViewClient(new DisplayingNewsPageClient());	
	}

	@Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if ((keyCode == KeyEvent.KEYCODE_BACK) && wb.canGoBack()) {
	            wb.goBack();
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	    }
	private class DisplayingNewsPageClient extends WebViewClient{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
