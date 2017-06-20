package ro.fsecure.cros.f_secureaplicatiadeintrarelacors.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ro.fsecure.cros.f_secureaplicatiadeintrarelacors.R;

/**
 * Created by humin on 6/13/2017.
 */

public class SpinnerAdapter extends ArrayAdapter {
    String[] objects;
    Activity context;

    public SpinnerAdapter(Context context, Activity activity, int textViewResourceId, String[] objects) {
        super(context, textViewResourceId, objects);
        this.context = activity;
        this.objects = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.simple_spinner_view, parent, false);
        TextView label = (TextView) row.findViewById(R.id.text1);
        label.setText(objects[position]);
        return row;
    }
}
