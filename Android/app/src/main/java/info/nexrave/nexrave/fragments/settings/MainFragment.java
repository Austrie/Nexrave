package info.nexrave.nexrave.fragments.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.SettingsActivity;
import info.nexrave.nexrave.SplashActivity;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    public static AlertDialog dialog;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {

    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
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
        View v = inflater.inflate(R.layout.fragment_settings_main, container, false);
        Button addSocialButton = (Button) v.findViewById(R.id.addSocialButton);
        Button createOrgButton = (Button) v.findViewById(R.id.createOrgButton);
        Button tcButton = (Button) v.findViewById(R.id.tcButton);
        Button logoutButton = (Button) v.findViewById(R.id.logoutButton);
        Button shareButton = (Button) v.findViewById(R.id.shareButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage("Are you sure you want to logout?")
                        .setTitle("Logout");
                // Add the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        FireDatabase.backupFirebaseUser = null;
                        FireDatabase.backupAccessToken = null;
                        FireDatabase.currentEvent = null;
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), SplashActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
                // Create the AlertDialog
                dialog = builder.create();
                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                });
                dialog.show();
            }
        });

        addSocialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SettingsActivity) getActivity())
                        .goToFragment(AddSocialFragment.newInstance(), "add social");
            }
        });

        tcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SettingsActivity) getActivity())
                        .goToFragment(TCFragment.newInstance(), "tc");
            }
        });

        createOrgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage("In order to create an organization, as of right now," +
                        " you must email Nexrave, Inc. and we'll set it up for you. Do you" +
                        " want to email us? (Once you click yes, the email will autopopulated)")
                        .setTitle("Create Organization");
                // Add the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        Intent i = new Intent(android.content.Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"nexravecorp@gmail.com",
                            "austrieshane@gmail.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Nexrave: Create Organization");
                        i.putExtra(Intent.EXTRA_TEXT   , "*Please enter desired organization name" +
                                " and desired organization profile image, along with any relevant" +
                                " relevant information like organization description and/or address.*");
                        startActivity(Intent.createChooser(i, "Email:"));

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
                // Create the AlertDialog
                dialog = builder.create();
                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                });
                dialog.show();
            }
        });
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
        void onFragmentInteraction(Uri uri);
    }
}
