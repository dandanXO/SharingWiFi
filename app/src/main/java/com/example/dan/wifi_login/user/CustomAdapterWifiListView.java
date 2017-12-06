package com.example.dan.wifi_login.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dan.wifi_login.R;

/**
 * Created by DAN on 2017/3/26.
 */

 class CustomAdapterWifiListView extends ArrayAdapter<String> {


    public CustomAdapterWifiListView(Context context,String [] ssid ) {
        super(context, R.layout.wifilistview,ssid);

    }
    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.wifilistview,parent,false);

        //get a reference
        String singlSsidItrm = getItem(position);
        TextView mytextvewSsid =(TextView) customView.findViewById(R.id.Textssid);

        mytextvewSsid.setText(singlSsidItrm);
        return customView;
    }
}
