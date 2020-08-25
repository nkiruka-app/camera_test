package com.example.nkirukaApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;

import android.view.TextureView;
import android.view.View;
import android.widget.Toast;


import com.example.nkirukaApp.camera.CameraFragment;
import com.example.nkirukaApp.camera.CameraIOHelper;
import com.example.nkirukaApp.camera.CameraMenuActivity;
import com.example.nkirukaApp.pytorchMicrophone.CommandHandler;
import com.example.nkirukaApp.pytorchMicrophone.MicrophoneHelper;
import com.example.nkirukaApp.utility.LambdaTask;

// Based on tutorial: https://inducesmile.com/android/android-camera2-api-example-tutorial/
public class MainActivity extends AppCompatActivity implements CommandHandler {

    private CameraIOHelper mCameraHelper = null;
    private MicrophoneHelper mMicHelper = null;
    private final String TAG = MainActivity.class.getName();

    private CameraFragment mCameraFragment = null;


    /*********** State Callback Functions ***********/

    final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if(mCameraHelper != null){
                mCameraHelper.openCamera(MainActivity.this);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    final View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mCameraHelper != null){
                mCameraHelper.takePicture(MainActivity.this);
            }
        }
    };


    /*********** Methods and Lifecycle ***********/


    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

        // This is where Fragment is ready to be processed/used with CameraIO
        if(fragment instanceof CameraFragment){
            mCameraFragment = (CameraFragment) fragment;
            Log.d(TAG, "Camera Preview Found!");

            mCameraHelper = new CameraIOHelper(mCameraFragment);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMicHelper = new MicrophoneHelper(this);

    }

    @Override   // I guess this is what handles the permission request?
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!mCameraHelper.onPermissionResult(requestCode, permissions, grantResults)
            && !mMicHelper.onPermissionResult(requestCode, permissions, grantResults)
            && grantResults.length > 0
        ) {
            /* Sometimes This method returns with no results which is weird af...
             * but we shouldn't kick the user on this occurrence. Instead we should
             * close the app if they reject either (both) permissions in a given response. And there
             * was one to respond to.
             */
            Toast.makeText(MainActivity.this, "Sorry! You can't use this app without granting permission", Toast.LENGTH_LONG).show();
            finish();
        }else if(grantResults.length > 0){
            // Without the if, the function would just keep calling the function below... icky!
            turnOnIO();
        }
    }

    @Override   // when the app resumes (lifecycle state), want to open the camera again
    protected void onResume() {
        super.onResume();
        turnOnIO();
    }

    @Override   // when the app is paused (lifecycle state), want to close the camera so other apps can use it
    protected void onPause() {
        Log.d(TAG, "onPause");
        if(mCameraHelper != null) {
            Log.d(TAG, "Releasing Camera");
            mCameraHelper.closeCamera();
            mCameraHelper.stopBackgroundThread();
        }
        super.onPause();
    }
        

    @Override   // creates the menu icon
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.camera_test_menu, menu);
        return true;
    }

    @Override   // allows clicking on menu icon
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_menu:

                // call the explicit intent
                Intent cameraMenuIntent = new Intent(this, CameraMenuActivity.class);
                startActivity(cameraMenuIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /********* HAND CRAFTED HELPER FUNCTIONS ********/

    // This is my own function - checks whether the menu setting has changed and returns the trigger
    private String checkSettingChanged() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String trigger = sharedPreferences.getString(
                getString(R.string.pref_camtrig_key),
                getString(R.string.pref_camtrig_default)
        );
        return trigger;
    }

    // TODO make this an anonymous class rather than an implemented method
    @Override
    public void onCommandEvent(CommandEvent event) {
        switch(event){
            case NOTHING:
                Log.d(MainActivity.class.getName(), "Command Received: Nothing");
                break;
            case TAKE_PICTURE:
                Log.d(MainActivity.class.getName(), "Command Received: Take Picture");
                mCameraHelper.takePicture(this);
                break;
            default:
                Log.e(MainActivity.class.getName(), "Unknown Command Received!!");
        }
    }

    private void turnOnIO(){
        Log.d(TAG, "onResume");
        if(mCameraHelper != null) {
            Log.d(TAG, "Resuming Camera");
            mCameraHelper.startBackgroundThread();
            mCameraHelper.tryStartPreview(this, textureListener);
            mCameraFragment.setButtonListener(buttonListener);
            if(mCameraHelper.requestPermission(this)){
                Log.d(MainActivity.class.getName(), "Already Had Cam Permission!");
            }else{
                Log.d(MainActivity.class.getName(), "Grabbed Cam Permission!");
                // Can Only Grab 1 permission at a time!
                return;
            }
        }

        Log.d(MainActivity.class.getName(), "Starting The Recording");
        if(mMicHelper.onRequestPermission(this)){
            Log.d(MainActivity.class.getName(), "Already Had Mic Permission!");
        }else{
            Log.d(MainActivity.class.getName(), "Grabbed Mic Permission!");
        }
    }


}