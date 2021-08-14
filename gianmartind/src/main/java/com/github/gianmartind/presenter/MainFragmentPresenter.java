package com.github.gianmartind.presenter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.widget.ImageView;

import com.github.gianmartind.SettingsPrefSaver;
import com.github.gianmartind.view.CustomToast;

import java.util.LinkedList;

public class MainFragmentPresenter {
    IMainFragment ui;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    Paint transparent;
    ThreadHandler threadHandler;
    LinkedList<MainThread> threads;
    LinkedList<SensorThread> sensorThreads;
    PlayThread playThread;
    PointF viewSize;
    float endPointY;
    float endPointX;
    int score;
    int health;
    SettingsPrefSaver settingsPrefSaver;
    //CountDownTimer toastCountDown;
    CustomToast toast;

    public MainFragmentPresenter(IMainFragment ui, CustomToast toast, SettingsPrefSaver settingsPrefSaver){
        this.ui = ui;
        this.threadHandler = new ThreadHandler(this);
        this.threads = new LinkedList<>();
        this.sensorThreads = new LinkedList<>();
        this.toast = toast;
        this.settingsPrefSaver = settingsPrefSaver;
    }

    public void initiateCanvas(ImageView ivCanvas){
        this.bitmap = Bitmap.createBitmap(ivCanvas.getWidth(), ivCanvas.getHeight(), Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(this.bitmap);
        this.paint = new Paint();
        this.paint.setColor(Color.BLACK);
        this.transparent = new Paint();
        this.transparent.setColor(Color.TRANSPARENT);
        this.transparent.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        this.viewSize = new PointF(ivCanvas.getWidth(), ivCanvas.getHeight());
        this.ui.initiateCanvas(this.bitmap, this.canvas);

        this.endPointY = this.viewSize.y/5;
        this.endPointX = this.viewSize.x/4;

        this.score = 0;
        this.ui.updateScore(this.score);

        this.health = this.settingsPrefSaver.getKeyHealth();
        this.ui.updateHealth(this.health);

        this.playThread = new PlayThread(new PointF(ivCanvas.getWidth(), ivCanvas.getHeight()), this.threadHandler);
        this.playThread.start();

        this.ui.registerSensor();
        Log.d("TAG", "initiateCanvas: " + this.viewSize.y);
    }

    public void drawRect(PointF point){
        this.canvas.drawRect(point.x, point.y, point.x+this.endPointX, point.y+this.endPointY, this.paint);
        //this.canvas.drawCircle(point.x, point.y, 100, this.paint);
        this.ui.updateCanvas(this.canvas);
    }

    public void clearRect(PointF point){
        this.canvas.drawRect(point.x, point.y, point.x+this.endPointX, point.y+this.endPointY, this.transparent);
        //this.canvas.drawCircle(point.x, point.y, 100, this.transparent);
        this.ui.updateCanvas(this.canvas);
    }

    public void generate(int pos) throws InterruptedException {
        if(pos < 5){
            MainThread newThread = new MainThread(this.threadHandler, pos, this.viewSize);
            newThread.start();
            this.threads.addLast(newThread);
        }
        else if(pos == 5){
            this.toast.setText("Tilt Left!");
            SensorThread sensorThread = new SensorThread(this.threadHandler, true);
            sensorThread.start();
            this.sensorThreads.addLast(sensorThread);
            this.toast.show();
        }
        else if(pos == 6){
            this.toast.setText("Tilt Right!");
            SensorThread sensorThread = new SensorThread(this.threadHandler, false);
            sensorThread.start();
            this.sensorThreads.addLast(sensorThread);
            this.toast.show();
        }
    }

    public void stop(){
        this.playThread.stopThread();
    }

    public void checkScore(PointF tap){
        for(int i = 0; i < this.threads.size(); i++){
           this.threads.get(i).checkScore(tap);
        }
    }

    public void checkSensor(float roll){
        for(int i = 0; i < this.sensorThreads.size(); i++){
            this.sensorThreads.get(i).changeRoll(roll);
        }
    }

    public void popOut(){
        //Log.d("TAG", "popOut: " + this.threads.size() + " " + this.inc);
        this.threads.removeFirst();
    }

    public void popSensorOut(){
        //Log.d("TAG", "popSensorOut: " + this.sensorThreads.size() + " " + this.inc);
        this.sensorThreads.removeFirst();
    }

    public void addScore(int pos){
        this.score++;
        this.ui.playNotes(pos);
        //this.toast.setText(Integer.toString(this.score));
        //this.toast.show();
        //this.toastCountDown.start();
        this.ui.updateScore(this.score);
    }

    public void addSensorScore(){
        this.score++;
        this.ui.updateScore(this.score);
        //this.ui.unregisterSensor();
    }

    public void removeHealth(){
        this.health--;
        if(this.health == 0){
            this.playThread.stopThread();
            this.ui.gameOver(this.score);
            this.ui.unregisterSensor();
        }
        this.ui.updateHealth(this.health);
    }

    public interface IMainFragment{
        void updateCanvas(Canvas canvas);
        void initiateCanvas(Bitmap bitmap, Canvas canvas);
        void updateScore(int score);
        void updateHealth(int health);
        void gameOver(int score);
        void registerSensor();
        void unregisterSensor();
        void playNotes(int pos);
    }
}
