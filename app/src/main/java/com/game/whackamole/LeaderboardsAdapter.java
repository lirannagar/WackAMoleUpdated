package com.game.whackamole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeaderboardsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public LeaderboardsAdapter(Context context, String[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.item_name_tv);
        TextView positionView = (TextView) rowView.findViewById(R.id.intem_place_tv);
        positionView.setText("#"+(position+1));
        textView.setText(values[position]);

        return rowView;
    }
}
