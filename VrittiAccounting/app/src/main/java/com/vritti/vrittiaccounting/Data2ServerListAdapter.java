package com.vritti.vrittiaccounting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vritti.vrittiaccounting.services.ItemDetailsBean;

import java.util.ArrayList;

/**
 * Created by Admin-3 on 10/8/2016.
 */

public class Data2ServerListAdapter extends BaseAdapter {
    private ArrayList<ItemDetailsBean> arrayList;

    private Context parent;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;

    public Data2ServerListAdapter(Context context,
                                  ArrayList<ItemDetailsBean> list
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
            holder.tvexpirydate.setVisibility(View.GONE);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int sr = position+1;

        holder.tvshopno.setText(getfirstchar(arrayList.get(position).getItem_desc()));
        holder.tvnamemar.setText(arrayList.get(position).getItem_desc());
        //holder.tvexpirydate.setText(" Rs."+arrayList.get(position).getRatefactor());

        return convertView;
    }

    private String getfirstchar(String name) {
        return String.valueOf(name.charAt(0));
    }
    private static class ViewHolder {
        TextView tvnamemar,tvshopno, tvexpirydate;
    }


}

