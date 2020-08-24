package com.example.nkirukaApp.pytorchMicrophone;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

public class MicrophonePipe {
    private static final String TAG = MicrophonePipe.class.getName();

    // https://stackoverflow.com/questions/13974234/android-record-mic-to-bytearray-without-saving-audio-file
    ParcelFileDescriptor[] descriptors;
    ParcelFileDescriptor parcelRead;
    ParcelFileDescriptor parcelWrite;

    InputStream inputStream;

    public MicrophonePipe(){
        try {
            descriptors = ParcelFileDescriptor.createPipe();
            parcelRead = new ParcelFileDescriptor(descriptors[0]);
            parcelWrite = new ParcelFileDescriptor(descriptors[1]);
        }catch (IOException e){
            Log.e(TAG, "IOException Occurred!");
        }
    }

    public FileDescriptor getWriteDescriptor(){
        return parcelWrite.getFileDescriptor();
    }

    public FileDescriptor getReadDescriptor(){
        return parcelRead.getFileDescriptor();
    }
}
