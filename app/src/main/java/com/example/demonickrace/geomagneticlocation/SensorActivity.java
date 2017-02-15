package com.example.demonickrace.geomagneticlocation;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class SensorActivity extends AppCompatActivity implements SensorEventListener ,View.OnClickListener{

    private SensorManager mSensorManager;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mMagnetometerUncalibratedReading = new float[6];

    int count;
    int tp = 1;
    final int len = 10;
    /*
    //精度 Latitude
    float lat;
    //緯度 Longitude
    float lng;
    //海拔 Altitude
    float alt;
    */
    //time
    String time;

    StringBuffer data;
    boolean recording = false;

    private float currentDegree = 0.0f;

//    private float[] temp = new float[2];
//    private int count = 0;

    TextView compassDegree;

    TextView m_x;
    TextView m_y;
    TextView m_z;
    TextView m_un_x;
    TextView m_un_y;
    TextView m_un_z;
    TextView m_un_bias_x;
    TextView m_un_bias_y;
    TextView m_un_bias_z;
    TextView gx;
    TextView gy;
    TextView gz;

    TextView position;
    EditText filname;

    Button startBtn;
    Button stopBtn;
    Button saveBtn;

    CheckBox uncalibrated;

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        img = (ImageView) findViewById(R.id.compass);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        initUI();

        data = new StringBuffer();

        verifyStoragePermissions(this);
        Log.i("SensorActivity", "onResume");
    }

    private void initUI() {
        compassDegree = (TextView) findViewById(R.id.compassDegree);

        m_x = (TextView) findViewById(R.id.m_x);
        m_y = (TextView) findViewById(R.id.m_y);
        m_z = (TextView) findViewById(R.id.m_z);

        m_un_x = (TextView) findViewById(R.id.m_un_x);
        m_un_y = (TextView) findViewById(R.id.m_un_y);
        m_un_z = (TextView) findViewById(R.id.m_un_z);

        m_un_bias_x = (TextView) findViewById(R.id.m_un_bias_x);
        m_un_bias_y = (TextView) findViewById(R.id.m_un_bias_y);
        m_un_bias_z = (TextView) findViewById(R.id.m_un_bias_z);

        gx = (TextView)findViewById(R.id.gx);
        gy = (TextView)findViewById(R.id.gy);
        gz = (TextView)findViewById(R.id.gz);

        startBtn = (Button) findViewById(R.id.startBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        stopBtn.setEnabled(false);
        saveBtn.setEnabled(false);

        position = (TextView) findViewById(R.id.position);
        filname = (EditText) findViewById(R.id.filename);

        uncalibrated = (CheckBox) findViewById(R.id.uncalibrated);


        Log.i("SensorActivity", "init");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.startBtn:
                Log.i("onClick","startBtn");
                Toast.makeText(view.getContext(), "開始記錄......", Toast.LENGTH_SHORT).show();
                startBtn.setEnabled(false);
                //stopBtn.setEnabled(true);
                saveBtn.setEnabled(false);
                position.setEnabled(false);
                recording = true;
                count = 0;
                break;
            /*
            case R.id.stopBtn:
                Log.i("onClick","stopBtn");
                Toast.makeText(view.getContext(), "暫停紀錄......", Toast.LENGTH_SHORT).show();
                startBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                saveBtn.setEnabled(true);
                recording = false;
                break;
                */
            case R.id.saveBtn:
                Log.i("onClick","saveBtn");
                Toast.makeText(view.getContext(), "儲存紀錄......", Toast.LENGTH_SHORT).show();
                if(!recording){
                    saveFile();
                }
                break;
            default:
                break;
        }

    }

    private void saveFile(){
        String dir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        String filenameStr;
        if(filname.getText().toString().isEmpty()){
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");//dd/MM/yyyy
            Date now = new Date();
            String strDate = sdfDate.format(now);

            filenameStr = dir + "/" + strDate + ".txt";
        }else {
            filenameStr = dir + "/" + filname.getText().toString() + ".txt";
        }

//        String filename = dir + "/myfile.txt";

        FileOutputStream outputStream;

        try {

            File file = new java.io.File(filenameStr);

            outputStream = new FileOutputStream (file, true);
            /*
            if(uncalibrated.isSelected()){
                String title = "position ,  un_x ,  un_y ,  un_z , un_sum , un_x_bias , un_y_bias , un_z_bias , time \n";
                data.append(title);
            }else{
                String title = "position ,   x   ,   y   ,   z   , time \n";
                data.append(title);
            }
            */
            outputStream.write(String.valueOf(data).getBytes());
            outputStream.close();
            Log.i("saved filepath = ",filenameStr);
            Toast.makeText(this, filenameStr + ", save successed!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("saveFile error = ",e.toString());
            Toast.makeText(this, filenameStr + ", save failed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);


        Log.i("SensorActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Don't receive any more updates from either sensor.
        mSensorManager.unregisterListener(this);

        Log.i("SensorActivity", "onPause");
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);


            // alpha is calculated as t / (t + dT)
            // with t, the low-pass filter's time-constant
            // and dT, the event delivery rate
            /*
            final float alpha = 0.8;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
            */

            mAccelerometerReading[0] = event.values[0];
            mAccelerometerReading[1] = event.values[1];
            mAccelerometerReading[2] = event.values[2];

            gx.setText("gx : " + String.valueOf(Math.rint(mAccelerometerReading[0]*1000)/1000));
            gy.setText("gy : " + String.valueOf(Math.rint(mAccelerometerReading[1]*1000)/1000));
            gz.setText("gz : " + String.valueOf(Math.rint(mAccelerometerReading[2]*1000)/1000));

            Log.i("TYPE_ACCELEROMETER = ",Arrays.toString(mAccelerometerReading));
        }


        float[] magnetic = new float[11];

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);

            m_x.setText("x : " + mMagnetometerReading[0]);
            m_y.setText("y : " + mMagnetometerReading[1]);
            m_z.setText("z : " + mMagnetometerReading[2]);

            magnetic[0] = mMagnetometerReading[0];
            magnetic[1] = mMagnetometerReading[1];
            magnetic[2] = mMagnetometerReading[2];
            magnetic[3] = (float) Math.sqrt(mMagnetometerReading[0] * mMagnetometerReading[0]
                    + mMagnetometerReading[1] * mMagnetometerReading[1]
                    + mMagnetometerReading[2] * mMagnetometerReading[2]);

            Log.i("MAGNETIC_FIELD", Arrays.toString(mMagnetometerReading));
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {
            System.arraycopy(event.values, 0, mMagnetometerUncalibratedReading,
                    0, mMagnetometerUncalibratedReading.length);

            m_un_x.setText("x : " + mMagnetometerUncalibratedReading[0]);
            m_un_y.setText("y : " + mMagnetometerUncalibratedReading[1]);
            m_un_z.setText("z : " + mMagnetometerUncalibratedReading[2]);

            m_un_bias_x.setText("x-bias : " + mMagnetometerUncalibratedReading[3]);
            m_un_bias_y.setText("y-bias : " + mMagnetometerUncalibratedReading[4]);
            m_un_bias_z.setText("z-bias : " + mMagnetometerUncalibratedReading[5]);

            magnetic[4] = mMagnetometerUncalibratedReading[0];
            magnetic[5] = mMagnetometerUncalibratedReading[1];
            magnetic[6] = mMagnetometerUncalibratedReading[2];

            magnetic[7] = (float) Math.sqrt(mMagnetometerUncalibratedReading[0] * mMagnetometerUncalibratedReading[0]
                    + mMagnetometerUncalibratedReading[1] * mMagnetometerUncalibratedReading[1]
                    + mMagnetometerUncalibratedReading[2] * mMagnetometerUncalibratedReading[2]);


            magnetic[8] = mMagnetometerUncalibratedReading[3];
            magnetic[9] = mMagnetometerUncalibratedReading[4];
            magnetic[10] = mMagnetometerUncalibratedReading[5];

            //String str = "x : " + mMagnetometerUncalibratedReading[0] + " , y : " + mMagnetometerUncalibratedReading[1] + " , z : " + mMagnetometerUncalibratedReading[2]
            //        + " , x-bias : " + mMagnetometerUncalibratedReading[3] + " , y-bias : " + mMagnetometerUncalibratedReading[4] + " , z-bias : " + mMagnetometerUncalibratedReading[5];


            Log.i("MAGNETIC_UNCALIBRATED", Arrays.toString(mMagnetometerUncalibratedReading));
        }


        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // get the angle around the z-axis rotated
            float degree = Math.round(event.values[0]);

            compassDegree.setText("Heading: " + Float.toString(degree) + " degrees");

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            // how long the animation will take place
            ra.setDuration(100);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation
            img.startAnimation(ra);

            currentDegree = -degree;

            Log.i("TYPE_ORIENTATION", Float.toString(degree));
        }

        //record the data
        if(recording){
            SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss.SSS");//dd/MM/yyyy
            Date now = new Date();
            String strDate = sdfDate.format(now);

            if(uncalibrated.isSelected() && event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED){

                count++;
                data.append(position.getText().toString()).append(" , ")
                    .append(magnetic[4]).append(" , ")
                    .append(magnetic[5]).append(" , ")
                    .append(magnetic[6]).append(" , ")
                    .append(magnetic[7]).append(" , ")
                    .append(magnetic[8]).append(" , ")
                    .append(magnetic[9]).append(" , ")
                    .append(magnetic[10]).append(" , ")

                    .append(mAccelerometerReading[0]).append(" , ")
                    .append(mAccelerometerReading[1]).append(" , ")
                    .append(mAccelerometerReading[2]).append(" , ")

                    .append(strDate).append("\n");
            }else if(!uncalibrated.isSelected() && event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                count++;

                data.append(position.getText().toString()).append(" , ")
                    .append(magnetic[0]).append(" , ")
                    .append(magnetic[1]).append(" , ")
                    .append(magnetic[2]).append(" , ")
                    .append(magnetic[3]).append(" , ")

                    .append(mAccelerometerReading[0]).append(" , ")
                    .append(mAccelerometerReading[1]).append(" , ")
                    .append(mAccelerometerReading[2]).append(" , ")

                    .append(strDate).append("\n");
            }
        }

        if(count == len){
            count = 0;
            recording = false;
            startBtn.setEnabled(true);
            saveBtn.setEnabled(true);
            position.setEnabled(true);
            Toast.makeText(this, "座標:" + position.getText().toString() + "," + String.valueOf(len) + "筆資料已儲存完畢!", Toast.LENGTH_SHORT).show();
            tp++;
            position.setText(String.valueOf(tp));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i("onAccuracyChanged","");

    }


    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}