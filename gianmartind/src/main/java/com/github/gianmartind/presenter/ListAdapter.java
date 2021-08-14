package com.github.gianmartind.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.gianmartind.R;
import com.github.gianmartind.model.Score;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {
    private List<Score> listItems;
    private Context fragment;


    public ListAdapter(Context fragment) {
        this.fragment = fragment;
        this.listItems = new ArrayList<Score>();
    }

    public void addLine(Score newItem) {
        this.listItems.add(newItem);
        this.notifyDataSetChanged();
    }

    public void updateList(List<Score> list) {
        this.listItems = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return this.listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(this.fragment).inflate(R.layout.item_list_fragment_score, viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view, i);;
        Score current = (Score) this.getItem(i);
        viewHolder.updateView(current);
        view.setTag(viewHolder);

        return view;
    }

    public class ViewHolder {
        protected TextView noUrut;
        protected TextView tvName;
        protected TextView tvScore;
        protected TextView tvDate;
        protected int i;
        public ViewHolder(View view, int i) {
            this.noUrut = view.findViewById(R.id.no_urut);
            this.tvName = view.findViewById(R.id.isi_nama);
            this.tvScore = view.findViewById(R.id.isi_score);
            this.tvDate = view.findViewById(R.id.isi_waktu);
            this.i = i;
        }

        public void updateView(final Score score) {
            this.noUrut.setText(Integer.toString(this.i + 1));
            if(this.i == 0){
                this.noUrut.setTextSize(38);
                this.tvScore.setTextSize(33);
            } else if(this.i == 1){
                this.noUrut.setTextSize(32);
                this.tvScore.setTextSize(27);
            } else if(this.i == 2){
                this.noUrut.setTextSize(26);
                this.tvScore.setTextSize(21);
            }
            this.tvName.setText(score.getName());
            this.tvScore.setText("Score: " + Integer.toString(score.getScore()));
            this.tvDate.setText(score.getDatetime());
        }
    }
}
