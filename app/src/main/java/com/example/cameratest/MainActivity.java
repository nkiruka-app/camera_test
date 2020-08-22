package com.example.cameratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.cameratest.pytorchMicrophone.MicrophoneHelper;

public class MainActivity extends AppCompatActivity implements MicrophoneHelper.CommandEventHandler {
    private MicrophoneHelper mMicHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView testext = findViewById(R.id.tv_testext);
        testext.setText(checkSettingChanged());

        mMicHelper = new MicrophoneHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(MainActivity.class.getName(), "Starting The Recording");
        if(mMicHelper.onRequestPermission(this)){
            Log.d(MainActivity.class.getName(), "Already Had Permission!");
            tryRecording();
        }else{
            Log.d(MainActivity.class.getName(), "Grabbed Permission!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(!mMicHelper.onPermissionResult(requestCode, permissions, grantResults)){
            Log.d(MainActivity.class.getName(), "Mic Helper Returned False on Permission Result. Could not Resolve Permission!");
        }else{
            tryRecording();
        }
    }

    public void tryRecording(){
        Log.d(MainActivity.class.getName(), "Starting the recording!");
        mMicHelper.startRecording();
        new LambdaTask(this,
                new LambdaTask.Task() {
                    @Override
                    public void task() {
                        try {
                            Log.d("Lambda Expression", "Sleeping!");
                            Thread.sleep(1000);
                            Log.d("Lambda Expression", "Done Sleeping!");
                        }catch(Exception e){
                            Log.d("Lambda Expression", "Sleep Interrupted");
                        }
                    }
                },
                new LambdaTask.Task() {
                    @Override
                    public void task() {
                        Log.d("Lambda Expression", "Stopping The Recording!");
                        mMicHelper.stopRecording();
                        Log.d("Lambda Expression", "Recording Stopped!");
                    }
                }
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    // creates the menu icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.camera_test_menu, menu);
        return true;
    }

    // allows clicking on menu icon
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_menu:

                // call the explicit intent
                Intent cameraMenuIntent = new Intent(this, cameramenu.class);
                startActivity(cameraMenuIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String checkSettingChanged() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String trigger = sharedPreferences.getString(
                getString(R.string.pref_camtrig_key),
                getString(R.string.pref_camtrig_default)
        );
        return trigger;
    }

    @Override
    public void onCommandEvent(MicrophoneHelper.CommandEvent command) {
        switch(command){
            case NOTHING:
                Log.d(MainActivity.class.getName(), "Command Received: Nothing");
                break;
            case TAKE_PICTURE:
                Log.d(MainActivity.class.getName(), "Command Received: Take Picture");
                break;
            default:
                Log.e(MainActivity.class.getName(), "Unknown Command Received!!");
        }
    }
}