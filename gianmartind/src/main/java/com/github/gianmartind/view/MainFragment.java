package com.github.gianmartind.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.gianmartind.R;
import com.github.gianmartind.FragmentListener;
import com.github.gianmartind.SettingsPrefSaver;
import com.github.gianmartind.presenter.MainFragmentPresenter;

public class MainFragment extends Fragment implements MainFragmentPresenter.IMainFragment, View.OnClickListener, View.OnTouchListener, SensorEventListener {
    FragmentListener fragmentListener;
    MainFragmentPresenter mainFragmentPresenter;
    Button startButton;
    ImageView ivCanvas;
    Canvas canvas;
    Bitmap bitmap;
    Button score, health;
    Boolean initiated;
    SettingsPrefSaver settingsPrefSaver;
    CustomToast toast;
    SensorManager sensorManager;
    Sensor accelerometer, magnetometer;
    float[] accelerometerReading, magnetometerReading;
    SoundPool soundPool;
    int pianoA, pianoB, pianoC, pianoD;
    public MainFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        View toastView = inflater.inflate(R.layout.custom_toast, container, false);
        this.toast = new CustomToast(toastView, getActivity().getApplicationContext());
        this.ivCanvas = view.findViewById(R.id.ivCanvas);
        this.startButton = view.findViewById(R.id.start_btn);
        this.score = view.findViewById(R.id.score);
        this.health = view.findViewById(R.id.health);

        this.settingsPrefSaver = new SettingsPrefSaver(this.getActivity());
        this.startButton.setOnClickListener(this);
        this.health.setOnClickListener(this);

        this.ivCanvas.setOnTouchListener(this);
        this.mainFragmentPresenter = new MainFragmentPresenter(this, this.toast, this.settingsPrefSaver);

        this.initiated = false;

        this.sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.magnetometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        this.accelerometerReading = new float[3];
        this.magnetometerReading = new float[3];
        this.soundPool = new SoundPool.Builder().setMaxStreams(10).build();
        this.pianoA = this.soundPool.load(this.getActivity(), R.raw.piano_a, 1);
        this.pianoB = this.soundPool.load(this.getActivity(), R.raw.piano_b, 1);
        this.pianoC = this.soundPool.load(this.getActivity(), R.raw.piano_c, 1);
        this.pianoD = this.soundPool.load(this.getActivity(), R.raw.piano_d, 1);

        return view;
    }

    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof FragmentListener){
            this.fragmentListener = (FragmentListener) context;
        } else{
            throw new ClassCastException(context.toString() + " must implement FragmentListener");
        }
    }

    @Override
    public void updateCanvas(Canvas canvas) {
        this.canvas = canvas;
        this.ivCanvas.postInvalidate();
    }

    @Override
    public void initiateCanvas(Bitmap bitmap, Canvas canvas) {
        this.bitmap = bitmap;
        this.ivCanvas.setImageBitmap(this.bitmap);
        this.canvas = canvas;
    }

    @Override
    public void updateScore(int score) {
        this.score.setText(Integer.toString(score));
    }

    @Override
    public void updateHealth(int health) {
        if(health > 0){
            this.health.setText(Integer.toString(health));
        }
    }

    @Override
    public void gameOver(int score) {
        this.fragmentListener.setScore(score);
        this.fragmentListener.changePage(2);
    }

    @Override
    public void registerSensor() {
        if(this.accelerometer != null){
            this.sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(this.magnetometer != null){
            this.sensorManager.registerListener(this, this.magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void unregisterSensor() {
        this.sensorManager.unregisterListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.sensorManager.unregisterListener(this);
    }

    @Override
    public void playNotes(int pos) {
        if(pos == 1){
            this.soundPool.play(this.pianoA, 1, 1, 1, 0, 1f);
            //this.pianoA.release();
        } else if(pos == 2){
            this.soundPool.play(this.pianoB, 1, 1, 1, 0, 1f);
            //this.pianoB.release();
        } else if(pos == 3){
            this.soundPool.play(this.pianoC, 1, 1, 1, 0, 1f);
            //this.pianoC.release();
        } else if(pos == 4){
            this.soundPool.play(this.pianoD, 1, 1, 1, 0, 1f);
            //this.pianoD.release();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == this.startButton){
            if(!this.initiated){
                this.mainFragmentPresenter.initiateCanvas(this.ivCanvas);
                this.startButton.setVisibility(View.INVISIBLE);
                this.score.setVisibility(View.VISIBLE);
                this.health.setVisibility(View.VISIBLE);
                this.initiated = true;
            }
        } else if(v == this.health){
            for(int i = 0; i < this.settingsPrefSaver.getKeyHealth(); i++){
                this.mainFragmentPresenter.removeHealth();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            this.mainFragmentPresenter.checkScore(new PointF(event.getX(), event.getY()));
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType){
            case Sensor.TYPE_ACCELEROMETER:
                this.accelerometerReading = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                this.magnetometerReading = event.values.clone();
                break;
        }

        final float[] rotationMatrix = new float[9];
        this.sensorManager.getRotationMatrix(rotationMatrix, null, this.accelerometerReading, this.magnetometerReading);

        final float[] orientationAngles = new float[3];
        this.sensorManager.getOrientation(rotationMatrix, orientationAngles);

        //float azimuth = orientationAngles[0];
        //float pitch = orientationAngles[1];
        float roll = orientationAngles[2];
        if(roll > 0.8f || roll < -0.8f){
            this.mainFragmentPresenter.checkSensor(roll);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
