package com.anirban.accountAuthenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AccountAuthenticatorService extends Service {
	private static final String TAG = "AccountAuthenticatorService";
	private static AccountAuthenticatorImpl sAccountAuthenticator = null;

	// Calling the constructor
	public AccountAuthenticatorService() {
		super();
	}

	public IBinder onBind(Intent intent) {
		IBinder ret = null;
		System.out.println(intent.getAction());
		if (intent.getAction().equals(
				android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
			ret = getAuthenticator().getIBinder();
		// returning the IBinder
		return ret;
	}

	private AccountAuthenticatorImpl getAuthenticator() {
		if (sAccountAuthenticator == null)
			sAccountAuthenticator = new AccountAuthenticatorImpl(this);
		Log.i(TAG, sAccountAuthenticator.toString());
		return sAccountAuthenticator;
	}

}
