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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.feedparts.FeedListAdapter;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.systemtools.ArrayListEvents;
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
    private static NetworkImageView largeIV;
    private static RelativeLayout largeIVContainer;
    private static String pic_uri;
    private static FeedListAdapter adapter;
    private static ArrayListEvents<Event> listOfEvents;
    private static ImageButton facebookIB, instagramIB, twitterIB, snapchatIB;

    private OnFragmentInteractionListener mListener;

    public EventUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * //     * @param param1 Parameter 1.
     * //     * @param param2 Parameter 2.
     *
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
        largeIVContainer = (RelativeLayout) v.findViewById(R.id.eventUser_large_IV_container);
        largeIVContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (largeIVContainer.getVisibility() == View.VISIBLE) {
                    Log.d("EventInfoActivity", "Was Visible");
                    largeIVContainer.setVisibility(View.GONE);
                }
            }
        });
        largeIV = (NetworkImageView) v.findViewById(R.id.eventUser_large_IV);
        largeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (largeIVContainer.getVisibility() == View.VISIBLE) {
                    Log.d("EventInfoActivity", "Was Visible");
                    largeIVContainer.setVisibility(View.GONE);
                }
            }
        });
        backgroundIV = (NetworkImageView) v.findViewById(R.id.eventUser_background);
        profile_picIV = (RoundedNetworkImageView) v.findViewById(R.id.eventUser_user_profile_pic);
        profile_picIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (largeIVContainer.getVisibility() != View.VISIBLE) {
                    largeIV.setImageUrl(pic_uri, AppController.getInstance().getImageLoader());
                    largeIVContainer.setVisibility(View.VISIBLE);
                }
            }
        });
        facebookIB = (ImageButton) v.findViewById(R.id.eventUser_facebook);
        instagramIB = (ImageButton) v.findViewById(R.id.eventUser_instagram);
        twitterIB = (ImageButton) v.findViewById(R.id.eventUser_twitter);
        snapchatIB = (ImageButton) v.findViewById(R.id.eventUser_snapchat);
        usernameTV = (TextView) v.findViewById(R.id.eventUser_username);
        ListView listView = (ListView) v.findViewById(R.id.eventUser_history);
        listOfEvents = new ArrayListEvents<>();
        adapter = new FeedListAdapter(getActivity(), listOfEvents);
        listView.setAdapter(adapter);
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
                pic_uri = (String) dataSnapshot.child("pic_uri").getValue();
                profile_picIV.setImageUrl(pic_uri, AppController.getInstance().getImageLoader());
                backgroundIV.setImageUrl(pic_uri, AppController.getInstance().getImageLoader());
                usernameTV.setText((String) dataSnapshot.child("name").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void loadUser(final String user_id) {
        DatabaseReference userRef = FireDatabase.getRoot().child("users").child(user_id);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pic_uri = (String) dataSnapshot.child("pic_uri").getValue();
                profile_picIV.setImageUrl(pic_uri, AppController.getInstance().getImageLoader());
                backgroundIV.setImageUrl(pic_uri, AppController.getInstance().getImageLoader());
                usernameTV.setText((String) dataSnapshot.child("name").getValue());
                largeIVContainer.setVisibility(View.GONE);
                loadHistory(user_id, String.valueOf(dataSnapshot.child("facebook_id").getValue(Long.class)));
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

    private static void loadHistory(String userId, String accessToken) {
        FireDatabase.loadFeedEvents(userId, accessToken, listOfEvents, adapter);
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

    public static void hideLargeIV() {
        largeIVContainer.setVisibility(View.GONE);
    }

    public static boolean isLargeIVisible() {
        if (largeIVContainer.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }
}
