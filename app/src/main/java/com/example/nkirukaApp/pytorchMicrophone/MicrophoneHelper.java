package com.example.nkirukaApp.pytorchMicrophone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.example.nkirukaApp.MainActivity;

import java.io.File;
import java.io.IOException;

public class MicrophoneHelper {

    private static final String TAG = MicrophoneHelper.class.getName();

    // Commands for when a vocal command is resolved
    private CommandHandler handle;

    // Permissions Fields
    private final String MIC_PERMISSION = Manifest.permission.RECORD_AUDIO;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean hasPermission = false;

    // Microphone Objects
    MediaRecorder recorder;

    // File Output
    // MicrophonePipe pipe = null;
    MicrophoneFS fs;

    // Background Task for Commands
    //MicrophoneReaderTask readerTask = null;


    public MicrophoneHelper(CommandHandler handle){
        this.handle = handle;
        //this.pipe = new MicrophonePipe();
        this.fs = new MicrophoneFS();
    }

    public boolean onRequestPermission(Activity activity){
        int permission = PermissionChecker.checkSelfPermission(activity.getApplicationContext(), MIC_PERMISSION);

        if(hasPermission || permission == PermissionChecker.PERMISSION_GRANTED){
            Log.d(MicrophoneHelper.class.getName(), "Already has Permission");
            hasPermission = true;
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

    public boolean startRecording(Activity activity){
        fs.setupFile(activity);

        if(recorder == null && hasPermission){
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(fs.getFile().getPath());
            //recorder.setOutputFile(pipe.getWriteDescriptor());
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Log.i(TAG, "Error");
                }
            });

            // Setup reading in the background
            // readerTask = new MicrophoneReaderTask(activity, pipe, handle);
            try{
                recorder.prepare();
                recorder.start();
                return true;
            }catch(IOException e){
                Log.e(MicrophoneHelper.class.getName(), "Prepare failed on start record!");
                Log.e(MicrophoneHelper.class.getName(), e.toString());
            }catch(Exception e){
                Log.e(TAG, "Exception Occurred!");
                Log.e(TAG, e.toString());
            }

            return false;
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
            recorder.reset();
            recorder.release();
            recorder = null;
            return true;
        }

        return false;
    }

    public MicrophoneFS getFileSystem(){
        return this.fs;
    }
}
