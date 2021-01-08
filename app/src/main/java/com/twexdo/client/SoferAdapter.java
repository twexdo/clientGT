package com.twexdo.client;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SoferAdapter extends ArrayAdapter<Sofer> {
    ArrayList<Sofer> s;
    Sofer sof;
    public static ImageView imageView;
    public SoferAdapter(Context context, ArrayList<Sofer> soferi) {
        super(context, 0, soferi);
        s = soferi;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Sofer sofer = getItem(position);
        sof = sofer;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
        }
        TextView name = convertView.findViewById(R.id.row_name);
        TextView nrtel = convertView.findViewById(R.id.row_nrtel);
        TextView status = convertView.findViewById(R.id.row_status);
        TextView lastSignal = convertView.findViewById(R.id.row_lastSignal);


        lastSignal.setText(sofer.getLastSignal());
        name.setText(sofer.getNume());
        nrtel.setText(sofer.getTelefon());
        imageView = convertView.findViewById(R.id.image);
        LinearLayout press = convertView.findViewById(R.id.presss);


        Picasso.get().load(sof.getURL()).resize(200,200).into(imageView);


        String status_text = "";
        switch (sofer.getStatus()) {
            case 0:
                status_text = "INDISPONIBIL";

                convertView.setBackgroundResource(R.drawable.indisponibil);
                break;
            case 1:
                status_text = "LIBER";
                convertView.setBackgroundResource(R.drawable.liber);
                break;
            case 2:
                status_text = "OCUPAT";
                convertView.setBackgroundResource(R.drawable.ocupat);
                break;
            case 3:
                status_text = "DUBLU OCUPAT";
                convertView.setBackgroundResource(R.drawable.dubluocupat);
                break;
        }

        status.setText(status_text);



        return convertView;
    }

    public ArrayList getAL(){
        return s;
    }

    @Nullable
    @Override
    public Sofer getItem(int position) {
        return super.getItem(position);
    }

    public void removeItem(int index) {
        s.remove(index);
        this.notifyDataSetChanged();
        Log.e("removeItem()", "Should remove" + s.get(index).getNume());
    }


}
