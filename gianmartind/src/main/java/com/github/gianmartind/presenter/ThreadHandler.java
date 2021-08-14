package com.github.gianmartind.presenter;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

public class ThreadHandler extends Handler {
    protected final static int DRAW_RECT = 1;
    protected final static int CLEAR_RECT = 2;
    protected final static int ADD_SCORE = 3;
    protected final static int POP_OUT = 4;
    protected final static int GENERATE = 5;
    protected final static int REMOVE_HEALTH = 6;
    protected final static int ADD_SENSOR_SCORE = 7;
    protected final static int POP_SENSOR_OUT = 8;

    protected MainFragmentPresenter mainFragmentPresenter;

    public ThreadHandler(MainFragmentPresenter mainFragmentPresenter){
        this.mainFragmentPresenter = mainFragmentPresenter;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if(msg.what == ThreadHandler.DRAW_RECT){
            PointF param = (PointF) msg.obj;
            this.mainFragmentPresenter.drawRect(param);
        } else if(msg.what == ThreadHandler.CLEAR_RECT){
            PointF param = (PointF) msg.obj;
            this.mainFragmentPresenter.clearRect(param);
        } else if(msg.what == ThreadHandler.ADD_SCORE){
            int param = (int) msg.obj;
            this.mainFragmentPresenter.addScore(param);
        } else if(msg.what == ThreadHandler.POP_OUT){
            this.mainFragmentPresenter.popOut();
        } else if(msg.what == ThreadHandler.GENERATE){
            int param = (int) msg.obj;
            try {
                this.mainFragmentPresenter.generate(param);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if(msg.what == ThreadHandler.REMOVE_HEALTH){
            this.mainFragmentPresenter.removeHealth();
        } else if(msg.what == ThreadHandler.ADD_SENSOR_SCORE){
            this.mainFragmentPresenter.addSensorScore();
        } else if(msg.what == ThreadHandler.POP_SENSOR_OUT){
            this.mainFragmentPresenter.popSensorOut();
        }
    }

    public void drawRect(PointF point, boolean isClicked){
        if(isClicked) return;
        Message msg = new Message();
        msg.what = ThreadHandler.DRAW_RECT;
        msg.obj = point;
        this.sendMessage(msg);
    }

    public void clearRect(PointF point){
        Message msg = new Message();
        msg.what = ThreadHandler.CLEAR_RECT;
        msg.obj = point;
        this.sendMessage(msg);
    }

    public void addScore(int pos){
        Message msg = new Message();
        msg.what = ThreadHandler.ADD_SCORE;
        msg.obj = (int) pos;
        this.sendMessage(msg);
    }

    public void addSensorScore(){
        Message msg = new Message();
        msg.what = ThreadHandler.ADD_SENSOR_SCORE;
        this.sendMessage(msg);
    }

    public void removeHealth(){
        Message msg = new Message();
        msg.what = ThreadHandler.REMOVE_HEALTH;
        this.sendMessage(msg);
    }

    public void popOut(){
        Message msg = new Message();
        msg.what = ThreadHandler.POP_OUT;
        this.sendMessage(msg);
    }

    public void popSensorOut(){
        Message msg = new Message();
        msg.what = ThreadHandler.POP_SENSOR_OUT;
        this.sendMessage(msg);
    }

    public void generate(int pos){
        Message msg = new Message();
        msg.what = ThreadHandler.GENERATE;
        msg.obj = pos;
        this.sendMessage(msg);
    }
}
