package com.example.nkirukaApp.pytorchMicrophone;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.example.nkirukaApp.utility.LambdaTask;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class MicrophoneReaderTask{
    private static final String TAG = MicrophoneReaderTask.class.getName();
    public static final int BUFFER_SIZE = 1024;
    public static final int PAGE_SIZE = 4096;

    FileDescriptor input;
    FileInputStream inputStream;
    boolean shouldStop = false;
    LambdaTask task = null;

    CommandHandler handle;

    /* Tasks */
    final LambdaTask.Task start = new LambdaTask.Task() {
        public void task(WeakReference<Activity> activityWeakReference) {
            Log.d(TAG, "Starting To Listen!");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int read = 0;
            int size = 0;
            byte[] data = new byte[BUFFER_SIZE];
            try {
                while (!shouldStop && activityWeakReference.get() != null) {
                    while ((read = inputStream.read(data, 0, data.length)) != -1) {
                        byteArrayOutputStream.write(data, 0, read);
                        size += read;
                        while(size > PAGE_SIZE){
                            sendDataToPytorch(activityWeakReference.get(), byteArrayOutputStream);
                        }
                    }
                }
            }catch(IOException e){
                Log.e(TAG, "IOException Occurred!");
                Log.e(TAG, e.toString());
            }
        }
    };

    final LambdaTask.Task finish = new LambdaTask.Task() {
        public void task(WeakReference<Activity> activityWeakReference){
            Log.d(TAG, "Closing Reader Task");
            try {
                inputStream.close();
            }catch(IOException e){
                Log.e(TAG, "IOException Occurred!");
                Log.e(TAG, e.toString());
            }
        }
    };

    public MicrophoneReaderTask(Activity activity, MicrophonePipe pipe, CommandHandler handle){
        input = pipe.getReadDescriptor();
        inputStream = new FileInputStream(input);
        task = new LambdaTask(activity, start, finish);
        this.handle = handle;
    }

    private void sendDataToPytorch(Activity activity, ByteArrayOutputStream out){
        if(activity != null){

            try {
                out.flush();
            }catch(IOException e){
                Log.e(TAG, "IOException Occurred!");
                Log.e(TAG, e.toString());
            }

            final byte[] data = out.toByteArray();

            new LambdaTask(activity,
                    new LambdaTask.Task() {
                        @Override
                        public void task(WeakReference<Activity> activity) {
                            CommandHandler.CommandEvent command =
                                    PytorchFunctions.containsCommand(data);
                            if(activity.get() != null){
                                handle.onCommandEvent(command);
                            }
                        }
                    },
                    new LambdaTask.Task() {
                        @Override
                        public void task(WeakReference<Activity> activity) {
                            Log.d("Lambda Task", "Ending Command Helper Task");
                        }
                    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
        }
    }

    public void release(){
        shouldStop = true;
    }

    public void shouldStop(){
        shouldStop = true;
    }


}
