package com.example.b2bouz;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class KonumServisi implements LocationListener {

    private final Context mContext;
    private Location mLocation;
    private LocationManager mLocationManager;

    public KonumServisi(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public LatLng getKonum() {
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            Log.e("KonumServisi", "Konum izni reddedildi: " + e.getMessage());
        }

        if (mLocation != null) {
            return new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        } else {
            return null;
        }
    }

    public void stopUsingGPS() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(KonumServisi.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("KonumServisi", "Konum servisi devre dışı bırakıldı.");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("KonumServisi", "Konum servisi etkinleştirildi.");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}

