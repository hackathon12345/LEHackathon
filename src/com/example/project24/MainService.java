package com.example.project24;


import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MainService extends Service implements GetMessageListener{
	
	private static final int SERVICE_CONNECTION_ACK = 1;
	private static final int MESSAGE_RECEIVED = 2;
	private Messenger clientMessenger = null;
	private String msg = null;
	@Override
	public void onCreate() {
		new GetMessageTask(this).execute();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}

	private Messenger messenger = new Messenger(new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == SERVICE_CONNECTION_ACK){
				clientMessenger = msg.replyTo;
				sendMessageToClient();
			}else{
				super.handleMessage(msg);
			}
		}
	});
	@Override
	public IBinder onBind(Intent arg0) {		
		return messenger.getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		clientMessenger = null;
		return false;
	}
	/**
	 * received the message from GetMessageTask
	 */
	@Override
	public void onMessageReceived(String msg) {
		this.msg = msg;
		sendMessageToClient();
	}
	/**
	 * send the message to the client (MainActivity), if the client exists
	 */
	private void sendMessageToClient(){
		if(this.msg != null){
			if(clientMessenger != null){
				Message msg = Message.obtain();
				msg.what = MESSAGE_RECEIVED;
				Bundle data = new Bundle();
				data.putString("message", this.msg);
				msg.setData(data);
				try {
					Log.i("MainService","sending message "+this.msg);
					clientMessenger.send(msg);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				stopSelf();
			}else{
				
				PendingIntent pintent = PendingIntent.getActivity(
						this, 0, new Intent(this,MainActivity.class), 0);
				NotificationCompat.Builder noti = 
						new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.trees)
						.setContentIntent(pintent)
						.setContentTitle("Message received")
						.setContentText("New news to read")
						.setAutoCancel(true);
				NotificationManager nmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				nmanager.notify(0, noti.build());
			}
		}
	}

}
