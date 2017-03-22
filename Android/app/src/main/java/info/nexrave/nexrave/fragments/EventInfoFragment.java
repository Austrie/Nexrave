package info.nexrave.nexrave.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import info.nexrave.nexrave.EventInfoActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.systemtools.FireDatabase;
import info.nexrave.nexrave.systemtools.QRCode;
import info.nexrave.nexrave.systemtools.RoundedNetworkImageView;
import info.nexrave.nexrave.systemtools.TimeConversion;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static Event event;
    private static ImageView QR;
    private static Bitmap QRCodeBitmap;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EventInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * //     * @param param1 Parameter 1.
     * //     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment EventInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventInfoFragment newInstance() {
        EventInfoFragment fragment = new EventInfoFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = ((EventInfoActivity)getActivity()).getEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_event_info, container, false);

        Uri uri = Uri.EMPTY;
        try {
            uri = Uri.parse(event.image_uri);
        } catch (Exception e) {
            Log.d("EventInfoActivity", e.toString());
            event = ((EventInfoActivity) getActivity()).getEvent();
            if (event ==  null) {
                getActivity().finish();
            }
        }
        SimpleDraweeView event_flier = (SimpleDraweeView) view
                .findViewById(R.id.eventInfo_flier);
        event_flier.setImageURI(uri);

        DatabaseReference orgRef = FireDatabase.getRoot().child("organizations")
                .child(event.organization);

        orgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Org name
                    RoundedNetworkImageView host_pic = (RoundedNetworkImageView) view
                            .findViewById(R.id.eventInfo_host_profile_pic);
                    host_pic.setImageUrl((String) dataSnapshot.child("pic_uri").getValue()
                            , AppController.getInstance().getImageLoader());

                } else {

                    final DatabaseReference mainHostRef = FireDatabase.getRoot().child("users")
                            .child(event.main_host_id);
                    mainHostRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //Main Host profile pic
                            RoundedNetworkImageView host_pic = (RoundedNetworkImageView) view
                                    .findViewById(R.id.eventInfo_host_profile_pic);
                            host_pic.setImageUrl((String) dataSnapshot.child("pic_uri").getValue()
                                    , AppController.getInstance().getImageLoader());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        TextView date_time = (TextView) view
                .findViewById(R.id.eventInfo_date_time);
        date_time.setText(TimeConversion.eventTime(event.date_time));

        TextView event_name = (TextView) view
                .findViewById(R.id.eventInfo_event_name);
        event_name.setText(event.event_name);

        TextView event_description = (TextView) view
                .findViewById(R.id.eventInfo_event_description);
        event_description.setText(event.description);

        final TextView event_location = (TextView) view
                .findViewById(R.id.eventInfo_event_location);
        event_location.setText(event.location);
        event_location.setText(Html.fromHtml("<a href=\"http://maps.google.com/maps?q=" +
                event.location.replace(" ", "+") + "\">" +
                event.location + "</a>"));

        QR = (ImageView) view.findViewById(R.id.eventInfo_qr_code_IV);
        event_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="
                        + event.location));
                startActivity(geoIntent);
            }
        });

        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QR.getVisibility() == View.VISIBLE) {
                    Log.d("EventInfoActivity", "Was Visible");
                    QR.setVisibility(View.GONE);
                }
            }
        });

        Button ticketButton = (Button) view.findViewById(R.id.eventInfo_ticket_button);
        Log.d("EventInfoActivity", "Button event set");
        ticketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QR.getVisibility() != View.VISIBLE) {
                    try {
                        if (QRCodeBitmap == null) {
                            String s = FireDatabase.backupAccessToken.getUserId() + "." + FireDatabase.backupFirebaseUser.getUid();
                            QRCodeBitmap = QRCode.encodeAsBitmap(s);
                        }
                        QR.setImageBitmap(QRCodeBitmap);
                        QR.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Log.d("EventInfoActivity", e.toString());
                    }
                } else {
                    QR.setVisibility(View.GONE);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(Color.parseColor("#00000000"));
            getActivity().getWindow().setNavigationBarColor(Color.parseColor("#00000000"));
        }
//        getActivity().findViewById(R.id.toolbar).getBackground().setAlpha(0);
//        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        getActivity().findViewById(R.id.eventInfo_contentLayout).setLayoutParams(p);

        return view;
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

    public static void hideQR() {
        QR.setVisibility(View.GONE);
    }

    public static boolean isQRVisible() {
        if (QR.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }
}
