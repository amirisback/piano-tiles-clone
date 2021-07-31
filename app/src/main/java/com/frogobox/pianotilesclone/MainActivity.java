package com.frogobox.pianotilesclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.frogobox.pianotilesclone.databinding.ActivityMainBinding;
import com.frogobox.pianotilesclone.BaseActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private boolean isLogged = false;

    @NonNull
    @Override
    public ActivityMainBinding setupViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void setupUI(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        System.out.println("da,merge");
    }


    private void initScoreBoards() {
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> initSet = new HashSet<>();
        initSet.add("init" + "\n" + "-1");

        if (sharedPreferences.getStringSet("Easy", null) == null) {
            editor.putStringSet("Easy", initSet);
        }
        if (sharedPreferences.getStringSet("Medium", null) == null) {
            editor.putStringSet("Medium", initSet);
        }
        if (sharedPreferences.getStringSet("Hard", null) == null) {
            editor.putStringSet("Hard", initSet);
        }
        editor.apply();
    }

    private void updateLogStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogged", isLogged);

        if (!isLogged)
            editor.putString("pic", "");
        editor.apply();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maine_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Settings) {
            startActivity(new Intent(this, MyPreferences.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void startGame(View view) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    public void showScore(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", 0);
        if ((sharedPreferences.getStringSet("Easy", null) == null) ||
                (sharedPreferences.getStringSet("Medium", null) == null) ||
                sharedPreferences.getStringSet("Hard", null) == null)
            initScoreBoards();

        Set<String> s1 = sharedPreferences.getStringSet("Easy", null);
        int n = s1.size();
        String[] arr1 = new String[n];
        arr1 = s1.toArray(arr1);

        Arrays.sort(arr1, (one, two) -> Integer.parseInt(two.split("\n")[1]) - Integer.parseInt(one.split("\n")[1]));

        Set<String> s2 = sharedPreferences.getStringSet("Medium", null);
        int n2 = s2.size();
        String[] arr2 = new String[n2];
        arr2 = s2.toArray(arr2);

        Arrays.sort(arr2, (one, two) -> Integer.parseInt(two.split("\n")[1]) - Integer.parseInt(one.split("\n")[1]));


        Set<String> s3 = sharedPreferences.getStringSet("Hard", null);
        int n3 = s3.size();
        String[] arr3 = new String[n3];
        arr3 = s3.toArray(arr3);

        Arrays.sort(arr3, (one, two) -> Integer.parseInt(two.split("\n")[1]) - Integer.parseInt(one.split("\n")[1]));

        if (n > 5)
            arr1 = Arrays.copyOfRange(arr1, 0, 5);
        if (n2 > 5)
            arr2 = Arrays.copyOfRange(arr2, 0, 5);
        if (n3 > 5)
            arr3 = Arrays.copyOfRange(arr3, 0, 5);

        Intent i = new Intent(this, ScoreActivity.class);
        i.putExtra("easy", arr1);
        i.putExtra("medium", arr2);
        i.putExtra("hard", arr3);
        startActivity(i);

    }

}