package info.nexrave.nexrave.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.systemtools.AskRunTimePermission;
import info.nexrave.nexrave.systemtools.CameraPreview;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Camera mCamera;
    private CameraPreview mPreview;

    private OnFragmentInteractionListener mListener;
    private FrameLayout frameLayout;

    private ImageView redDotIV;
    private ProgressBar mProgressBar;
    private static final int MIN_CLICK_DURATION = 600;
    private long startClickTime;
    private boolean longClickActive;
    private boolean recording, pause = false;
    private long elapsed;
    private long remainingSecs = 0;
    private long elapsedSecs = 0;
    private Timer timer;
    private TimerTask task;


    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CameraFragment.
     */
    //TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
//        Bundle args = new Bundle();;
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        Log.d("CameraActivity", "onCalled called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CameraActivity", "onCreateView called");
        AskRunTimePermission.ask(getActivity(), Manifest.permission.CAMERA, 1);
        // Inflate the layout for this fragment
//        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getActivity(), mCamera);
        frameLayout = (FrameLayout) view.findViewById(R.id.eventInfo_camera_frame);
        frameLayout.addView(mPreview);
        RelativeLayout controls = (RelativeLayout) frameLayout.findViewById(R.id.camera_controls);
        controls.bringToFront();
        ((ImageView) controls.findViewById(R.id.switchCameraIcon)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switchCamera();
                    }
                });

        ((ImageView) controls.findViewById(R.id.snapCameraButton)).setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View v, final MotionEvent event) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                snapButton(v, event);
                            }

                        }).run();
                        return true;
                    }
                });

        mProgressBar = (ProgressBar) view.findViewById(R.id.circular_progress_bar);
        redDotIV = (ImageView) view.findViewById(R.id.eventCamera_red_dot);

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
        Log.d("CameraActivity", "onAttached called");
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
        Log.d("CameraActivity", "onDetach called");

    }


    @Override
    public void onResume() {
        super.onResume();
        AskRunTimePermission.ask(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
        AskRunTimePermission.ask(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, 1);
        AskRunTimePermission.ask(getActivity(), Manifest.permission.RECORD_AUDIO, 1);
        Log.d("CameraActivity", "onResume called");
        try {
            if ((mCamera == null) || (mPreview == null)) {
                try {
                    mCamera.reconnect();
                    mCamera.setDisplayOrientation(90);
                    mPreview.previewCamera();
                } catch (Exception e) {
                    Log.d("CameraActivity", e.toString());
                }
            }
            mPreview.previewCamera();
        } catch (Exception e) {
            Log.d("CameraFragment", e.toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            Log.d("CameraActivity", "onPause called");
            mListener = null;
            mPreview.stopPreview();
            mPreview.releaseMediaRecorder();
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            if (task != null)
                task.cancel();
            mProgressBar.setProgress(100);
            remainingSecs = 30;
            elapsedSecs = 0;
            remainingSecs = 0;
        } catch (Exception e) {
            Log.d("CameraFragment", e.toString());
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("CameraActivity", "onStop called");
//        releaseCamera();
        mPreview.stopPreview();
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

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.d("CameraActivity", " Is in use or doesn't exist");
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            Activity a = getActivity();
            if (a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void switchCamera() {
        if (mPreview.isPreviewRunning()) {
            mPreview.stopPreview();
        }
        mCamera.release();

        if (CameraPreview.currentCamera == Camera.CameraInfo.CAMERA_FACING_BACK) {
            CameraPreview.currentCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            CameraPreview.currentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        try {
            mCamera = Camera.open(CameraPreview.currentCamera);
            mCamera.setDisplayOrientation(90);
            mPreview.setCamera(mCamera);
            mPreview.previewCamera();
        } catch (Exception e) {
            Log.d("CameraActivty", e.toString());
        }
    }

    public boolean snapButton(View v, MotionEvent event) {

        try {
            final long INTERVAL = 1000;
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    Log.i("ACTION_DOWN", "ACTION_DOWN::" + pause + " " + recording);
                    startClickTime = System.currentTimeMillis();
                    recording = false;
                    remainingSecs = 30;
                    elapsedSecs = 0;
                    remainingSecs = 0;
                    break;

                case MotionEvent.ACTION_MOVE:
                    long clickTime = System.currentTimeMillis() - startClickTime;
                    if (!recording && clickTime >= MIN_CLICK_DURATION) {
                        recording = true;

                        if (!mPreview.prepareMediaRecorder()) {

                            Log.e("CameraFragment", "Fail in prepare MediaRecorder");

                        }
                        // work on UiThread for better performance
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    mPreview.getRecorder().start();

                                } catch (final Exception ex) {
                                    Log.d("CameraFragment", ex.toString());
                                }
                            }
                        });
                        recording = true;
                        task = new TimerTask() {
                            @Override
                            public void run() {
                                elapsed += INTERVAL;
                                redBlink(true);
                                if (elapsed == 31000) {
                                    try {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                recording = false;
                                                // TODO Auto-generated method stub
                                                // mediaRecorder.stop(); // stop the recording
                                                mPreview.releaseMediaRecorder(); // release the MediaRecorder object
                                                mProgressBar.setProgress(100);
                                                if (timer != null) {
                                                    timer.cancel();
                                                    timer.purge();
                                                }
                                                if (task != null)
                                                    task.cancel();

                                                Log.e("Video captured!", "");
                                            }
                                        });


                                    } catch (Exception e) {
                                        Log.d("CameraFragment", e.toString());
                                    }
                                    return;
                                }
                                Log.i("CameraFragment", "Debug" + elapsed);
                                elapsedSecs =elapsed / 1000;
                                Log.i("CameraFragment", "Debug" + elapsedSecs);
                                remainingSecs = 30 - (int) elapsedSecs;
                                Log.i("CameraFragment", "Debug" + remainingSecs);
                                mProgressBar.setProgress((int) (((remainingSecs / 30)) * 100));
                                Log.i("CameraFragment", "Debug" + mProgressBar.getProgress());
                                Log.i("CameraFragment", "Seconds::" + (elapsedSecs));
                            }
                        };
                        timer = new Timer();
                        timer.scheduleAtFixedRate(task, INTERVAL, INTERVAL);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("ACTION_UP", "ACTION_UP::" + recording);
                    if (recording) {
                        // stop recording and release camera
                        mProgressBar.setProgress(100);
                        if (timer != null) {
                            timer.cancel();
                            timer.purge();
                        }
                        if (task != null)
                            task.cancel();
                        recording = false;
                        remainingSecs = 30;
                        elapsedSecs = 0;
                        remainingSecs = 0;
                        mPreview.releaseMediaRecorder(); // release the MediaRecorder object
                        redBlink(false);
                    } else {

//                        mPreview.takePicture();
                    }

                    break;
            }

            return true;
        } catch (Exception e) {
            // TODO: handle exception
            Log.d("CameraFragment", e.toString());
        }
        return false;
    }

    public void redBlink(final boolean blink) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (blink) {
                    if (redDotIV.getVisibility() == View.VISIBLE) {
                        redDotIV.setVisibility(View.INVISIBLE);
                    } else {
                        redDotIV.setVisibility(View.VISIBLE);
                    }
                } else {
                    redDotIV.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}





