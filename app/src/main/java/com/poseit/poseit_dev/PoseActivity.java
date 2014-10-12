package com.poseit.poseit_dev;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.appspot.numeric_ion_678.yorn.Yorn;
import com.appspot.numeric_ion_678.yorn.Yorn.YornEndpoint.NewQuestionSimple;
import com.appspot.numeric_ion_678.yorn.model.Message;

import java.io.IOException;

public class PoseActivity extends Activity {

    private static final String LOG_TAG = "PoseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pose, menu);
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

    public void onClickPoseIt(View view) {
        View rootView = view.getRootView();
        EditText txtQuestion = (EditText)rootView.findViewById(R.id.textView);

        final String questionStr = txtQuestion.getText().toString();

//        try {
//            Log.i(LOG_TAG, "sending...");
//            Yorn apiServiceHandle = AppConstants.getApiServiceHandle();
//            apiServiceHandle.yornEndpoint().newQuestionSimple(questionStr).execute();
//            Log.i(LOG_TAG, "sent!");
//        } catch (IOException ioe) {
//            Log.e(LOG_TAG, "Exception during API call", ioe);
//        }

        // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
        // static-inner or top-level classes to prevent memory leak issues.
        // @see http://goo.gl/fN1fuE @26:00 for a great explanation.
        AsyncTask<String, Void, Message> setNewQuestion =
                new AsyncTask<String, Void, Message> () {
                    @Override
                    protected Message doInBackground(String... strings) {
                        // Retrieve service handle.
                        Yorn apiServiceHandle = AppConstants.getApiServiceHandle();

                        try {
                            Log.i(LOG_TAG, "sending...");
                            NewQuestionSimple nqs = apiServiceHandle.yornEndpoint().newQuestionSimple(questionStr);
                            Message msg = nqs.execute();

                            //GetGreeting getGreetingCommand = apiServiceHandle.greetings().getGreeting(integers[0]);
                            //HelloGreeting greeting = getGreetingCommand.execute();
                            Log.i(LOG_TAG, "sent!");
                            return msg;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Message yorn) {
//                        if (greeting!=null) {
//                            displayGreetings(greeting);
//                        } else {
//                            Log.e(LOG_TAG, "No greetings were returned by the API.");
//                        }
                    }
                };

        setNewQuestion.execute();
    }
}
