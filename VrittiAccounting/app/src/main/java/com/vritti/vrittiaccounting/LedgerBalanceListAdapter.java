package com.vritti.vrittiaccounting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vritti.vrittiaccounting.services.BalanceDetailsBean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin-3 on 10/8/2016.
 */

public class LedgerBalanceListAdapter extends BaseAdapter {
    private ArrayList<LedgerBalanceBean> arrayList;

    private Context parent;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;

    public LedgerBalanceListAdapter(Context context, ArrayList<LedgerBalanceBean> list ) {
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
            convertView = mInflater.inflate(R.layout.custom_ledgeritem, null);
            holder = new ViewHolder();

            holder.textViewL_Amount = (TextView) convertView.findViewById(R.id.textViewL_Amount);
            holder.textViewL_Date = (TextView) convertView.findViewById(R.id.textViewL_Date);
            holder.textViewL_Description = (TextView) convertView.findViewById(R.id.textViewL_Description);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewL_Amount.setText(getcurrencyformate(arrayList.get(position).getAmount()));
        holder.textViewL_Date.setText(getDateinFormat(arrayList.get(position).getDate()));
        holder.textViewL_Description.setText(arrayList.get(position).getNarr());

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

    private String getfirstchar(String name) {
        return String.valueOf(name.charAt(0));
    }

    private String getcreditordebit(String bal_cd) {
        if(bal_cd.equals("Cr")){
            return  "Credit";
        }else if(bal_cd.equals("Dr")){
            return  "Debit";
        }
        return null;
    }



    private static class ViewHolder {
        TextView textViewL_Amount,textViewL_Date, textViewL_Description;
    }


}

