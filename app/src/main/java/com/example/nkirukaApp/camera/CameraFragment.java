package com.example.nkirukaApp.camera;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nkirukaApp.MainActivity;
import com.example.nkirukaApp.R;

import org.w3c.dom.Text;

public class CameraFragment extends Fragment {
    private static final String TAG = CameraFragment.class.getName();

    // Global Variables (layout elements)
    private Button takePictureButton;
    private TextureView textureView;

    public void setButtonListener(View.OnClickListener listener){
        takePictureButton.setOnClickListener(listener);
    }

    public void setSurfaceListener(TextureView.SurfaceTextureListener listener){
        textureView.setSurfaceTextureListener(listener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.camera_view, container, true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Populating Fields!");

        // TODO BUGG!!
        textureView = getActivity().findViewById(R.id.texture);
        Log.d(TAG, "Texture View: " + textureView.toString());
        takePictureButton = getActivity().findViewById(R.id.btn_takepicture);
    }


    // Initializes the camera preview (calls updatePreview)
    protected Surface createCameraPreview(int width, int height) {
        SurfaceTexture texture = textureView.getSurfaceTexture();
        assert texture != null;
        texture.setDefaultBufferSize(width, height);
        return new Surface(texture);
    }

    public void makeToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public SurfaceTexture getSurfaceOfView() {
        return textureView.getSurfaceTexture();
    }
    public TextureView getTextureView(){
        return textureView;
    }
}
