package com.example.meetingmasterclient;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;


class ETA{
    String attendee;
    String eta;

    public ETA(String attendee, String eta) {
        this.attendee = attendee;
        this.eta = eta;
    }

    public String getAttendee() {
        return attendee;
    }

    public String getEta() {
        return eta;
    }
}
public class ETAAdapter extends ArrayAdapter<ETA> {
    private ArrayList<ETA> etas;
    private int viewResourceID;
    private LayoutInflater inflater;
    public ETAAdapter(Context context, int textViewResourceId, ArrayList<ETA> eta){
        super(context, textViewResourceId, eta);
        etas=eta;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewResourceID = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parents){
        convertView= inflater.inflate(viewResourceID,null);
        ETA eta = etas.get(position);
        TextView attendee = (TextView) convertView.findViewById(R.id.attendee);
        TextView etaTextView = (TextView) convertView.findViewById(R.id.eta);

        attendee.setText(eta.getAttendee());
        etaTextView.setText(eta.getEta());

        return convertView;
    }
}
