package com.example.jangerhard.airport;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Region region;

    TextView main;

    ImageView mainImgView, beaconImgView;

    private static final Map<String, List<String>> PLACES_BY_BEACONS;

    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        //Green
        placesByBeacons.put("16246:59757", new ArrayList<String>() {{
            add("Times Square");
            // read as: "Times Square" is closest
            // to the beacon with major 16246 and minor 59757
            add("Statue of Liberty");
            add("Empire State Building");
        }});
        //Green
        placesByBeacons.put("30158:39057", new ArrayList<String>() {{
            add("Statue of Liberty");
            add("Times Square");
            add("Empire State Building");
        }});
        //Purple
        placesByBeacons.put("48706:57437", new ArrayList<String>() {{
            add("Empire State Building");
            add("Times Square");
            add("Statue of Liberty");
        }});
        //Lightblue
        placesByBeacons.put("38973:61396", new ArrayList<String>() {{
            add("Statue of Liberty");
            add("Times Square");
            add("Empire State Building");
        }});
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main = (TextView) findViewById(R.id.listOfNearbyPlaces);

        mainImgView = (ImageView) findViewById(R.id.mainImgView);
        mainImgView.setVisibility(View.INVISIBLE);
        beaconImgView = (ImageView) findViewById(R.id.beaconView);
        beaconImgView.setVisibility(View.INVISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        beaconManager = new BeaconManager(this);
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    List<String> places = placesNearBeacon(nearestBeacon);
                    //main.setText("");

                    displayPhoto(places.get(0));
                    //main.append("Nearest places: \n\n");
                    //for (int i=0; i<places.size(); i++)
                    //    main.append((i+1) + ". " + places.get(i) + "\n");

                } else {
                    main.setText("No nearby beacons..");
                    mainImgView.setVisibility(View.INVISIBLE);
                    beaconImgView.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    private void displayPhoto(String nearestPlace){
        switch (nearestPlace) {

            case "Times Square":
                mainImgView.setImageResource(R.drawable.timesquare);
                beaconImgView.setImageResource(R.drawable.green_beacon);
                break;
            case "Statue of Liberty":
                mainImgView.setImageResource(R.drawable.statueofliberty);
                beaconImgView.setImageResource(R.drawable.ice_beacon);
                break;
            case "Empire State Building":
                mainImgView.setImageResource(R.drawable.empirestate);
                beaconImgView.setImageResource(R.drawable.purple_beacon);
                break;
            default:
                break;
        }
        main.setText("Nearest place: \n" + nearestPlace);
        mainImgView.setVisibility(View.VISIBLE);
        beaconImgView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }
}
