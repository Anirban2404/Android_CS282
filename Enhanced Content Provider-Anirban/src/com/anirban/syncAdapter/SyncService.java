package com.anirban.syncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service to handle Account sync. This is invoked with an intent with action
 * ACTION_AUTHENTICATOR_INTENT. It instantiates the syncadapter and returns its
 * IBinder.
 */
public class SyncService extends Service {

	private static final String TAG = "SyncService";
	private static final Object sSyncAdapterLock = new Object();

	private static SyncAdapterImpl sSyncAdapter = null;

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null) {
				Log.d(TAG, "SyncService: onCreate(): ");
				 sSyncAdapter = new SyncAdapterImpl(getApplicationContext(), true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "SyncService: onBind(): ");
		IBinder result = getSyncAdapter().getSyncAdapterBinder();
		return result;
	}

	private SyncAdapterImpl getSyncAdapter() {
		if (sSyncAdapter == null)
			sSyncAdapter = new SyncAdapterImpl(this,true);
		return sSyncAdapter;
	}
}
