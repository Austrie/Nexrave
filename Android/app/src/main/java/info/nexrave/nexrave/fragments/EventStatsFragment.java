package info.nexrave.nexrave.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.nexrave.nexrave.EventInfoActivity;
import info.nexrave.nexrave.R;



public class EventStatsFragment extends Fragment {

    public static boolean justInit = false;


    private OnFragmentInteractionListener mListener;

    public EventStatsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EventStatsFragment newInstance() {
        Log.d("EventStatsFragment", "Instance");
        EventStatsFragment fragment = new EventStatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        justInit = true;
        Log.d("EventStatsFragment", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("EventStatsFragment", "onCreateView");
        return inflater.inflate(R.layout.fragment_event_stats, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("EventStatsFragment", "onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public void onResume() {
        Log.d("EventStatsFragment", "onResume");
        super.onResume();
        if (justInit) {
            justInit = false;
            ViewPager vp = (ViewPager) getActivity().findViewById(R.id.eventInfoContainer);
            vp.setCurrentItem(1);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("EventStatsFragment", "Detach");
        mListener = null;
    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
