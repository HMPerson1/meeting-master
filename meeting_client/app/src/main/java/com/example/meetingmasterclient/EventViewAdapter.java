package com.example.meetingmasterclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.util.List;

import retrofit2.Call;

// TODO: Set up adapter to store entire event object
public class EventViewAdapter extends BaseAdapter {
    Context context;
    List<MeetingService.EventData> eventInfo;

    public EventViewAdapter(Context context, List<MeetingService.EventData> eventInfo) {
        this.context = context;
        this.eventInfo = eventInfo;
    }

    @Override
    public int getCount() {
        return eventInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return eventInfo.get(position);
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
        Call<MeetingService.UserProfile> c = Server.getService().getUser("/users/" + eventInfo.get(position).event_admin + "/");
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        from.setText(response.body().username);
                    } else {
                        // TODO: Parse error
                        //Server.parseUnsuccessful(response, MeetingService.EventDetailsError.class(), System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));

        TextView name = (TextView)infl.findViewById(R.id.event_name);
        name.setText(eventInfo.get(position).event_name);

        TextView date = (TextView)infl.findViewById(R.id.event_date);
        date.setText(eventInfo.get(position).event_date);

        TextView place = (TextView)infl.findViewById(R.id.event_place);
        // TODO: Make query to obtain location details
        place.setText(eventInfo.get(position).event_location);

        return infl;
    }
}
