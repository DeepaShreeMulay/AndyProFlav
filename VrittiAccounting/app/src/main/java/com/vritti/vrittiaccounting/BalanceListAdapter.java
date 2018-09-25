package com.vritti.vrittiaccounting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vritti.vrittiaccounting.services.BalanceDetailsBean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Admin-3 on 10/8/2016.
 */

public class BalanceListAdapter extends BaseAdapter {
    private ArrayList<BalanceDetailsBean> arrayList;

    private Context parent;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;

    public BalanceListAdapter(Context context,
                              ArrayList<BalanceDetailsBean> list
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
            holder.llshopno = (LinearLayout) convertView.findViewById(R.id.llshopno);
            holder.tvshopno = (TextView) convertView.findViewById(R.id.tvshopno);
            holder.tvnamemar = (TextView) convertView.findViewById(R.id.tvnamemar);
            holder.tvexpirydate = (TextView) convertView.findViewById(R.id.tvexpirydate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int sr = position+1;
        String category =arrayList.get(position).getAc_type();

        holder.tvshopno.setText(getfirstchar(arrayList.get(position).getName()));
        holder.tvnamemar.setText(arrayList.get(position).getName());
        String amttype = getcreditordebit(arrayList.get(position).getBal_cd());
        if (category.equals("Supp")){
            if (amttype.equals("Debit")) {
                holder.tvexpirydate.setText("- \u20B9 " + getcurrencyformate(arrayList.get(position).getBalance()));
                holder.tvexpirydate.setTextColor(parent.getResources().getColor(R.color.colorCredit));
                holder.tvnamemar.setTextColor(parent.getResources().getColor(R.color.colorCredit));
                holder.llshopno.setBackground(parent.getResources().getDrawable(R.drawable.edt_border_red));

            } else if (amttype.equals("Credit")) {
                holder.tvexpirydate.setText("\u20B9 " + getcurrencyformate(arrayList.get(position).getBalance()));
                holder.tvexpirydate.setTextColor(parent.getResources().getColor(R.color.colorDebit1));
                holder.tvnamemar.setTextColor(parent.getResources().getColor(R.color.colorDebit1));
                holder.llshopno.setBackground(parent.getResources().getDrawable(R.drawable.edt_border_orange));
            }
        }else {
            if (amttype.equals("Credit")) {
                holder.tvexpirydate.setText("- \u20B9 " + getcurrencyformate(arrayList.get(position).getBalance()));
                holder.tvexpirydate.setTextColor(parent.getResources().getColor(R.color.colorCredit));
                holder.tvnamemar.setTextColor(parent.getResources().getColor(R.color.colorCredit));
                holder.llshopno.setBackground(parent.getResources().getDrawable(R.drawable.edt_border_red));
            } else if (amttype.equals("Debit")) {
                holder.tvexpirydate.setText("\u20B9 " + getcurrencyformate(arrayList.get(position).getBalance()));
                holder.tvexpirydate.setTextColor(parent.getResources().getColor(R.color.colorDebit1));
                holder.tvnamemar.setTextColor(parent.getResources().getColor(R.color.colorDebit1));
                holder.llshopno.setBackground(parent.getResources().getDrawable(R.drawable.edt_border_orange));
            }
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
        TextView tvnamemar,tvshopno, tvexpirydate;
        LinearLayout llshopno;
    }


}

