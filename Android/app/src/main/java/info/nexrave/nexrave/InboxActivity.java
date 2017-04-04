package info.nexrave.nexrave;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import info.nexrave.nexrave.fragments.inbox.InboxMessagesFragment;
import info.nexrave.nexrave.fragments.inbox.InboxThreadsFragment;
import info.nexrave.nexrave.models.InboxThread;
import info.nexrave.nexrave.systemtools.FireDatabase;
import info.nexrave.nexrave.systemtools.LockableViewPager;

public class InboxActivity extends AppCompatActivity
        implements InboxThreadsFragment.OnFragmentInteractionListener,
                    InboxMessagesFragment.OnFragmentInteractionListener {

    private static final String TAG = InboxActivity.class.getSimpleName();
    private static LockableViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getExtra(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (LockableViewPager) findViewById(R.id.inbox_Container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPagingEnabled(false);
    }

//    private void getExtra(Bundle savedInstanceState) {
//        if (savedInstanceState == null) {
//            String extra = (Event) getIntent().getSerializableExtra("SELECTED_EVENT");
//            if (extra == null) {
//                selectedEvent = new Event();
//            } else {
//                selectedEvent = extra;
//            }
//        } else {
//            selectedEvent = (Event) savedInstanceState.getSerializable("SELECTED_EVENT");
//        }
//    }

    public void removePic(View v) {
        InboxMessagesFragment.removePic();
    }

    public void toChat(View v) {
        mViewPager.setCurrentItem(1, true);
    }

    public void backButton(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        int position = mViewPager.getCurrentItem();
        if (position == 0) {
            super.onBackPressed();
        } else if (position == 1) {
            mViewPager.setCurrentItem(0, true);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a HostMainFragment (defined as a static inner class below).
            switch (position) {
                case (0):
                    return InboxThreadsFragment.newInstance();
                case (1):
                    return InboxMessagesFragment.newInstance(new InboxThread(), InboxActivity.this);
            }
            return InboxThreadsFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Inbox";
                case 1:
                    return "Chat";
            }
            return "Event Info";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
//            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                ErrorDialog.newInstance(getString(R.string.request_permission))
//                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
//            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static void setInboxThread(InboxThread thread) {
        InboxMessagesFragment.setInboxThread(thread);
        mViewPager.setPagingEnabled(true);
        mViewPager.setCurrentItem(1, true);
    }

}
