package com.poseit.poseit_dev;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.numeric_ion_678.yorn.Yorn;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.common.base.Strings;

import java.io.IOException;


public class LaunchActivity extends ActionBarActivity implements
        PoseFragment.OnPoseFragmentInteractionListener, NaviFragment.OnNaviFragmentInteractionListener {

    private static final String LOG_TAG = "LaunchActivity";

    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;

    private AuthorizationCheckTask mAuthTask;
    private String mEmailAccount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.launch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPoseFragmentInteraction(Uri uri) {

    }

    @Override
    public void onNaviFragmentInteraction(String id) {

    }

    public void onClickPoseIt(View view) {
        if (!isSignedIn()) {
            Toast.makeText(LaunchActivity.this, "You must sign in to pose questions!", Toast.LENGTH_LONG).show();
            return;
        }

        View rootView = view.getRootView();
        EditText txtQuestion = (EditText)rootView.findViewById(R.id.textView);

        final String questionStr = txtQuestion.getText().toString();

        AsyncTask<Void, Void, Void> authenticatedPose =
                new AsyncTask<Void, Void, Void> () {
                    @Override
                    protected Void doInBackground(Void... unused) {
                        if (!isSignedIn()) {
                            return null;
                        };

                        if (!AppConstants.checkGooglePlayServicesAvailable(LaunchActivity.this)) {
                            return null;
                        }

                        // Create a Google credential since this is an authenticated request to the API.
                        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                                LaunchActivity.this, AppConstants.AUDIENCE);
                        credential.setSelectedAccountName(mEmailAccount);

                        // Retrieve service handle using credential since this is an authenticated call.
                        Yorn apiServiceHandle = AppConstants.getApiServiceHandle(credential);

                        try {
                            apiServiceHandle.yornEndpoint().newQuestion("AndroidAuthd", questionStr, mEmailAccount).execute();
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }
                };

        authenticatedPose.execute((Void)null);
    }

    private boolean isSignedIn() {
        if (!Strings.isNullOrEmpty(mEmailAccount)) {
            return true;
        } else {
            return false;
        }
    }

    public void performAuthCheck(String emailAccount) {
        // Cancel previously running tasks.
        if (mAuthTask != null) {
            try {
                mAuthTask.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new AuthorizationCheckTask().execute(emailAccount);
    }

    public void onClickSignIn(View view) {
        TextView emailAddressTV = (TextView) view.getRootView().findViewById(R.id.email_address_tv);
        // Check to see how many Google accounts are registered with the device.
        int googleAccounts = AppConstants.countGoogleAccounts(this);
        if (googleAccounts == 0) {
            // No accounts registered, nothing to do.
            Toast.makeText(this, R.string.toast_no_google_accounts_registered,
                    Toast.LENGTH_LONG).show();
        } else if (googleAccounts == 1) {
            // If only one account then select it.
            Toast.makeText(this, R.string.toast_only_one_google_account_registered,
                    Toast.LENGTH_LONG).show();
            AccountManager am = AccountManager.get(this);
            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            if (accounts != null && accounts.length > 0) {
                // Select account and perform authorization check.
                emailAddressTV.setText(accounts[0].name);
                mEmailAccount = accounts[0].name;
                performAuthCheck(accounts[0].name);
            }
        } else {
            // More than one Google Account is present, a chooser is necessary.

            // Reset selected account.
            emailAddressTV.setText(R.string.auth_pending);

            // Invoke an {@code Intent} to allow the user to select a Google account.
            Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                    "Select an account to sign in with", null, null, null);
            startActivityForResult(accountSelector,
                    ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION && resultCode == RESULT_OK) {
            // This path indicates the account selection activity resulted in the user selecting a
            // Google account and clicking OK.

            // Set the selected account.
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            TextView emailAccountTextView = (TextView)this.findViewById(R.id.email_address_tv);
            emailAccountTextView.setText(accountName);

            // Fire off the authorization check for this account and OAuth2 scopes.
            performAuthCheck(accountName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuthTask!=null) {
            mAuthTask.cancel(true);
            mAuthTask = null;
        }
    }

    class AuthorizationCheckTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... emailAccounts) {
            Log.i(LOG_TAG, "Background task started.");

            if (!AppConstants.checkGooglePlayServicesAvailable(LaunchActivity.this)) {
                return false;
            }

            String emailAccount = emailAccounts[0];
            // Ensure only one task is running at a time.
            mAuthTask = this;

            // Ensure an email was selected.
            if (Strings.isNullOrEmpty(emailAccount)) {
                publishProgress(R.string.toast_no_google_account_selected);
                // Failure.
                return false;
            }

            Log.d(LOG_TAG, "Attempting to get AuthToken");

            try {
                // If the application has the appropriate access then a token will be retrieved, otherwise
                // an error will be thrown.
                GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                        LaunchActivity.this, AppConstants.AUDIENCE);
                credential.setSelectedAccountName(emailAccount);

                String accessToken = credential.getToken();

                Log.d(LOG_TAG, "AccessToken retrieved");

                // Success.
                return true;
            } catch (GoogleAuthException unrecoverableException) {
                Log.e(LOG_TAG, "Exception checking OAuth2 authentication.", unrecoverableException);
                publishProgress(R.string.toast_exception_checking_authorization);
                // Failure.
                return false;
            } catch (IOException ioException) {
                Log.e(LOG_TAG, "Exception checking OAuth2 authentication.", ioException);
                publishProgress(R.string.toast_exception_checking_authorization);
                // Failure or cancel request.
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... stringIds) {
            // Toast only the most recent.
            Integer stringId = stringIds[0];
            Toast.makeText(LaunchActivity.this, stringId, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            mAuthTask = this;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            TextView emailAddressTV = (TextView) LaunchActivity.this.findViewById(R.id.email_address_tv);
            if (success) {
                // Authorization check successful, set internal variable.
                mEmailAccount = emailAddressTV.getText().toString();
            } else {
                // Authorization check unsuccessful, reset TextView to empty.
                mEmailAccount = "";
                emailAddressTV.setText("");
            }
            mAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
