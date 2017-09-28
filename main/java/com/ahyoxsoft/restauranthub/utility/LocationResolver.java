package com.ahyoxsoft.restauranthub.utility;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dejiogunnubi on 1/1/16.
 */
public class LocationResolver {
    private Timer timer;
    private LocationManager locationManager;
    private LocationResult locationResult;
    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;
    private Handler locationTimeoutHandler;

    private final Handler.Callback locationTimeoutCallback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            locationTimeoutFunc();
            return true;
        }

        private void locationTimeoutFunc() {
            try {
                locationManager.removeUpdates(locationListenerGps);
                locationManager.removeUpdates(locationListenerNetwork);

                Location networkLocation = null, gpsLocation = null;
                if (gpsEnabled)
                    gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (networkEnabled)
                    networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                // if there are both values use the latest one
                if (gpsLocation != null && networkLocation != null) {
                    if (gpsLocation.getTime() > networkLocation.getTime())
                        locationResult.gotLocation(gpsLocation);
                    else
                        locationResult.gotLocation(networkLocation);
                    return;
                }

                if (gpsLocation != null) {
                    locationResult.gotLocation(gpsLocation);
                    return;
                }
                if (networkLocation != null) {
                    locationResult.gotLocation(networkLocation);
                    return;
                }
                locationResult.gotLocation(null);
            }catch (SecurityException e) {
            }
        }
    };
    private final LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            try {
                timer.cancel();
                locationResult.gotLocation(location);
                locationManager.removeUpdates(this);
                locationManager.removeUpdates(locationListenerNetwork);
            }catch (SecurityException e) {
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            try {
                timer.cancel();
                locationResult.gotLocation(location);
                locationManager.removeUpdates(this);
                locationManager.removeUpdates(locationListenerGps);
            }catch (SecurityException e) {
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void prepare() {
        locationTimeoutHandler = new Handler(locationTimeoutCallback);
    }

    public synchronized boolean getLocation(Context context, LocationResult result, int maxMillisToWait) {
        locationResult = result;
        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // exceptions will be thrown if provider is not permitted.
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        // don't start listeners if no provider is enabled
        if (!gpsEnabled && !networkEnabled)
            return false;

        if (gpsEnabled) {
            try {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListenerGps, Looper.myLooper());
            }catch (SecurityException e){

            }
        }
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if (networkEnabled) {
            try {
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListenerNetwork, Looper.myLooper());
            }catch (SecurityException e) {}
        }
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);

        timer = new Timer();
        timer.schedule(new GetLastLocationTask(), maxMillisToWait);
        return true;
    }

    private class GetLastLocationTask extends TimerTask {
        @Override
        public void run() {
            locationTimeoutHandler.sendEmptyMessage(0);
        }
    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }
}
