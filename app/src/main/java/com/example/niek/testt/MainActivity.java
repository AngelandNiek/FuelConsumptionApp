package com.example.niek.testt;

import java.io.IOException;
import java.util.List;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String FUELCONS = "Fuel Cons";
    private String LAST = "last city";

    private TextView inputCoordinatesFrom, inputCoordinatesTo;
    private EditText inputFromCity, inputToCity;
    TextView costTravelling, resultConsumption;
    EditText distDriven, fuelUsed, priceLiter;
    Button btnShowCoFrom, btnShowCoTo, btnGetCosts;

    //to save the city
    private SharedPreferences prefs;

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputCoordinatesFrom = (TextView) findViewById(R.id.CoordinatesFrom);
        inputCoordinatesTo = (TextView) findViewById(R.id.CoordinatesTo);
        inputFromCity = (EditText) findViewById(R.id.FromText);
        inputToCity = (EditText) findViewById(R.id.ToText);

        btnShowCoFrom = (Button) findViewById(R.id.GetLocFrom);
        btnShowCoFrom.setOnClickListener(this);
        btnShowCoTo = (Button) findViewById(R.id.GetLocTo);
        btnShowCoTo.setOnClickListener(this);

        distDriven = (EditText) findViewById(R.id.distanceInput);
        fuelUsed = (EditText) findViewById(R.id.fuelInput);
        priceLiter = (EditText) findViewById(R.id.priceInput);

        costTravelling = (TextView) findViewById(R.id.costTravelling);
        resultConsumption = (TextView) findViewById(R.id.resConsLabel);

        btnGetCosts = (Button) findViewById(R.id.getCosts);
        btnGetCosts.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //load Settings and Saved Data

        //load the last city from your shared preferences (xml file in app)
        prefs = getSharedPreferences(FUELCONS, 0);
        inputFromCity.setText(prefs.getString(LAST, " "));
    }

    @Override
    public void onClick(View v) {

        gps = new GPSTracker(MainActivity.this);

        //first get coordinates from GPSTracker
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            //then translate the coordinates into an address
            Geocoder geocoder = new Geocoder(MainActivity.this);

            try {
                Address address = null;
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null) {
                    address = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder();

                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(address.getAddressLine(i)).append("\n");
                    }
                    switch (v.getId()) {
                        case R.id.GetLocFrom:
                            inputFromCity.setText(address.getAddressLine(1).substring(5));
                            inputCoordinatesFrom.setText("long = " + String.valueOf(gps.getLongitude())
                                    + ", lat = " + String.valueOf(gps.getLatitude()));
                            break;

                        case R.id.GetLocTo:
                            inputToCity.setText(address.getAddressLine(1).substring(5));
                            inputCoordinatesTo.setText("long = " + String.valueOf(gps.getLongitude())
                                    + ", lat = " + String.valueOf(gps.getLatitude()));
                            break;

                        case R.id.getCosts:
                            getCosts();
                            break;

                        default:
                            break;
                    }
                } else {
                    inputFromCity.setText("No Address returned!");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                inputFromCity.setText("Canont get Address!");
            }

        } else {
            gps.showSettingsAlert();

        }

        //To save the data for the next startup
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(LAST, inputFromCity.getText().toString());
        edit.commit();
    }

        public void getCosts(){
       //function for calculating the travel costs

        //read EditText and fill variables with numbers
        float distDriv = Float.parseFloat(distDriven.getText().toString());
        float fuelUs = Float.parseFloat(fuelUsed.getText().toString());
        float priceLi = Float.parseFloat(priceLiter.getText().toString());

            //calculation
            float averCons = distDriv / fuelUs;
            float costTrav = fuelUs * priceLi;

            //output
            resultConsumption.setText(String.valueOf(averCons));
            costTravelling.setText(String.valueOf(costTrav));

        }
    }
