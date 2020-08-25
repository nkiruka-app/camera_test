package com.example.nkirukaApp.pytorchMicrophone;

import android.util.Log;

import java.util.Arrays;

public class PytorchFunctions {
    private static final String TAG = PytorchFunctions.class.getName();
    /*
     * There is no instance definition of this class. Instead it is a namespace for all of the pytorch
     * manipulations and tasks necessary for sending commands.
     */

    public static CommandHandler.CommandEvent containsCommand(byte[] array){
        Log.d(TAG, "Received Array:");
        Log.d(TAG, Arrays.toString(array));
        

        return CommandHandler.CommandEvent.NOTHING;
    }
}
