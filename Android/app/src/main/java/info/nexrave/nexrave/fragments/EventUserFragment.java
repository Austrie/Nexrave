package info.nexrave.nexrave.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.newsfeedparts.AppController;
import info.nexrave.nexrave.systemtools.FireDatabase;
import info.nexrave.nexrave.systemtools.RoundedNetworkImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventUserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventUserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static String current_user;
    private static TextView usernameTV;
    private static RoundedNetworkImageView profile_picIV;
    private static NetworkImageView backgroundIV;
    private static ImageButton facebookIB, instagramIB, twitterIB, snapchatIB;

    private OnFragmentInteractionListener mListener;

    public EventUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment EventUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventUserFragment newInstance() {
        EventUserFragment fragment = new EventUserFragment();
        Bundle args = new Bundle();
        current_user = FireDatabase.backupFirebaseUser.getUid();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event_user, container, false);
        backgroundIV = (NetworkImageView) v.findViewById(R.id.eventUser_background);
        profile_picIV = (RoundedNetworkImageView) v.findViewById(R.id.eventUser_user_profile_pic);
        facebookIB = (ImageButton) v.findViewById(R.id.eventUser_facebook);
        instagramIB = (ImageButton) v.findViewById(R.id.eventUser_instagram);
        twitterIB = (ImageButton) v.findViewById(R.id.eventUser_twitter);
        snapchatIB = (ImageButton) v.findViewById(R.id.eventUser_snapchat);
        usernameTV = (TextView) v.findViewById(R.id.eventUser_username);
        return v;
    }

    public static void setUserToBeViewed(String u) {
        current_user = u;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            Activity a = getActivity();
            if (a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("EventUserFragment", "onResume called");

        DatabaseReference userRef = FireDatabase.getRoot().child("users").child(current_user);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profile_picIV.setImageUrl((String) dataSnapshot.child("pic_uri").getValue()
                        , AppController.getInstance().getImageLoader());
                backgroundIV.setImageUrl((String) dataSnapshot.child("pic_uri").getValue()
                        , AppController.getInstance().getImageLoader());
                usernameTV.setText((String) dataSnapshot.child("name").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void loadUser(String user_id) {
        DatabaseReference userRef = FireDatabase.getRoot().child("users").child(user_id);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profile_picIV.setImageUrl((String) dataSnapshot.child("pic_uri").getValue()
                        , AppController.getInstance().getImageLoader());
                backgroundIV.setImageUrl((String) dataSnapshot.child("pic_uri").getValue()
                        , AppController.getInstance().getImageLoader());
                usernameTV.setText((String) dataSnapshot.child("name").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
}
