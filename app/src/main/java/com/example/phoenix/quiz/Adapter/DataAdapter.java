package com.example.phoenix.quiz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.phoenix.quiz.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DataAdapter extends BaseAdapter {

    Context mContext;
    private ArrayList<HashMap<String, String>> mapArrayList;
    private LayoutInflater mInflater;

    private static final String TAG_QUESTION = "question";
    private static final String TAG_CHOICE = "choice";
    private static final String TAG_ISRIGHT = "is_right";

    public DataAdapter(Context c, ArrayList<HashMap<String,String>> list) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
        mapArrayList = list;
    }

    @Override
    public int getCount() {
        return mapArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_result_details, parent, false);

            holder = new ViewHolder();
            holder.txtQuestion = convertView.findViewById(R.id.viewQuestion);
            holder.txtChoice = convertView.findViewById(R.id.viewChoice);
            holder.txtIsright = convertView.findViewById(R.id.viewIsright);

            if (position == 0) {
                convertView.setTag(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtQuestion.setText(mapArrayList.get(position).get(TAG_QUESTION));
        holder.txtChoice.setText(mapArrayList.get(position).get(TAG_CHOICE));
        holder.txtIsright.setText(mapArrayList.get(position).get(TAG_ISRIGHT));

        return convertView;
    }

    static class ViewHolder {

        TextView txtQuestion;
        TextView txtChoice;
        TextView txtIsright;
    }
}
