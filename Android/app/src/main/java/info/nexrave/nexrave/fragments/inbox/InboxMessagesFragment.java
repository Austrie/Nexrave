package info.nexrave.nexrave.fragments.inbox;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.adapters.InboxMessagesAdapter;
import info.nexrave.nexrave.models.InboxThread;
import info.nexrave.nexrave.models.Message;
import info.nexrave.nexrave.systemtools.FireDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InboxMessagesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InboxMessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InboxMessagesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static InboxThread thread;
    private static ListView listView;
    private static InboxMessagesAdapter adapter;
    private static Activity activity;
    private static ImageView sendButton;
    private static EditText typedMessage;

    private OnFragmentInteractionListener mListener;

    public InboxMessagesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static InboxMessagesFragment newInstance(InboxThread choosenThread, Activity a) {
        InboxMessagesFragment fragment = new InboxMessagesFragment();
        thread = choosenThread;
        activity = a;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_inbox_messages, container, false);
        typedMessage = (EditText) v.findViewById(R.id.InboxMessages_editText);
        sendButton = (ImageView) v.findViewById(R.id.InboxMessages_sendButton);
        ImageView shutterButton = (ImageView) v.findViewById(R.id.InboxMessages_shutter_icon);
        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Shutter button in dms
            }
        });
        adapter = new InboxMessagesAdapter(getActivity(),
                new ArrayList<>(thread.messages.values()), thread.thread_id);
        listView = (ListView) v.findViewById(R.id.InboxMessages_listView);
        listView.setAdapter(adapter);
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

    public static void setInboxThread(InboxThread iThread) {
        thread = iThread;
        final DatabaseReference messagesRef = FireDatabase.getRoot().child("direct_messages").child(thread.thread_id);
        adapter = new InboxMessagesAdapter(activity, new ArrayList<>(thread.messages.values()), thread.thread_id);
        listView.setAdapter(adapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!typedMessage.getText().toString().isEmpty()) {
                    Long time = System.currentTimeMillis();
                    Message message = new Message(
                            FireDatabase.backupFirebaseUser.getUid(), typedMessage.getText().toString(), time);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(String.valueOf(time), message);
                    messagesRef.child("messages").updateChildren(map);
                    typedMessage.setText("");
                }
            }
        });
    }
}
