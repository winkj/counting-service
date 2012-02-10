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

import java.util.ArrayList;

import android.os.Handler;
import android.os.RemoteException;
import ch.smts.countingservice.ICountingService;

public class CountingServiceImpl extends ICountingService.Stub {
    
    public interface CountListener {
        void newValue(int value);
    }
    
    public static final int UPDATE_DELAY = 1000;
    
    private boolean mIsCounting;
    private int mCount = -1;
    
    private ArrayList<CountListener> mListeners = new ArrayList<CountingServiceImpl.CountListener>();
    
    Handler mHandler = new Handler();
    Runnable mCountingRunnable = new Runnable() {
        @Override
        public void run() {
            ++mCount;
            for (CountListener l : mListeners) {
                l.newValue(mCount);
            }
            mHandler.postDelayed(this, UPDATE_DELAY);
        }
    };
    
    @Override
    public void startCounting() throws RemoteException {
        mIsCounting = true;
        mCount = 0;
        mHandler.post(mCountingRunnable);
    }
    
    @Override
    public void stopCounting() throws RemoteException {
        mHandler.removeCallbacks(mCountingRunnable);
        mIsCounting = false;
    }
    
    @Override
    public boolean isCounting() throws RemoteException {
        return mIsCounting;
    }
    
    @Override
    public int getCount() throws RemoteException {
        return mCount;
    }

    public void addListener(CountListener l) {
        mListeners.add(l);
    }
    
}
