package com.poseit.poseit_dev;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.appspot.numeric_ion_678.yorn.Yorn;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.common.base.Strings;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PoseFragment.OnPoseFragmentInteractionListener} interface
 * to handle interaction events.
 * There was a factory method that you could use to create one of these, but it was removed for
 * the time being.
 */
public class PoseFragment extends Fragment {

    private static final String TAG = "PoseFragment";

    private OnPoseFragmentInteractionListener mListener;

//    private String mEmailAccount = "";
//
//    private Activity mHostActivity;

    public PoseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, getClass().getSimpleName() + ":entered onCreateView()");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pose, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, getClass().getSimpleName() + ":entered onAttach(Activity)");
//        mHostActivity = activity;
        try {
            mListener = (OnPoseFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPoseFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onPoseFragmentInteraction(Uri uri);
    }

//    public void setEmailAccount(String emailAccount) {
//        if (emailAccount == null) {
//            mEmailAccount = "";
//        } else {
//            mEmailAccount = emailAccount;
//        }
//    }

//    public void onClickPoseIt(View view) {
//        if (!isSignedIn()) {
//            Toast.makeText(mHostActivity, "You must sign in to pose questions!", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        View rootView = view.getRootView();
//        EditText txtQuestion = (EditText)rootView.findViewById(R.id.textView);
//
//        final String questionStr = txtQuestion.getText().toString();
//
//        AsyncTask<Void, Void, Void> authenticatedPose =
//                new AsyncTask<Void, Void, Void> () {
//                    @Override
//                    protected Void doInBackground(Void... unused) {
//                        if (!isSignedIn()) {
//                            return null;
//                        };
//
//                        if (!AppConstants.checkGooglePlayServicesAvailable(mHostActivity)) {
//                            return null;
//                        }
//
//                        // Create a Google credential since this is an authenticated request to the API.
//                        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
//                                mHostActivity, AppConstants.AUDIENCE);
//                        credential.setSelectedAccountName(mEmailAccount);
//
//                        // Retrieve service handle using credential since this is an authenticated call.
//                        Yorn apiServiceHandle = AppConstants.getApiServiceHandle(credential);
//
//                        try {
//                            apiServiceHandle.yornEndpoint().newQuestion("AndroidAuthd", questionStr, mEmailAccount).execute();
//                        } catch (IOException e) {
//                            Log.e(TAG, "Exception during API call", e);
//                        }
//                        return null;
//                    }
//                };
//
//        authenticatedPose.execute((Void)null);
//    }
//
//    private boolean isSignedIn() {
//        if (!Strings.isNullOrEmpty(mEmailAccount)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
}
