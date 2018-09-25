package com.vritti.vrittiaccounting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin-3 on 10/8/2016.
 */

public class CashBalanceListAdapter extends BaseAdapter {
    private ArrayList<CashBalanceBean> arrayList;

    private Context parent;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;

    public CashBalanceListAdapter(Context context, ArrayList<CashBalanceBean> list ) {
        parent = context;
        arrayList = list;
        mInflater = LayoutInflater.from(parent);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
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
            convertView = mInflater.inflate(R.layout.custom_cashitem, null);
            holder = new ViewHolder();
            holder.textViewL_Date = (TextView) convertView.findViewById(R.id.textViewL_Date);
            holder.textViewL_Amount1 = (TextView) convertView.findViewById(R.id.textViewL_Amount1);
            holder.textViewL_Amount2 = (TextView) convertView.findViewById(R.id.textViewL_Amount2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewL_Amount1.setText(getcurrencyformate(arrayList.get(position).getAmount1()));
        holder.textViewL_Amount2.setText(getcurrencyformate(arrayList.get(position).getAmount2()));
        holder.textViewL_Date.setText(getDateinFormat(arrayList.get(position).getDate()));
        if (arrayList.get(position).getAmount1().contains("-")) {
            holder.textViewL_Amount1.setTextColor(parent.getResources().getColor(R.color.colorNeg));
        }
        if (arrayList.get(position).getAmount2().contains("-")) {
            holder.textViewL_Amount2.setTextColor(parent.getResources().getColor(R.color.colorNeg));
        }


        return convertView;
    }

    private String getcurrencyformate(String balance) {
        String parts[] = balance.split("\\.");
        int i  = Integer.parseInt(parts[0]);
        DecimalFormat formatter = new DecimalFormat("##,##,##,###");
        String yourFormattedString = formatter.format(i);
        return yourFormattedString+"."+parts[1];
    }

    private String getDateinFormat(String amcExpireDt) {
        String result= null;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM,yyyy");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd MMM");
        try {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            //set the given date in one of the instance and current date in another

            Date date = dateFormat1.parse(amcExpireDt);
            cal1.setTime(date);
            cal2.setTime(new Date());
            //now compare the dates using functions
            if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                Date date2 = dateFormat1.parse(amcExpireDt);
                result = dateFormat3.format(date2);
            }else {
                Date date2 = dateFormat1.parse(amcExpireDt);
                result = dateFormat2.format(date2);
            }
        }catch( Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private String getDateinFormat2(String amcExpireDt) {
        String result= null;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM,yyyy");
        try {
            Date date2 = dateFormat1.parse(amcExpireDt);
            result = dateFormat2.format(date2);
        }catch( Exception e){
            e.printStackTrace();
        }
        return result;
    }





    private static class ViewHolder {
        TextView textViewL_Amount1,textViewL_Date, textViewL_Amount2;
    }


}

