package com.example.cameratest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView testext = findViewById(R.id.tv_testext);
        testext.setText(checkSettingChanged());
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
}