package info.nexrave.nexrave.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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

import info.nexrave.nexrave.EventInfoActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.adapters.EventUsersListAdapter;
import info.nexrave.nexrave.models.Guest;
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

    private static FirebaseUser user;
    private static View userList_Container;
    private ListAdapter listAdapter;
    private TextView numLikesTV;
    private int numOfLikes;
    private static boolean isUserListShowing = false;
    private ListView userList;
    private EventUsersListAdapter userListAdapter;

    private OnFragmentInteractionListener mListener;

    public EventChatFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EventChatFragment newInstance() {
        user = FireDatabase.backupFirebaseUser;
        EventChatFragment fragment = new EventChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        DatabaseReference mRootReference = FireDatabase.getInstance().getReference();
        final DatabaseReference usersRef = mRootReference.child("users");
        final DatabaseReference eventRef = mRootReference.child("event_messages")
                .child(((EventInfoActivity) getActivity()).getEvent().event_id);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_event_chat, container, false);
        final EditText typedMessageET = (EditText) view.findViewById(R.id.eventChat_editText);
        final ImageView sendButton = (ImageView) view.findViewById(R.id.eventChat_sendButton);

        typedMessageET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendButton.performClick();
                    handled = true;
                }
                return handled;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!typedMessageET.getText().toString().isEmpty()) {
                    Long time = System.currentTimeMillis();
                    Message message = new Message(
                            user.getUid(), typedMessageET.getText().toString(), time);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(String.valueOf(time), message);
                    eventRef.updateChildren(map);
                    typedMessageET.setText("");
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
        listAdapter = new FirebaseListAdapter<Message>(getActivity(), Message.class,
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

                        final TextView username = (TextView) v.findViewById(R.id.eventChat_user_name);
                        username.setText(dataSnapshot.child("name").getValue(String.class));
                        v.findViewById(R.id.eventChat_user_name).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EventUserFragment.loadUser(model.user_id);
                                ViewPager vp = (ViewPager) getActivity().findViewById(R.id.eventInfoContainer);
                                vp.setCurrentItem(2, true);
                            }
                        });

                        //TODO: FirebaseListAdapter is too unstable with different views to do this
//                        Event currentEvent = ((EventInfoActivity) getActivity()).getEvent();
//                        if (currentEvent.main_host_id.equals(model.user_id)
//                                || currentEvent.hosts.containsKey(model.user_id)) {
//                            username.setTextColor(Color.RED);
//                        }
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


        userList_Container = view.findViewById(R.id.eventChat_user_list_container);
        final SearchView searchView = (SearchView) (userList_Container.findViewById(R.id.eventChat_search_toolbar))
                .findViewById(R.id.searchView);
        userList = (ListView) userList_Container.findViewById(R.id.event_chat_user_list_listview);
        (view.findViewById(R.id.eventChat_users_icon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                if (!isUserListShowing) {
                    userListAdapter = new EventUsersListAdapter((EventInfoActivity) getActivity(), false);
//                    TextView tv = new TextView(getActivity());
//                    tv.setText("500");
//                    userList.addView(tv);
//                    userList.addFooterView(tv);
                    userList.setAdapter(userListAdapter);
                    userListAdapter.notifyDataSetChanged();
                    userList_Container.setVisibility(View.VISIBLE);
                    searchView.setIconified(false);
                    searchView.setQueryHint("Enter guest name here...");
//                    Log.d("EventUsersList", String.valueOf(userListAdapter.getCount()));
//                    Log.d("EventUsersList", String.valueOf(userListAdapter.getItemId(0)));
//                    Log.d("EventUsersList", String.valueOf(userList.getChildCount()));
                    //TODO Remove AP and Kunal
                    long l = 100002510856637L;
                    final Guest AP = new Guest(l, FireDatabase.backupFirebaseUser.getUid());
                    AP.firebase_id = "geDSG6TDxTXWsWr8UjV6j7gI3232";
                    ((EventInfoActivity) getActivity()).getEvent().guests.put("100002510856637", AP);

                    long l2 = 791117281041660L;
                    final Guest KS = new Guest(l2, FireDatabase.backupFirebaseUser.getUid());
                    KS.firebase_id = "ePKPFosz3KeVs39MdVndueQnLVN2";
                    ((EventInfoActivity) getActivity()).getEvent().guests.put("791117281041660", KS);
                    userListAdapter.setNewList(false);
//                    Log.d("EventUsersList", String.valueOf(userListAdapter.getCount()));
////                    for (int i = 0; i < userListAdapter.getCount(); i++) {
////                        listAdapter.getItem(i).
////                    }
//                    Log.d("EventUsersList", String.valueOf(userListAdapter.getItemId(0)));
//                    Log.d("EventUsersList", String.valueOf(userList.getChildCount()));
//                    userListAdapter.setNewList(false);
//
//                    Log.d("EventUsersList", String.valueOf(userListAdapter.getCount()));
////                    for (int i = 0; i < userListAdapter.getCount(); i++) {
////                        listAdapter.getItem(i).
////                    }
//                    Log.d("EventUsersList", String.valueOf(userListAdapter.getItemId(0)));
//                    Log.d("EventUsersList", String.valueOf(userList.getChildCount()));
//                    userList.invalidate();
//                    userList.requestLayout();
                    isUserListShowing = true;

                } else {
                    hideUserList();
                }
            }
        });
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
        final DatabaseReference messageRef = FireDatabase.getRoot().child("event_messages")
                .child(((EventInfoActivity) getActivity()).getEvent().event_id).child(timeStamp);

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

    public static boolean isUserListShowing() {
        return isUserListShowing;
    }

    public static void hideUserList() {
        if (isUserListShowing && (userList_Container != null)) {
            userList_Container.setVisibility(View.GONE);
            isUserListShowing = false;
        } else {
            isUserListShowing = false;
        }
    }

    public static void setUser(@NonNull FirebaseUser firebaseUser) {
        user = firebaseUser;
    }
}
