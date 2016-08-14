package com.example.hasibuzzaman.locationtest;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Hasibuzzaman on 8/14/2016.
 */
public class LocationFragment extends Fragment {
    TextView lattitudeTv;
    TextView longitudeTv,AddressTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1,container,false);
        lattitudeTv= (TextView) view.findViewById(R.id.lattitudeTv);
        longitudeTv= (TextView) view.findViewById(R.id.longitudeTv);
        AddressTV= (TextView) view.findViewById(R.id.AddressTV);
      return view;
    }
     public void post(Location location)
     {


     }
}
