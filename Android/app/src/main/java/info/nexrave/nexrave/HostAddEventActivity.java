package info.nexrave.nexrave;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.BitmapTypeRequest;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import info.nexrave.nexrave.systemtools.FireDatabase;

public class HostAddEventActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static File picFile;
    private ViewPager mViewPager;
    private int fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_host);

        fragment = 1;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = 0;
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(mViewPager);
                fab.setVisibility(View.GONE);
            }
        });

    }

    public void backToFeed(View v) {
        Intent i = new Intent(HostAddEventActivity.this, FeedActivity.class);
        startActivity(i);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * The first screen for host activity
     */
    public static class HostAddFragment extends Fragment {
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

        private static final String ARG_SECTION_NUMBER = "section_number";
        private final int PICK_IMAGE_REQUEST = 1;
        private File tempFile;

        public HostAddFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static HostAddFragment newInstance(int sectionNumber) {
            HostAddFragment thisFragment = new HostAddFragment();
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
            //TODO start using DatePicker and TimePicker
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
                    HostAddFragment.this.startActivityForResult(intent, PICK_IMAGE_REQUEST);
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a HostMainFragment (defined as a static inner class below).
                    return HostAddFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return "Private Event";
        }
    }
}
