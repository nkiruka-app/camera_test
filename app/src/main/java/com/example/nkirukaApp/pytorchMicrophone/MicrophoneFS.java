package com.example.nkirukaApp.pytorchMicrophone;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class MicrophoneFS {
    private final String TAG = MicrophoneFS.class.getName();
    private File file = null;

    //TODO Clear Directory and make String cryptographically random!
    private final String filename = "FEED_BEEF";

    public MicrophoneFS(){ }

    public void setupFile(Context context){
        if(file == null) {
            Log.d(TAG, "Opening File");
            file = new File(context.getFilesDir(), filename);
        }
    }

    public boolean isSetup(){
        return file != null;
    }

    public boolean destroyFile(){
        if(file != null){
            Log.d(TAG, "Deleting File!");
            return file.delete();
        }

        return false;
    }

    public File getFile(){
        return file;
    }
}
