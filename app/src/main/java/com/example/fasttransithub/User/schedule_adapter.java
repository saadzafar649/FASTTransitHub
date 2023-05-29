package com.example.fasttransithub.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.fasttransithub.R;

import java.util.ArrayList;

public class schedule_adapter extends BaseAdapter {

    private Context context;
    private ArrayList<User_schedule_frag > User_scheduleArrayList;

    public schedule_adapter(Context context, ArrayList<User_schedule_frag > User_scheduleArrayList) {

        this.context = context;
        this.User_scheduleArrayList = User_scheduleArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return User_scheduleArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return User_scheduleArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.schedule_item, null, true);

            holder.stop = (TextView) convertView.findViewById(R.id.user_stop);
            holder.time = (TextView) convertView.findViewById(R.id.stop_time);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.stop.setText(User_scheduleArrayList.get(position).getstop());
        holder.time.setText(String.valueOf(User_scheduleArrayList.get(position).getstop()));

        return convertView;
    }

    private class ViewHolder {

        protected TextView stop, time;

    }

}
