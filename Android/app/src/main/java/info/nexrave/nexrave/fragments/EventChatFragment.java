package info.nexrave.nexrave.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.EventChatMessage;
import info.nexrave.nexrave.models.User;
import info.nexrave.nexrave.newsfeedparts.AppController;

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

    private OnFragmentInteractionListener mListener;

    public EventChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * //     * @param param1 Parameter 1.
     * //     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment EventChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventChatFragment newInstance(FirebaseUser mUser, Activity context, String id) {
        user = mUser;
        activity = context;
        event_id = id;
        EventChatFragment fragment = new EventChatFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("EventChat", event_id);
        Log.d("EventChat", String.valueOf(user));
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference usersRef = mRootReference.child("users");
        final DatabaseReference eventRef = mRootReference.child("event_messages").child(event_id);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_event_chat, container, false);
        final EditText typedMessage = (EditText) view.findViewById(R.id.eventChat_editText);
        ImageButton sendButton = (ImageButton) view.findViewById(R.id.eventChat_sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!typedMessage.getText().toString().isEmpty()) {
                    Long time = System.currentTimeMillis();
                    EventChatMessage message = new EventChatMessage(
                            user.getUid(), typedMessage.getText().toString(), time);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(String.valueOf(time), message);
                    eventRef.updateChildren(map);
                    typedMessage.setText("");
                }
            }
        });

        ListView listView = (ListView) view.findViewById(R.id.eventChat_listView);
        ListAdapter listAdapter = new FirebaseListAdapter<EventChatMessage>(activity, EventChatMessage.class,
                R.layout.message, eventRef) {
            @Override
            protected void populateView(final View v, final EventChatMessage model, final int position) {
                DatabaseReference userRef = usersRef.child(model.user_id);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User poster = dataSnapshot.getValue(User.class);
                        ((NetworkImageView) v.findViewById(R.id.eventChat_user_profile_pic))
                                .setImageUrl(poster.pic_uri, AppController.getInstance().getImageLoader());

                        ((TextView) v.findViewById(R.id.eventChat_user_name)).setText(poster.name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                ((TextView) v.findViewById(R.id.eventChat_user_message)).setText(model.message);
            }
        };
        listView.setAdapter(listAdapter);

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

    public static void setUser(@NonNull FirebaseUser firebaseUser) {
        user = firebaseUser;
    }
}
