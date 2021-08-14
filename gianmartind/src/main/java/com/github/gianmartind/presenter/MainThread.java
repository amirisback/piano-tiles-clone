package com.github.gianmartind.presenter;

import android.graphics.PointF;

public class MainThread extends Thread{
    protected ThreadHandler threadHandler;
    protected int pos;
    protected PointF viewSize;
    protected PointF start;
    protected boolean isClicked;

    public MainThread(ThreadHandler threadHandler, int pos, PointF viewSize){
        this.threadHandler = threadHandler;
        this.pos = pos;
        this.viewSize = viewSize;
        this.isClicked = false;
        this.start = new PointF();
        this.setPoint();
    }

    public void setPoint(){
        if(this.pos == 1){
            this.start.set(0, -this.viewSize.y/4);
        } else if(this.pos == 2){
            this.start.set(this.viewSize.x/4, -this.viewSize.y/4);
        } else if(this.pos == 3){
            this.start.set(this.viewSize.x/2, -this.viewSize.y/4);
        } else if(this.pos == 4){
            this.start.set(this.viewSize.x/4*3, -this.viewSize.y/4);
        }
    }

    public void run(){
        while(check(this.start.y)){
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            this.threadHandler.clearRect(new PointF(this.start.x, this.start.y));
            this.start.set(this.start.x, this.start.y+this.viewSize.y*0.005f);
            this.threadHandler.drawRect(new PointF(this.start.x, this.start.y), this.isClicked);
        }
        this.threadHandler.popOut();
        if(!this.isClicked){
            this.threadHandler.removeHealth();
        }
        return;
    }

    public void checkScore(PointF tap){
        if(this.isClicked) return;
        if(tap.x >= this.start.x && tap.x <= this.start.x + this.viewSize.x/4){
            if(tap.y >= this.start.y && tap.y <= this.start.y + this.viewSize.y/4){
                this.threadHandler.addScore(this.pos);
                this.threadHandler.clearRect(new PointF(this.start.x, this.start.y));
                this.isClicked = true;
            }
        }
    }

    public boolean check(float y){
        if(y >= this.viewSize.y){
            return false;
        }
        return true;
    }
}
