package com.poseit.poseit_dev;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.numeric_ion_678.yorn.Yorn;
import com.appspot.numeric_ion_678.yorn.model.YesNoQuestion;
import com.appspot.numeric_ion_678.yorn.model.YesNoQuestionListResponce;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.common.base.Strings;
import com.poseit.poseit_dev.dummy.DummyContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PoseSelectionFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String LOG_TAG = "PoseSelectionFragment";

    private static final String ARG_EMAIL = "emailAccount";
    private String mEmailAccount = "";

    //private Activity mHostActivity;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    private TextView mTextView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    public static PoseSelectionFragment newInstance(String paramEmail) {
        PoseSelectionFragment fragment = new PoseSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, paramEmail);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PoseSelectionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mEmailAccount = getArguments().getString(ARG_EMAIL);
        }

    //FROM HERE; NEED TO FIGURE OUT THE CORRECT WAY TO POPULATE THE LIST FROM THE API CALL

        // Initially empty
        mAdapter = new ArrayAdapter<YesNoQuestion>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<YesNoQuestion>());
    }

    private void getPoseSelection() {
        if (!isSignedIn()) {
            Toast.makeText(getActivity(), "You must sign in for this action.", Toast.LENGTH_LONG).show();
            return;
        }

        final Activity hostActivity = getActivity();

        AsyncTask<Void, Void, YesNoQuestionListResponce> getPoseSelectionFromBackend =
                new AsyncTask<Void, Void, YesNoQuestionListResponce> () {
                    @Override
                    protected YesNoQuestionListResponce doInBackground(Void... unused) {
                        if (!isSignedIn()) {
                            return null;
                        };

                        if (!AppConstants.checkGooglePlayServicesAvailable(hostActivity)) {
                            return null;
                        }

                        // Create a Google credential since this is an authenticated request to the API.
                        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                                hostActivity, AppConstants.AUDIENCE);
                        credential.setSelectedAccountName(mEmailAccount);

                        // Retrieve service handle using credential since this is an authenticated call.
                        Yorn apiServiceHandle = AppConstants.getApiServiceHandle(credential);

                        try {
                            return apiServiceHandle.yornEndpoint().listAllUsersQuestions().execute();
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(YesNoQuestionListResponce response) {
                        if (response != null) {
                            displayQuestions(response.getQuestions());
                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                            return;
                        }
                    }
                };

        getPoseSelectionFromBackend.execute((Void)null);
    }

    private void displayQuestions(List<YesNoQuestion> questionList) {
        ((ArrayAdapter<YesNoQuestion>)mAdapter).addAll(questionList);

        ((ArrayAdapter<YesNoQuestion>)mAdapter).notifyDataSetChanged();
    }

    private boolean isSignedIn() {
        if (!Strings.isNullOrEmpty(mEmailAccount)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poseselection, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        mTextView = (TextView) view.findViewById(android.R.id.empty);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        //mHostActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEmailAccount = "";
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
}
