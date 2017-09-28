package com.ahyoxsoft.restauranthub.utility;

import android.content.Context;
import android.location.Location;
import android.os.Looper;

/**
 * Created by dejiogunnubi on 1/1/16.
 */
public class LocationGetter {
    private final Context context;
    private Location location = null;
    private final Object gotLocationLock = new Object();
    private final LocationResolver.LocationResult locationResult = new LocationResolver.LocationResult() {
        @Override
        public void gotLocation(Location location) {
            synchronized (gotLocationLock) {
                LocationGetter.this.location = location;
                gotLocationLock.notifyAll();
                Looper.myLooper().quit();
            }
        }
    };

    public LocationGetter(Context context) {
        if (context == null)
            throw new IllegalArgumentException("context == null");

        this.context = context;
    }

    public synchronized Coordinates getLocation(int maxWaitingTime, int updateTimeout) {
        Coordinates coordinates;
        try {
            final int updateTimeoutPar = updateTimeout;
            synchronized (gotLocationLock) {
                new Thread() {
                    public void run() {
                        Looper.prepare();
                        LocationResolver locationResolver = new LocationResolver();
                        locationResolver.prepare();
                        locationResolver.getLocation(context, locationResult, updateTimeoutPar);
                        Looper.loop();
                    }
                }.start();

                gotLocationLock.wait(maxWaitingTime);
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        if (location != null)
            coordinates = new Coordinates(location.getLatitude(), location.getLongitude());
        else
            //coordinates = Coordinates.UNDEFINED;
            coordinates = new Coordinates(0.0, 0.0);
        return coordinates;
    }

    public class Coordinates {
        private double longitude;
        private double latitude;

        public Coordinates(double lat, double lgt) {
            setLatitude(lat);
            setLongitude(lgt);
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }
}
