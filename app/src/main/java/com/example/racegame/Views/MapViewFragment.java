package com.example.racegame.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.racegame.R;
import com.example.racegame.ScoreRecord;
import com.example.racegame.Utils.GameSharedPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapViewFragment extends Fragment implements OnMapReadyCallback{
    private GoogleMap googleMap;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        List<ScoreRecord> scores = GameSharedPreferences.getInstance().getList();
        this.googleMap = googleMap;
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getView().getContext(), R.raw.style_json));
        LatLng centerMapLocation = new LatLng(0,0);
        setCameraPosition(0,centerMapLocation);
        for (ScoreRecord score: scores){
            String name = score.getName();
            double[] location = score.getLocation();
            double longitude = location[0];
            double latitude = location[1];
            LatLng myLocation = new LatLng(latitude,longitude);
            addMarker(myLocation, name);
        }
    }


    public void showOnMap(ScoreRecord scoreRecord) {
        googleMap.clear();
        String name = scoreRecord.getName();
        double[] location = scoreRecord.getLocation();
        double longitude = location[0];
        double latitude = location[1];
        LatLng myLocation = new LatLng(latitude,longitude);
        addMarker(myLocation, name);
        setCameraPosition(10,myLocation);
    }

    private void addMarker(LatLng myLocation, String name) {
        googleMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .title(name));
    }
    private void setCameraPosition(int zoom, LatLng myLocation) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation)
                .zoom(zoom)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}


