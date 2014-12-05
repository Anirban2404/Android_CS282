package com.anirban.accountAuthenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.anirban.enhanced_content_provider.DownloadActivity;

public class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {

	// tag for logging
	private static final String TAG = "AccountAuthenticatorImpl";

	// Authentication Service context
	private final Context mContext;

	public AccountAuthenticatorImpl(Context context) {
		super(context);
		mContext = context;
		Log.i(TAG, "In AccountAuthenticator");
	}

	/*
	 * The user has requested to add a new account to the system. We return an
	 * intent that will launch our login screen and this will just pass the
	 * user's credentials on to the account manager.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.accounts.AbstractAccountAuthenticator#addAccount(android.accounts
	 * .AccountAuthenticatorResponse, java.lang.String, java.lang.String,
	 * java.lang.String[], android.os.Bundle)
	 */
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		Log.i(TAG, "Add Account..");
		Bundle reply = new Bundle();
		Intent i = new Intent(mContext, DownloadActivity.class);
		i.setAction("android.accounts.AccountAuthenticator");
		i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		reply.putParcelable(AccountManager.KEY_INTENT, i);
		return reply;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.accounts.AbstractAccountAuthenticator#confirmCredentials(android
	 * .accounts.AccountAuthenticatorResponse, android.accounts.Account,
	 * android.os.Bundle)
	 */
	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) {
		Log.d(TAG, "nothing to do");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.accounts.AbstractAccountAuthenticator#editProperties(android.
	 * accounts.AccountAuthenticatorResponse, java.lang.String)
	 */
	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		Log.d(TAG, "Not supported");
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.accounts.AbstractAccountAuthenticator#getAuthToken(android.accounts
	 * .AccountAuthenticatorResponse, android.accounts.Account,
	 * java.lang.String, android.os.Bundle)
	 */
	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle loginOptions)
			throws NetworkErrorException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.accounts.AbstractAccountAuthenticator#getAuthTokenLabel(java.
	 * lang.String)
	 */
	@Override
	public String getAuthTokenLabel(String authTokenType) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.accounts.AbstractAccountAuthenticator#hasFeatures(android.accounts
	 * .AccountAuthenticatorResponse, android.accounts.Account,
	 * java.lang.String[])
	 */
	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.accounts.AbstractAccountAuthenticator#updateCredentials(android
	 * .accounts.AccountAuthenticatorResponse, android.accounts.Account,
	 * java.lang.String, android.os.Bundle)
	 */
	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle loginOptions) {
		return null;
	}
}