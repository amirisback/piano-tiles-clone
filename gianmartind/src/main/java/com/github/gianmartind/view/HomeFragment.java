package com.github.gianmartind.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.gianmartind.R;
import com.github.gianmartind.FragmentListener;

public class HomeFragment extends Fragment implements View.OnClickListener {
    Button startGame, highScore, settings, exit;
    CustomToast toast;
    FragmentListener fragmentListener;
    MediaPlayer song;

    public HomeFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        View toastView = inflater.inflate(R.layout.custom_toast, container, false);
        this.toast = new CustomToast(toastView, getActivity().getApplicationContext());
        this.startGame = view.findViewById(R.id.btn_start);
        this.highScore = view.findViewById(R.id.btn_high_score);
        this.settings = view.findViewById(R.id.btn_settings);
        this.exit = view.findViewById(R.id.btn_exit);

        this.startGame.setOnClickListener(this);
        this.highScore.setOnClickListener(this);
        this.settings.setOnClickListener(this);
        this.exit.setOnClickListener(this);

        this.song = MediaPlayer.create(getActivity(),R.raw.canon);

        //this.song.setLooping(true);
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
    public void onResume() {
        super.onResume();
        this.song.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.song.stop();
    }

    @Override
    public void onClick(View v) {
        if(v == this.startGame){
            this.fragmentListener.changePage(1);
        } else if(v == this.highScore){
            this.fragmentListener.changePage(3);
        } else if(v == this.settings){
            this.fragmentListener.changePage(4);
        } else if(v == this.exit){
            this.fragmentListener.closeApplication();
        }
    }
}
