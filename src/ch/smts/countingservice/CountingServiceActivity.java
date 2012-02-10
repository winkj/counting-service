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

package ch.smts.countingservice;

import ch.smts.countingservice.service.CountingService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

public class CountingServiceActivity extends Activity {
    
    private ServiceConnection mConnection;
    private ICountingService mCountingService;
    
    private boolean mServiceConnected = false;
    
    
    // - service
    
    private class CountingServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceConnected = true;
            mCountingService = ICountingService.Stub.asInterface(service);
            updateUi();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceConnected = false;
            mCountingService = null;
            updateUi();
        }
    }
    
    
    private void connectToService() {
        Intent i = new Intent(this, CountingService.class);
        startService(i);
        
        mConnection = new CountingServiceConnection();
        bindService(i, mConnection, BIND_AUTO_CREATE);
    }
    
    private void disconnectFromService() {
        if (mServiceConnected) {
            unbindService(mConnection);
            mConnection = null;
            mCountingService = null;
            mServiceConnected = false;
            updateUi();
        }
    }
    
    private void stopService() {
        Intent i = new Intent(this, CountingService.class);
        stopService(i);
        
        updateUi();
    }
    
    
    // - UI
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        updateUi();
    }
    
    @Override
    public void onDestroy() {
        disconnectFromService();
        super.onDestroy();
    }
    
    
    
    @Override
    protected void onPause() {
        disconnectFromService();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        connectToService();
    }
    
    public void handleButton(View v) {
        switch (v.getId()) {
        case R.id.buttonStartService:
            connectToService();
            break;
        case R.id.buttonStopService:
            disconnectFromService();
            stopService();
            break;
            
        case R.id.buttonStartCounting:
            startCounting();
            break;
        case R.id.buttonStopCounting:
            stopCounting();
            break;
            
        case R.id.buttonGetCount:
            getCount();
            break;
        }		
    }
    
    
    private void startCounting() {
        try {
            mCountingService.startCounting();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        updateUi();
    }
    
    private void stopCounting() {
        try {
            mCountingService.stopCounting();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        updateUi();
    }
    
    
    private void getCount() {
        TextView tv = (TextView) findViewById(R.id.textViewCount);
        try {
            tv.setText("" + mCountingService.getCount());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    public void updateUi() {	
        findViewById(R.id.buttonStartService).setEnabled(!mServiceConnected);
        findViewById(R.id.buttonStopService).setEnabled(mServiceConnected);
        findViewById(R.id.buttonGetCount).setEnabled(mServiceConnected);
        
        if (mCountingService != null) {
            try {
                boolean counting = mCountingService.isCounting();
                findViewById(R.id.buttonStartCounting).setEnabled(!counting);
                findViewById(R.id.buttonStopCounting).setEnabled(counting);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            findViewById(R.id.buttonStartCounting).setEnabled(false);
            findViewById(R.id.buttonStopCounting).setEnabled(false);
        }
    }
    
}