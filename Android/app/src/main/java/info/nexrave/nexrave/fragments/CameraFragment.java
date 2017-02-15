package info.nexrave.nexrave.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;

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
    private static Activity activity;

    private OnFragmentInteractionListener mListener;
    private FrameLayout frameLayout;

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
    public static CameraFragment newInstance(Activity a, String eventId) {
        CameraFragment fragment = new CameraFragment();
        activity = a;
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
        AskRunTimePermission.ask(activity, Manifest.permission.CAMERA, 1);
        AskRunTimePermission.ask(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
        AskRunTimePermission.ask(activity, Manifest.permission.READ_EXTERNAL_STORAGE, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CameraActivity", "onCreateView called");
        // Inflate the layout for this fragment
//        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(activity, mCamera);
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

        ((ImageView) controls.findViewById(R.id.snapCameraButton)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takePicture();
                    }
                });
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
        Log.d("CameraActivity", "onResume called");
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
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("CameraActivity", "onPause called");
        mListener = null;
        mPreview.stopPreview();

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

    private void releaseCamera() {
        if (mCamera != null) {
            if (mPreview.isPreviewRunning())
            {
                mPreview.stopPreview();
            }
//            mCamera.release();        // release the camera for other applications
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isResumed()) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void switchCamera() {
        if (mPreview.isPreviewRunning()) {
            mPreview.stopPreview();
        }
        mCamera.release();

        if (CameraPreview.currentCamera == Camera.CameraInfo.CAMERA_FACING_BACK){
            CameraPreview.currentCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            CameraPreview.currentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        try {
            mCamera = Camera.open(CameraPreview.currentCamera);
            mCamera.setDisplayOrientation(90);
            mPreview.setCamera(mCamera);
            mPreview.previewCamera();
        } catch(Exception e) {
            Log.d("CameraActivty", e.toString());
        }
    }

    public void takePicture() {
        if (mPreview.isPreviewRunning()){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    mPreview.takePicture();
                }
            });
            t.run();
        }

    }
}
