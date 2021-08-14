package com.github.gianmartind.presenter;

public class SensorThread extends Thread{
    protected ThreadHandler threadHandler;
    float roll;
    int counter;
    boolean isLeft;

    public SensorThread(ThreadHandler threadHandler, boolean isLeft){
        this.threadHandler = threadHandler;
        this.isLeft = isLeft;
        this.roll = 0;
        this.counter = 0;
    }

    public void changeRoll(float roll){
        this.roll = roll;
    }

    public void run(){
        while (this.counter < 2000){
            if(checkValid()){
                this.threadHandler.addSensorScore();
                return;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.threadHandler.popSensorOut();
        return;
    }

    public boolean checkValid(){
        if(this.isLeft){
            if(this.roll < -0.8f){
                return true;
            }
        } else if(!this.isLeft){
            if(this.roll > 0.8f){
                return true;
            }
        }
        return false;
    }
}
