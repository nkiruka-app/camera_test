package com.example.nkirukaApp.pytorchMicrophone;

import android.content.Context;
import android.util.Log;

import com.example.nkirukaApp.R;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class PytorchFunctions {
    private static final String TAG = PytorchFunctions.class.getName();
    /*
     * There is no instance definition of this class. Instead it is a namespace for all of the pytorch
     * manipulations and tasks necessary for sending commands.
     */

    private static Module module = null;
    public static void setModule(Module module){
        PytorchFunctions.module = module;
    }

    public static void loadModule(Context context){
        try {
            module = Module.load(assetFilePath(context, "serialized_model.pt"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static CommandHandler.CommandEvent containsCommand(byte[] array){
        Log.d(TAG, "Received Array:");
        Log.d(TAG, Arrays.toString(array));

        Tensor inputTensor = Tensor.fromBlob(array, new long[]{ (long) array.length});
        Log.d(TAG, "Input is: " + inputTensor.toString());
        Log.d(TAG, "IValue is: " + IValue.from(inputTensor).toString());
        Log.d(TAG, "Module is: " + module.toString());
        Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();

        Log.d(TAG, "Output is:" + outputTensor.toString());

        return CommandHandler.CommandEvent.NOTHING;
    }

    /** From https://github.com/pytorch/android-demo-app/blob/79c4a74aad2045b8dc16947c7b7f85490fa1cfef/HelloWorldApp/app/src/main/java/org/pytorch/helloworld/MainActivity.java#L81
     * Copies specified asset to the file in /files app directory and returns this file absolute path.
     *
     * @return absolute file path
     */
    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }
}
