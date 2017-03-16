package info.nexrave.nexrave.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.Message;
import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.systemtools.FireDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static Activity activity;
    private static String event_id;
    private static FirebaseUser user;
    private ListAdapter listAdapter;
    private TextView numLikesTV;
    private int numOfLikes;
    private boolean choice;

    private OnFragmentInteractionListener mListener;

    public EventChatFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EventChatFragment newInstance(FirebaseUser mUser, Activity context, String id) {
        user = mUser;
        activity = context;
        event_id = id;
        EventChatFragment fragment = new EventChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DatabaseReference mRootReference = FireDatabase.getInstance().getReference();
        final DatabaseReference usersRef = mRootReference.child("users");
        final DatabaseReference eventRef = mRootReference.child("event_messages").child(event_id);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_event_chat, container, false);
        final EditText typedMessage = (EditText) view.findViewById(R.id.eventChat_editText);
        ImageView sendButton = (ImageView) view.findViewById(R.id.eventChat_sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!typedMessage.getText().toString().isEmpty()) {
                    Long time = System.currentTimeMillis();
                    Message message = new Message(
                            user.getUid(), typedMessage.getText().toString(), time);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(String.valueOf(time), message);
                    eventRef.updateChildren(map);
                    typedMessage.setText("");
                }
            }
        });


        ImageView shutterButton = (ImageView) view.findViewById(R.id.eventChat_shutter_icon);
        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerticalViewPagerFragment.toCamera();
            }
        });

        ListView listView = (ListView) view.findViewById(R.id.eventChat_listView);
        listAdapter = new FirebaseListAdapter<Message>(activity, Message.class,
                R.layout.message, eventRef) {
            @Override
            protected void populateView(final View v, final Message model, final int position) {
                DatabaseReference userRef = usersRef.child(model.user_id);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ((NetworkImageView) v.findViewById(R.id.eventChat_user_profile_pic))
                                .setImageUrl(dataSnapshot.child("pic_uri").getValue(String.class), AppController.getInstance().getImageLoader());
                        (v.findViewById(R.id.eventChat_user_profile_pic)).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        EventUserFragment.loadUser(model.user_id);
                                        ViewPager vp = (ViewPager) getActivity().findViewById(R.id.eventInfoContainer);
                                        vp.setCurrentItem(2, true);
                                    }
                                }
                        );

                        ((TextView) v.findViewById(R.id.eventChat_user_name)).setText(dataSnapshot.child("name").getValue(String.class));
                        v.findViewById(R.id.eventChat_user_name).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EventUserFragment.loadUser(model.user_id);
                                ViewPager vp = (ViewPager) getActivity().findViewById(R.id.eventInfoContainer);
                                vp.setCurrentItem(2, true);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                ((TextView) v.findViewById(R.id.eventChat_user_message)).setText(model.message);

                numLikesTV = (TextView) v.findViewById(R.id.eventChat_numberOfLikes);
                if ((model.numberOfLikes != 0)) {
                    numLikesTV.setText(String.valueOf(model.numberOfLikes));
                } else {
                    numLikesTV.setText("");
                }

                final ImageView heartIV = (ImageView) v.findViewById(R.id.eventChat_message_heart_icon);

                if (model.whoLiked.containsKey(FireDatabase.backupFirebaseUser.getUid())) {
                    heartIV.setImageResource(R.drawable.heart_icon);
                } else {
                    heartIV.setImageResource(R.drawable.empty_heart_icon);
                }

                ((ImageView) v.findViewById(R.id.eventChat_message_heart_icon)).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (model.whoLiked.containsKey(FireDatabase.backupFirebaseUser.getUid())) {
                                    heartIV.setImageResource(R.drawable.empty_heart_icon);
                                    userLiked(false, String.valueOf(model.time_stamp));
                                } else {
                                    heartIV.setImageResource(R.drawable.heart_icon);
                                    userLiked(true, String.valueOf(model.time_stamp));
                                }
                            }
                        }
                );
            }
        };
        listView.setAdapter(listAdapter);
        return view;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            EventUserFragment.setUserToBeViewed(FireDatabase.backupFirebaseUser.getUid());
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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

    public void userLiked(final boolean choice, String timeStamp) {
        final DatabaseReference messageRef = FireDatabase.getRoot().child("event_messages").child(event_id)
                .child(timeStamp);

        if (choice) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put(FireDatabase.backupFirebaseUser.getUid(), System.currentTimeMillis());
            messageRef.child("whoLiked").updateChildren(map);
            messageRef.child("numberOfLikes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int numOfLikes = dataSnapshot.getValue(Integer.class);
                        messageRef.child("numberOfLikes").setValue(++numOfLikes);
                        numLikesTV.setText(String.valueOf(++numOfLikes));
                    } else {
                        messageRef.child("numberOfLikes").setValue(1);
                        numLikesTV.setText(String.valueOf(1));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            messageRef.child("whoLiked").child(FireDatabase.backupFirebaseUser.getUid()).removeValue();
            messageRef.child("numberOfLikes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        numOfLikes = dataSnapshot.getValue(Integer.class);
                        numOfLikes = (numOfLikes - 1);
                        messageRef.child("numberOfLikes").setValue(numOfLikes);
                        numLikesTV.setText(String.valueOf(numOfLikes));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    public static void setUser(@NonNull FirebaseUser firebaseUser) {
        user = firebaseUser;
    }
}
