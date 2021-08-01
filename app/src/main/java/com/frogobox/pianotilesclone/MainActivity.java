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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @NonNull
    @Override
    public ActivityMainBinding setupViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void setupUI(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        binding.btnPlay.setOnClickListener(v -> {
            Intent i = new Intent(this, GameActivity.class);
            startActivity(i);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maine_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_setting) {
            startActivity(new Intent(this, MyPreferences.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void initScoreBoards() {
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> initSet = new HashSet<>();
        initSet.add("init" + "\n" + "-1");

        if (sharedPreferences.getStringSet("Easy", null) == null) {
            editor.putStringSet("Easy", initSet);
        } else if (sharedPreferences.getStringSet("Medium", null) == null) {
            editor.putStringSet("Medium", initSet);
        } else if (sharedPreferences.getStringSet("Hard", null) == null) {
            editor.putStringSet("Hard", initSet);
        }
        editor.apply();
    }

    public void showScore(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", 0);
        if ((sharedPreferences.getStringSet("Easy", null) == null) ||
                (sharedPreferences.getStringSet("Medium", null) == null) ||
                (sharedPreferences.getStringSet("Hard", null) == null)) {
            initScoreBoards();
        }

        Set<String> s1 = sharedPreferences.getStringSet("Easy", null);
        Set<String> s2 = sharedPreferences.getStringSet("Medium", null);
        Set<String> s3 = sharedPreferences.getStringSet("Hard", null);

        int n1 = s1.size();
        int n2 = s2.size();
        int n3 = s3.size();

        String[] arr1 = new String[n1];
        String[] arr2 = new String[n2];
        String[] arr3 = new String[n3];

        arr1 = s1.toArray(arr1);
        arr2 = s2.toArray(arr2);
        arr3 = s3.toArray(arr3);

        Arrays.sort(arr1, (one, two) -> Integer.parseInt(two.split("\n")[1]) - Integer.parseInt(one.split("\n")[1]));
        Arrays.sort(arr2, (one, two) -> Integer.parseInt(two.split("\n")[1]) - Integer.parseInt(one.split("\n")[1]));
        Arrays.sort(arr3, (one, two) -> Integer.parseInt(two.split("\n")[1]) - Integer.parseInt(one.split("\n")[1]));

        if (n1 > 5)
            arr1 = Arrays.copyOfRange(arr1, 0, 5);
        if (n2 > 5)
            arr2 = Arrays.copyOfRange(arr2, 0, 5);
        if (n3 > 5)
            arr3 = Arrays.copyOfRange(arr3, 0, 5);

        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("easy", arr1);
        intent.putExtra("medium", arr2);
        intent.putExtra("hard", arr3);
        startActivity(intent);

    }

}