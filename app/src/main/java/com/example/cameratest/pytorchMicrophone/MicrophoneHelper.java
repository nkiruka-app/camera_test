package com.example.cameratest.pytorchMicrophone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class MicrophoneHelper {

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
        return true;
    }

    public boolean onPermissionResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ){
        if(requestCode == REQUEST_RECORD_AUDIO_PERMISSION){
            hasPermission =  (grantResults[0] == PackageManager.PERMISSION_GRANTED);
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
}
