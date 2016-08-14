package com.example.hasibuzzaman.locationtest;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hasibuzzaman on 8/14/2016.
 */
public class FetchAddressIntentService extends IntentService {


    private static final String TAG = "Tag";
    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }


    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        Log.e("deliverResultToReceiver", "deliverResultToReceiver ");
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";
        Log.e("On handeled intent","On handeled intent");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location location = intent.getParcelableExtra(Constants.MY_LOCATION);
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", e + "");
        }
        if (addresses != null ) {
           /* if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }*/
            /*if(addresses != null )
            {*/
                Address address = addresses.get(1);
                ArrayList<String> addressFragments= new ArrayList<>();
                /*for(int i=0; i<address.getMaxAddressLineIndex();i++)
                {
                    addressFragments.add(address.getAddressLine(i));
                    Log.e("Testing ",address.getAddressLine(i));
                }*/

            addressFragments.add(address.getFeatureName());
            addressFragments.add(address.getSubLocality());
            addressFragments.add(address.getSubAdminArea());
            addressFragments.add(address.getAdminArea());
            addressFragments.add(address.getLocality());



            /*addressFragments.add(address.getCountryName());
               addressFragments.add(address.getLocality());
               addressFragments.add(address.getPostalCode());
*/

            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                        TextUtils.join(System.getProperty("line.separator"),addressFragments));


           // }


        }
    }
}
