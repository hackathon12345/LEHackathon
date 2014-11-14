
package com.example.project24;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

public class MainActivity extends ListActivity {
	private static final int SERVICE_CONNECTION_ACK = 1;
	private static final int MESSAGE_RECEIVED = 2;
	
	private Messenger messenger = new Messenger(new Handler(){
		@Override
		public void handleMessage(Message  msg){
			Log.i("MainAcitivity","got message");
			if(msg.what==MESSAGE_RECEIVED){

				Bundle data =msg.getData();
				String message = data.getString("message");
				Log.i("MainActivity","received message" + message);
				TextView tv =(TextView) MainActivity.this.findViewById(R.id.message);
				tv.setText(message);
			}
		}
	});
	private ServiceConnection serviceConn = new ServiceConnection(){
		@Override
		public void onServiceConnected (ComponentName className , IBinder service){
			Log.i("MainActivity", "bound to service");
			Messenger serviceMessenger = new Messenger(service);
			Message message =Message.obtain();
			message.what = SERVICE_CONNECTION_ACK;
			message.replyTo = messenger;
			try{
				serviceMessenger.send(message);
			}catch(RemoteException e){
				e.printStackTrace();
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName className){
			
		}
	};
   
  private RSSListAdapter MyAdapter;
    
  private EditText UrlText;

  private TextView Text;
   
  private Handler handler;

  private RSSWorker MyWorker;

  public static final int LENGTH = 100;
    
  public static final String STRINGS_KEY = "strings";

  public static final String SELECTION_KEY = "selection";

  public static final String URL_KEY = "url";
    
  public static final String STATUS_KEY = "status";
   
  @Override
  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);	
        
        Intent i = new Intent(this , MainService.class);
		startService(i);
		bindService(i, serviceConn, 0);
        
        RssDataBase db= new RssDataBase(this);
        db.insertNewsSite(new NewsSite());
        List<NewsSite> list = db.getSite();
        db.deleteNewsSite(list.get(0));
        db.getSite();
        
        List<RssItem> items = new ArrayList<RssItem>();
        MyAdapter = new RSSListAdapter(this, items);
        getListView().setAdapter(MyAdapter);

        UrlText = (EditText)findViewById(R.id.text);
        Text = (TextView)findViewById(R.id.statustext);
        
