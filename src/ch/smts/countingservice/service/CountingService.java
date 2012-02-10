// Copyright 2012 Johannes Winkelmann, jw@smts.ch
//
//
//  This file is part of CountingService.
//
//  CountingService is free software: you can redistribute it and/or modify 
//  it under the terms of the GNU General Public License as published by the 
//  Free Software Foundation, either version 3 of the License, or (at your 
//  option) any later version.
//
//  CountingService is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
//  more details.
//
//  You should have received a copy of the GNU General Public License along 
//  with CountingService. If not, see http://www.gnu.org/licenses/.

package ch.smts.countingservice.service;

import ch.smts.countingservice.CountingServiceActivity;
import ch.smts.countingservice.R;
import ch.smts.countingservice.service.CountingServiceImpl.CountListener;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CountingService extends Service implements CountListener {
    
    public static final String TAG = "CountingService";
    private static final int TEMP_NOTIFICATION_ID = 1;
    
    
    private CountingServiceImpl mCountingService = new CountingServiceImpl();
    private NotificationManager mNotificationManager;
    
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mCountingService.asBinder();
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        
        mCountingService.addListener(this);
        
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        updateNotification(0);
    }
    
    private void updateNotification(int value) {
        int icon = R.drawable.cs_notification;
        CharSequence tickerText = "Counting service started";
        long when = System.currentTimeMillis();
        
        Notification notification = new Notification(icon, tickerText, when);
        
        Context context = getApplicationContext();
        CharSequence contentTitle = "Count service";
        CharSequence contentText = "Current value: " + value;
        Intent notificationIntent = new Intent(this, CountingServiceActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        
        mNotificationManager.notify(TEMP_NOTIFICATION_ID, notification);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        mNotificationManager.cancel(TEMP_NOTIFICATION_ID);

        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
    
    @Override
    public void newValue(int value) {
        // TODO: rate limit updates
        
        updateNotification(value);
    }
    
    
    // - these are here to see what happens when a client connects / disconnects
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }


}
