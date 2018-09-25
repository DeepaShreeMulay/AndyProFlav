package com.vritti.vrittiaccounting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin-3 on 10/8/2016.
 */

public class FirmListAdapter extends BaseAdapter {
    private ArrayList<FirmMaster> arrayList;

    private Context parent;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;

    public FirmListAdapter(Context context,
                            ArrayList<FirmMaster> list
    ) {
        parent = context;
        arrayList = list;
        mInflater = LayoutInflater.from(parent);

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final int pos = position;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_firmlistitem, null);
            holder = new ViewHolder();

            holder.tvshopno = (TextView) convertView.findViewById(R.id.tvshopno);
            holder.tvnamemar = (TextView) convertView.findViewById(R.id.tvnamemar);
            holder.tvexpirydate = (TextView) convertView.findViewById(R.id.tvexpirydate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvshopno.setText(arrayList.get(position).getShop_no());
        holder.tvnamemar.setText(arrayList.get(position).getName_mar());
        holder.tvexpirydate.setText("Contract ends on : "+getDateinFormat(arrayList.get(position).getAMCExpireDt()));

        return convertView;
    }

    private String getDateinFormat(String amcExpireDt) {
        String result= null;
        //SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM, yyyy");
        try {
            Date date2 = dateFormat1.parse(amcExpireDt);
            result = dateFormat2.format(date2);
        }catch( Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private static class ViewHolder {
        TextView tvnamemar,tvshopno, tvexpirydate;
    }


}

