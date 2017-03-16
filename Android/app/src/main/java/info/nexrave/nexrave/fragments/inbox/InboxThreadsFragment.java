package info.nexrave.nexrave.fragments.inbox;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.view.DraweeView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.adapters.InboxThreadsAdapter;
import info.nexrave.nexrave.models.InboxThread;
import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.models.Message;
import info.nexrave.nexrave.systemtools.FireDatabase;

public class InboxThreadsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    //TODO stop using activity static variables
//    private static InboxActivity activity;

    public InboxThreadsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static InboxThreadsFragment newInstance() {
//        activity = context;
        InboxThreadsFragment fragment = new InboxThreadsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Inbox", "Started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inbox_threads, container, false);
        Log.d("Inbox", "Started 2");
        setupInbox(v);
        Log.d("Inbox", "returned");
        return v;
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

    private void setupInbox(final View v) {
        Log.d("Inbox", "Setup 1");
        final ListView listView = (ListView) v.findViewById(R.id.inboxThreads_listView);
        final List<InboxThread> listOfMessages = new LinkedList<>();
        final InboxThreadsAdapter adapter = new InboxThreadsAdapter(getActivity(), listOfMessages);
        listView.setAdapter(adapter);

        final DatabaseReference mRootReference = FireDatabase.getRoot();
        final DatabaseReference userRef = mRootReference.child("users").child(FireDatabase.backupFirebaseUser.getUid())
                .child("direct_messages");
        userRef.child("threads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Map<String, String>> maps = (Map<String, Map<String, String>>) dataSnapshot.getValue();
                    final ArrayList<String> mapKeys = new ArrayList<String>(maps.keySet());
                    for (int i = 0; i < mapKeys.size(); i++) {
                        Map<String, String> map = (Map<String, String>) dataSnapshot.child(mapKeys.get(i)).getValue();
                        if (!map.get("setting").equals("hide")) {
                            final String key = mapKeys.get(i);
                            FireDatabase.getRoot().child("direct_messages").child(key)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            InboxThread thread = new InboxThread();
                                            try {
                                                thread = dataSnapshot.getValue(InboxThread.class);
                                            } catch(Exception e) {
                                                Log.d("Inbox", e.toString());
                                                Map<String, Map<String, Object>> primitiveMessages =
                                                        (Map<String, Map<String, Object>>) dataSnapshot.child("messages").getValue();
                                                int length = primitiveMessages.keySet().size();
                                                String[] key = (String[]) primitiveMessages.keySet().toArray();
                                                for (int i = 0; i < length; i++) {
                                                    thread.messages.put(key[i], Message.convertMapToMessage(primitiveMessages.get(i)));
                                                }
                                                thread.members = (Map<String, String>) dataSnapshot.child("members").getValue();
                                            }
                                            listOfMessages.add(thread);
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                        //Sets the first chat that appears if you swipe left
                        try {
                            InboxMessagesFragment.setInboxThread((listOfMessages.get(listOfMessages.size() - 1)));
                        } catch(Exception e) {
                            Log.d("Inbox", e.toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

//    private void setupInbox(View v) {
//        DatabaseReference mRootReference = FireDatabase.getInstance().getReference();
//        final DatabaseReference userRef = mRootReference.child("users").child(Firedatebase.backup.child("direct_messages");
//        final DatabaseReference messageRef = mRootReference.child("direct_messages");
//
//        userRef.child("users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
//                    ArrayList<Object> threadsList = new ArrayList<Object>(map.values());
//                    for (int i = 0; i < map.size(); i++) {
//                        userRef.child("threads").child(map.get(i)).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                final String permission = (String) dataSnapshot.getValue();
//                                if (permission.equals("allow")) {
//
//                                } else if (permission.equals("mute")) {
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        ListView listView = (ListView) v.findViewById(R.id.inboxThreads_listView);
//        FirebaseListAdapter<Message> listAdapter = new FirebaseListAdapter<Message>(activity, Message.class,
//                R.layout.message, eventRef) {
//            @Override
//            protected void populateView(final View v, final Message model, final int position) {
//                DatabaseReference userRef = usersRef.child(model.user_id);
//                userRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        ((NetworkImageView) v.findViewById(R.id.eventChat_user_profile_pic))
//                                .setImageUrl(dataSnapshot.child("pic_uri").getValue(String.class), AppController.getInstance().getImageLoader());
//                        (v.findViewById(R.id.eventChat_user_profile_pic)).setOnClickListener(
//                                new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        EventUserFragment.loadUser(model.user_id);
//                                        ViewPager vp = (ViewPager) getActivity().findViewById(R.id.eventInfoContainer);
//                                        vp.setCurrentItem(2, true);
//                                    }
//                                }
//                        );
//
//                        ((TextView) v.findViewById(R.id.eventChat_user_name)).setText(dataSnapshot.child("name").getValue(String.class));
//                        v.findViewById(R.id.eventChat_user_name).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                EventUserFragment.loadUser(model.user_id);
//                                ViewPager vp = (ViewPager) getActivity().findViewById(R.id.eventInfoContainer);
//                                vp.setCurrentItem(2, true);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//                ((TextView) v.findViewById(R.id.eventChat_user_message)).setText(model.message);
//
//                numLikesTV = (TextView) v.findViewById(R.id.eventChat_numberOfLikes);
//                if ((model.numberOfLikes != 0)) {
//                    numLikesTV.setText(String.valueOf(model.numberOfLikes));
//                } else {
//                    numLikesTV.setText("");
//                }
//
//                final ImageView heartIV = (ImageView) v.findViewById(R.id.eventChat_message_heart_icon);
//
//                if (model.whoLiked.containsKey(FireDatabase.backupFirebaseUser.getUid())) {
//                    heartIV.setImageResource(R.drawable.heart_icon);
//                }
//
//                ((ImageView) v.findViewById(R.id.eventChat_message_heart_icon)).setOnClickListener(
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if (model.whoLiked.containsKey(FireDatabase.backupFirebaseUser.getUid())) {
//                                    heartIV.setImageResource(R.drawable.empty_heart_icon);
//                                    userLiked(false, String.valueOf(model.time_stamp));
//                                } else {
//                                    heartIV.setImageResource(R.drawable.heart_icon);
//                                    userLiked(true, String.valueOf(model.time_stamp));
//                                }
//                            }
//                        }
//                );
//            }
//        };
//        listView.setAdapter(listAdapter);
//    }
}
