package info.nexrave.nexrave.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;

import info.nexrave.nexrave.EventInfoActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.systemtools.VerticalViewPager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VerticalViewPagerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VerticalViewPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerticalViewPagerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static VerticalViewPager mViewPager;
    private static FirebaseUser user;
    private static Event event;
    private static Activity context;

    public VerticalViewPagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * //     * @param param1 Parameter 1.
     * //     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment VerticalViewPagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerticalViewPagerFragment newInstance(FirebaseUser firebaseUser,
                                                        Activity activity, Event selectedEvent) {
        VerticalViewPagerFragment fragment = new VerticalViewPagerFragment();
        user = firebaseUser;
        event = selectedEvent;
        context = activity;
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vertical_view_pager, container, false);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (VerticalViewPager) view.findViewById(R.id.eventInfo_chat_camera_Container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        return view;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a HostMainFragment (defined as a static inner class below).
            switch (position) {
                case (0):
                    Log.d("EventInfo", "Chat");
                    return EventChatFragment.newInstance(user, context, event.event_id);
                case (1):
                    Log.d("EventInfo", "Camera");
                    return CameraFragment.newInstance(context, event.event_id);
            }
            Log.d("EventInfo", "Non-selected");
            return EventChatFragment.newInstance(user, context, event.event_id);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Event Chat";
                case 1:
                    return "Event Camera";
            }
//
//                case (1):
//                    switch (position) {
//                        case 0:
//                            return "Private";
//                        case 1:
//                            return "Public";
//                    }
//                    break;
//            }

            return "Event Chat";
        }
    }

    public static boolean backToChat() {
        if (mViewPager == null) {
            return false;
        }
        int position = mViewPager.getCurrentItem();
        if (position == 1) {
            mViewPager.setCurrentItem(0, true);
            return true;
        } else {
            return false;
        }
    }

    public static boolean toCamera() {
        if (mViewPager == null) {
            return false;
        }
        int position = mViewPager.getCurrentItem();
        if (position == 0) {
            mViewPager.setCurrentItem(1, true);
            return true;
        } else {
            return false;
        }
    }
}
