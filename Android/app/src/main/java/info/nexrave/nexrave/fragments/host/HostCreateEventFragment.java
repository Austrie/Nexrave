package info.nexrave.nexrave.fragments.host;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.systemtools.FireDatabase;

/**
 * Created by yoyor on 2/19/2017.
 */

public class HostCreateEventFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    Button nextButton;
    Button coverPicButton;
    EditText eventLocationEt;
    EditText eventNameEt;
    EditText eventTimeEt;
    EditText eventDateEt;
    EditText eventDescriptionEt;
    private static File picFile;

//    private static final String ARG_SECTION_NUMBER = "section_number";
    private final int PICK_IMAGE_REQUEST = 1;
    private File tempFile;

    public HostCreateEventFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HostCreateEventFragment newInstance() {
        HostCreateEventFragment thisFragment = new HostCreateEventFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            thisFragment.setArguments(args);
        return thisFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_host_add, container, false);
        eventNameEt = (EditText) rootView.findViewById(R.id.et_event_name);
        eventTimeEt = (EditText) rootView.findViewById(R.id.et_event_time);
        eventDateEt = (EditText) rootView.findViewById(R.id.et_event_date);
        eventLocationEt = (EditText) rootView.findViewById(R.id.et_event_location);
        eventDescriptionEt = (EditText) rootView.findViewById(R.id.et_event_description);
        coverPicButton = (Button) rootView.findViewById(R.id.bt_select_cover_pic);
        coverPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                HostCreateEventFragment.this.startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        nextButton = (Button) rootView.findViewById(R.id.bt_search_guests);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FireDatabase.createEvent(user, eventNameEt.getText().toString()
                        , eventDescriptionEt.getText().toString(), eventTimeEt.getText().toString()
                        , eventDateEt.getText().toString(), eventLocationEt.getText().toString()
                        , picFile);
            }
        });
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    public void searchforGuestsScreen(View v) {

    }

    public void addHosts(View v) {

    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        tempFile = new File(getActivity().getFilesDir().getAbsolutePath(), "temp_image");

        //Copy Uri contents into temp File.
        if (data != null) {
            try {
                tempFile.createNewFile();
                IOUtils.copy(getActivity().getContentResolver().openInputStream(data.getData())
                        , new FileOutputStream(tempFile));
                Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getPath());
                Drawable bitmapDrawable = new BitmapDrawable(bitmap);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    coverPicButton.setBackgroundDrawable(bitmapDrawable);
                } else {
                    coverPicButton.setBackground(bitmapDrawable);
                }
                picFile = tempFile;

            } catch (IOException e) {
                //Log Error
            }

//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                    // Log.d(TAG, String.valueOf(bitmap));
//
//                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
//                    imageView.setImageBitmap(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
        }
    }

}
