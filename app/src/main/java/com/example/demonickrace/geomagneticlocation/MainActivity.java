package com.example.demonickrace.geomagneticlocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn1;
    ImageView compass;


    float currentDegree = 0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        Log.i("MainActivity","onCreate");
    }

    private void init(){
        btn1 = (Button)findViewById(R.id.button);
        compass = (ImageView)findViewById(R.id.compass);

        Log.i("MainActivity","init");
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.button:
                Intent intent = new Intent();
                intent.setClass( MainActivity.this, SensorActivity.class );
                startActivity(intent);
                Log.i("onClick","R.id.button");
                break;
            default:
                break;
        }

        Log.i("MainActivity","onClick");
    }
}
