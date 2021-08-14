package com.github.gianmartind.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.gianmartind.R;
import com.github.gianmartind.DBHandler;
import com.github.gianmartind.model.Score;
import com.github.gianmartind.presenter.HighScoreFragmentPresenter;
import com.github.gianmartind.presenter.ListAdapter;

import java.util.List;

public class HighScoreFragment extends Fragment implements HighScoreFragmentPresenter.IHighScoreFragment{
    HighScoreFragmentPresenter highScoreFragmentPresenter;
    ListAdapter adapter;
    ListView listView;
    DBHandler db;

    public HighScoreFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);

        this.db = new DBHandler(this.getActivity());
        this.adapter = new ListAdapter(this.getActivity());
        this.listView = view.findViewById(R.id.lst_fragment_score);
        this.listView.setAdapter(this.adapter);
        this.highScoreFragmentPresenter = new HighScoreFragmentPresenter(this, this.db);

        this.highScoreFragmentPresenter.loadData(10);

        return view;
    }

    @Override
    public void updateList(List<Score> list) {
        this.adapter.updateList(list);
    }
}