        Button download = (Button)findViewById(R.id.download);
        download.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doRSS(UrlText.getText().toString());
            }
        });

        handler = new Handler();   
    }
  		@Override
  		protected void onDestroy(){
  				super.onDestroy();
  				unbindService(serviceConn);
	}
  

  private class RSSListAdapter extends ArrayAdapter<RssItem> {
        private LayoutInflater MyInflater;
		

        public  RSSListAdapter(Context context, List<RssItem> objects) {
            super(context, 0, objects);

            MyInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
     
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TwoLineListItem view;

            if (convertView == null) {
                view = (TwoLineListItem) MyInflater.inflate(android.R.layout.simple_list_item_2,
                        null);
            } else {
                view = (TwoLineListItem) convertView;
            }

            final RssItem item = this.getItem(position);
            view.getText1().setText(item.getTitle());
            String descr = item.getDescription().toString();
           descr = removeTags(descr);
           view.getText2().setText(descr.substring(0, Math.min(descr.length(), LENGTH)));
            return view;
        }
    }
  
  public String removeTags(String str) {
        str = str.replaceAll("<.*?>", " ");
        str = str.replaceAll("\\s+", " ");
        return str;
    }
    

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
        RssItem item = MyAdapter.getItem(position);
        
        Intent intent = new Intent(MainActivity.this, DescriptionActivity.class);
        intent.putExtra("title",item.getDescription());
        intent.putExtra("link",item.getLink().toString());
  
        startActivity(intent);
    }

  public void resetUI() {
        List<RssItem> items = new ArrayList<RssItem>();
        MyAdapter = new RSSListAdapter(this, items);
        getListView().setAdapter(MyAdapter);

        Text.setText("");
        UrlText.requestFocus();
    }

    
  public synchronized void setCurrentWorker(RSSWorker worker) {
        if (MyWorker != null) MyWorker.interrupt();
         MyWorker = worker;
    }

    
  public synchronized boolean isCurrentWorker(RSSWorker worker) {
        return (MyWorker == worker);
    }

  private void doRSS(String RssUrl) {
        RSSWorker worker = new RSSWorker(RssUrl);
        setCurrentWorker(worker);

        resetUI();
        Text.setText("Downloading");

        worker.start();
    }

   
  private class ItemAdder implements Runnable {
        RssItem MyItem;

        ItemAdder(RssItem item) {
            MyItem = item;
        }

        public void run() {
            MyAdapter.add(MyItem);
        }

    }

   
  private class RSSWorker extends Thread {
        private String MyUrl;

        public RSSWorker(String url) {
            MyUrl = url;
        }

        @Override
        public void run() {
            String status = "";
            try {
               
                URL url = new URL(MyUrl.toString());
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(10000);

                connection.connect();
                InputStream in = connection.getInputStream();

                parseRSS(in, MyAdapter);
               
            } catch (Exception e) {
                status = "failed:" + e.getMessage();
            }

            final String temp = status;
            if (isCurrentWorker(this)) {
                handler.post(new Runnable() {
                    public void run() {
                        Text.setText(temp);
                    }
                });
            }
        }
    }

    
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 0, 0, "CNN")
            .setOnMenuItemClickListener(new RSSMenu("http://rss.cnn.com/rss/cnn_topstories.rss"));

        menu.add(0, 0, 0, "Reset")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                resetUI();
                return true;
            }
        });

        return true;
    }

   
  private class RSSMenu implements MenuItem.OnMenuItemClickListener {
        private String MyUrl;

        RSSMenu(String url) {
            MyUrl = url;
        }

        public boolean onMenuItemClick(MenuItem item) {
            Text.setText(MyUrl);
            Text.requestFocus();
            return true;
        }
    }


  @Override
  protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int count = MyAdapter.getCount();

        ArrayList<String> strings = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            RssItem item = 	MyAdapter.getItem(i);
            strings.add(item.getTitle());
            //estrings.add(item.getLink());
            strings.add(item.getDescription()); 
        }
        outState.putSerializable(STRINGS_KEY, strings);

        if (getListView().hasFocus()) {
            outState.putInt(SELECTION_KEY, Integer.valueOf(getListView().getSelectedItemPosition()));
        }

        outState.putString(URL_KEY, UrlText.getText().toString());
       
        outState.getString(STATUS_KEY);
    }

   
  @SuppressWarnings("unchecked")
  @Override
  protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

    
        if (state == null) return;

        List<String> strings = (ArrayList<String>)state.getSerializable(STRINGS_KEY);
        List<RssItem> items = new ArrayList<RssItem>();
        for (int i = 0; i < strings.size(); i += 3) {
            items.add(new RssItem(strings.get(i), strings.get(i + 1), strings.get(i + 2), strings.get(i + 3)));
        }

        MyAdapter = new RSSListAdapter(this, items);
        getListView().setAdapter(MyAdapter);

       
        if (state.containsKey(SELECTION_KEY)) {
            getListView().requestFocus(View.FOCUS_FORWARD);
            
            getListView().setSelection(state.getInt(SELECTION_KEY));
        }
        
        UrlText.setText(state.getString(URL_KEY));
        
        Text.setText(state.getString(STATUS_KEY));
    }

    void parseRSS(InputStream in, RSSListAdapter adapter) throws IOException,
            XmlPullParserException {
       

        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(in, null);  

        int eventType;
        String title = "";
        String link = "";
        String description = "";
        eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tag = xpp.getName();
                if (tag.equals("item")) {
                    title = link = description = "";
                } else if (tag.equals("title")) {
                    xpp.next(); 
                    title = xpp.getText();
                } else if (tag.equals("link")) {
                    xpp.next();
                    link = xpp.getText();
                } else if (tag.equals("description")) {
                    xpp.next();
                    description = xpp.getText();
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                String tag = xpp.getName();
                if (tag.equals("item")) {
                    RssItem item = new RssItem(title, link, description, null );
                    handler.post(new ItemAdder(item));
                }
            }
            eventType = xpp.next();
        }
    }
}


