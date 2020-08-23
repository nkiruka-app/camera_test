package com.example.nkirukaApp.pytorchMicrophone;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.example.nkirukaApp.MainActivity;
import com.example.nkirukaApp.utility.LambdaTask;

import java.io.IOException;

public class MicrophoneHelper {

    private static final String TAG = MicrophoneHelper.class.getName();

    public enum CommandEvent {
        NOTHING,
        TAKE_PICTURE
    }

    public interface CommandEventHandler{
        public void onCommandEvent(CommandEvent command);
    }

    // Commands for when a vocal command is resolved
    private CommandEventHandler handle;

    // Permissions Fields
    private final String MIC_PERMISSION = Manifest.permission.RECORD_AUDIO;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean hasPermission = false;

    // Microphone Objects
    MediaRecorder recorder;
    private static final String filename = "file.mp3";



    public MicrophoneHelper(CommandEventHandler handle){
        this.handle = handle;
    }

    public boolean onRequestPermission(Activity activity){
        int permission = PermissionChecker.checkSelfPermission(activity.getApplicationContext(), MIC_PERMISSION);

        if(permission == PermissionChecker.PERMISSION_GRANTED){
            Log.d(MicrophoneHelper.class.getName(), "Already has Permission");
            return true;
        }
        ActivityCompat.requestPermissions(activity, new String[]{MIC_PERMISSION}, REQUEST_RECORD_AUDIO_PERMISSION);
        return false;
    }

    public boolean onPermissionResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ){
        if(requestCode == REQUEST_RECORD_AUDIO_PERMISSION){
            hasPermission =  (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            Log.d(TAG, "Attempted Mic Permission: " + (Boolean.valueOf(hasPermission)).toString());
            return hasPermission;
        }

        return false;
    }

    public boolean startRecording(){
        if(recorder == null && hasPermission){
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setOutputFile(filename);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            try{
                recorder.prepare();
            }catch(IOException e){
                Log.e(MicrophoneHelper.class.getName(), "Prepare failed on start record!");
                Log.e(MicrophoneHelper.class.getName(), e.toString());
                return false;
            }

            recorder.start();
            return true;
        }else if(!hasPermission){
            Log.e(MicrophoneHelper.class.getName(), " Tried to record without permission!");
        }else{
            Log.e(MicrophoneHelper.class.getName(), "Already Recording");
        }

        return false;
    }

    public boolean stopRecording(){
        if(recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
            return true;
        }

        return false;
    }

    public void tryRecording(Activity activity){
        Log.d(MainActivity.class.getName(), "Starting the recording!");
        startRecording();
        new LambdaTask(activity,
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
                        stopRecording();
                        Log.d("Lambda Expression", "Recording Stopped!");
                    }
                }
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
    }
}
