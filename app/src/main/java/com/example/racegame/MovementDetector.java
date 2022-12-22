package com.example.racegame;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MovementDetector {
    private int position = 3;
    private MovementCallback mc;

    private long timestamp=0;
    private SensorManager sm;
    private Sensor sensor;
    private SensorEventListener sel;

    public MovementDetector(Context context, MovementCallback movementCallback) {
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mc = movementCallback;
        initEventListener();
    }

    public int getPosition() {
        return position;
    }


    private void initEventListener() {
        sel = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                if (System.currentTimeMillis() - timestamp > 500) {
                    timestamp = System.currentTimeMillis();
                    if (x > 3.0)
                        mc.moveLeftSensor();
                    if (x < -3.0)
                        mc.moveRightSensor();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }
    public void start(){
        sm.registerListener(sel,sensor,SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop(){
        sm.unregisterListener(sel,sensor);
    }

}
