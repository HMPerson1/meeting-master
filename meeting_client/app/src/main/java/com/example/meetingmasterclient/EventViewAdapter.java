package com.example.meetingmasterclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// TODO: Set up adapter to store entire event object
public class EventViewAdapter extends BaseAdapter {
    Context context;
    String[][] eventInfo;

    public EventViewAdapter(Context context, String[][] eventInfo) {
        this.context = context;
        this.eventInfo = eventInfo;
    }

    @Override
    public int getCount() {
        return eventInfo.length;
    }

    @Override
    public Object getItem(int position) {
        return eventInfo[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View infl = inflater.inflate(R.layout.event_view_item, null);

        TextView from = (TextView)infl.findViewById(R.id.from);
        from.setText(eventInfo[position][0]);

        TextView name = (TextView)infl.findViewById(R.id.event_name);
        name.setText(eventInfo[position][1]);

        TextView date = (TextView)infl.findViewById(R.id.event_date);
        date.setText(eventInfo[position][2]);

        TextView place = (TextView)infl.findViewById(R.id.event_place);
        place.setText(eventInfo[position][3]);

        return infl;
    }
}
