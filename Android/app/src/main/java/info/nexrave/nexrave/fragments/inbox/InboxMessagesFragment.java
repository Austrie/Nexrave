package info.nexrave.nexrave.fragments.inbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.adapters.InboxMessagesAdapter;
import info.nexrave.nexrave.models.InboxThread;
import info.nexrave.nexrave.models.Message;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;
import info.nexrave.nexrave.systemtools.LockableViewPager;

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
    private static  DatabaseReference messagesRef;
    private static ValueEventListener listener;
    private static ArrayList<Message> messagesAL;
    private static ImageView attachedImage;
    private static View attachedImageContainer;
    private static File tempFile;

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
        final View v = inflater.inflate(R.layout.fragment_inbox_messages, container, false);
        typedMessage = (EditText) v.findViewById(R.id.InboxMessages_editText);
        sendButton = (ImageView) v.findViewById(R.id.InboxMessages_sendButton);
        ImageView shutterButton = (ImageView) v.findViewById(R.id.InboxMessages_shutter_icon);
        EditText typedMessageET = (EditText) v.findViewById(R.id.InboxMessages_editText);
        attachedImage = (ImageView) v.findViewById(R.id.InboxMessages_editText_image);
        attachedImageContainer = v.findViewById(R.id.InboxMessages_attachedImageContainer);

        attachedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ImageView) v.findViewById(R.id.InboxMessages_enlarge_IV)).setImageDrawable(
                        attachedImage.getDrawable()
                );
                v.findViewById(R.id.InboxMessages_enlarge_IV).setVisibility(View.VISIBLE);
            }
        });

        v.findViewById(R.id.InboxMessages_enlarge_IV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ImageView) v.findViewById(R.id.InboxMessages_enlarge_IV)).setImageDrawable(null);
                v.findViewById(R.id.InboxMessages_enlarge_IV).setVisibility(View.GONE);
            }
        });
        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Shutter button in dms
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                InboxMessagesFragment.this.startActivityForResult(intent, 1);
            }
        });

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
        adapter = new InboxMessagesAdapter(getActivity(),
                new ArrayList<>(thread.messages.values()), thread.thread_id);
        listView = (ListView) v.findViewById(R.id.InboxMessages_listView);
        listView.setAdapter(adapter);
        if (thread.thread_id == null) {
            LockableViewPager vp = (LockableViewPager) getActivity().findViewById(R.id.inbox_Container);
            vp.setCurrentItem(0);
            vp.setPagingEnabled(false);
        }
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
        if ((messagesRef != null) && (listener != null)) {
            messagesRef.removeEventListener(listener);
        }
        thread = iThread;
        messagesAL = new ArrayList<>(thread.messages.values());
        adapter = new InboxMessagesAdapter(activity, messagesAL, thread.thread_id);
        listView.setAdapter(adapter);
        messagesRef = FireDatabase.getRoot().child("direct_messages").child(thread.thread_id);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FireDatabase.checkForPreviousMessage(activity, thread.thread_id, typedMessage.getText().toString().isEmpty()
                        , (attachedImageContainer.getVisibility() == View.GONE), System.currentTimeMillis()
                        , typedMessage.getText().toString(), tempFile, messagesRef);
                typedMessage.setText("");
            }
        });

        if (thread.thread_id != null) {
            listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        InboxThread t =  dataSnapshot.getValue(InboxThread.class);
                        thread = t;
                        messagesAL = new ArrayList<>(t.messages.values());
                        adapter.setNewList(messagesAL);
                        listView.setSelection(adapter.getCount() - 1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            messagesRef.addValueEventListener(listener);
        }
    }

    public static void removePic() {
        attachedImage.setImageDrawable(null);
        attachedImageContainer.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((messagesRef != null) && (listener != null)) {
            messagesRef.addValueEventListener(listener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if ((messagesRef != null) && (listener != null)) {
            messagesRef.removeEventListener(listener);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Copy Uri contents into temp File.
        if ((data != null) && (requestCode == 1)) {
            tempFile = new File(getActivity().getFilesDir().getAbsolutePath(), "temp_image");
            try {
                tempFile.createNewFile();
                IOUtils.copy(getActivity().getContentResolver().openInputStream(data.getData())
                        , new FileOutputStream(tempFile));
                Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getPath());

                Drawable bitmapDrawable = new BitmapDrawable(bitmap);
                attachedImage.setImageDrawable(bitmapDrawable);
                attachedImageContainer.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                //Log Error
                Log.d("InboxMessageFragment", e.toString());
            }
        }
    }

}
