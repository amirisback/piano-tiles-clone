package com.github.gianmartind.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gianmartind.R;


public class CustomToast {
    protected Toast toast;
    protected String text;
    protected int duration;
    protected ImageView imageView;
    protected TextView textView;

    public CustomToast(View view, Context context){
        this.imageView = view.findViewById(R.id.image);
        this.textView = view.findViewById(R.id.text);
        this.toast = new Toast(context);
        this.toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        this.toast.setView(view);
    }

    public void setToast(String text, int duration){
        this.textView.setText(text);
        this.toast.setDuration(duration);
    }

    public void setText(String text){
        this.textView.setText(text);
    }

    public void setDuration(int duration){
        this.toast.setDuration(duration);
    }

    public void show(){
        this.toast.show();
    }

    public void cancel(){
        this.toast.cancel();
    }
}
