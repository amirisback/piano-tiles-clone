package com.github.gianmartind.presenter;

import android.graphics.PointF;

import java.util.Random;

public class PlayThread extends Thread{
    protected PointF viewSize;
    protected boolean isStopped;
    ThreadHandler threadHandler;
    Random random;
    int[] array;

    public PlayThread(PointF viewSize, ThreadHandler threadHandler){
        this.random = new Random();
        this.viewSize = viewSize;
        this.threadHandler = threadHandler;
        this.isStopped = false;
        this.array = new int[42];
        for(int i = 0; i < 10; i++){
            this.array[i] = 1;
        }
        for(int i = 10; i < 20; i++){
            this.array[i] = 2;
        }
        for(int i = 20; i < 30; i++){
            this.array[i] = 3;
        }
        for(int i = 30; i < 40; i++){
            this.array[i] = 4;
        }
        this.array[40] = 5;
        this.array[41] = 6;
    }

    public void stopThread(){
        this.isStopped = true;
    }

    public void run(){
        int exclude = this.random.nextInt(6) + 1;
        while(!this.isStopped){
            int[] arrayExclude;
            if(exclude == 1){
                arrayExclude = new int[]{0,1,2,3,4,5,6,7,8,9};
            } else if(exclude == 2){
                arrayExclude = new int[]{10,11,12,13,14,15,16,17,18,19};
            } else if(exclude == 3){
                arrayExclude = new int[]{20,21,22,23,24,25,26,27,28,29};
            } else if(exclude == 4){
                arrayExclude = new int[]{30,31,32,33,34,35,36,37,38,39};
            } else if(exclude == 5){
                arrayExclude = new int[]{40};
            } else {
                arrayExclude = new int[]{41};
            }
            int num = this.getRandomWithExclusion(1, 41, arrayExclude);
            try {
                Thread.sleep((long) (this.viewSize.y * 0.40265277));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.threadHandler.generate(this.array[num]);
            exclude = this.array[num];
        }
        return;
    }

    public int getRandomWithExclusion(int start, int end, int... exclude) {
        int random = start + this.random.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }
}
