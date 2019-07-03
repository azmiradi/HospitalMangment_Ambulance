package bee.read.abanob.prog.com.hospitalmangment;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import static bee.read.abanob.prog.com.hospitalmangment.MapUtils.decodePoly;
import static bee.read.abanob.prog.com.hospitalmangment.MapUtils.getBearing;
import static com.google.android.gms.maps.model.JointType.ROUND;

public class MapsActivityTracking extends FragmentActivity implements OnMapReadyCallback,
        LocationListener {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference locationUpdate;
    protected LocationManager locationManager;
   private static LatLng latLng;
    private static final long DELAY = 4500;
    private static final long ANIMATION_TIME_PER_ROUTE = 3000;
    String polyLine = "q`epCakwfP_@EMvBEv@iSmBq@GeGg@}C]mBS{@KTiDRyCiBS";
    GoogleMap googleMap;
    private PolylineOptions polylineOptions;
    private PolylineOptions blackPolylineOptions;
    private Polyline blackPolyline,grayPolyline;
    private Polyline greyPolyLine;
    private SupportMapFragment mapFragment;
    private Handler handler;
    private Marker carMarker;
    private int index;
    private int next;
    private LatLng startPosition;
    private LatLng endPosition;
    private float v;
     List<LatLng> polyLineList;
    private double lat, lng;
    // banani
    double latitude = 23.7877649;
    double longitude = 90.4007049;
    private String TAG = "HomeActivity";

    private boolean isFirstPosition = true;
    private Double startLatitude;
    private Double startLongitude;
    TextView textView;
   private static String paramedics_id;
    private static boolean is_draw;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_track);
        paramedics_id=getIntent().getExtras().getString("id");
        firebaseDatabase=FirebaseDatabase.getInstance();
        locationUpdate=firebaseDatabase.getReference("location");
        textView=findViewById(R.id.arr);
        getLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions( //Method of Fragment
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    0
            );
        } else {
            //
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        handler = new Handler();

    }

    void staticPolyLine() {

        googleMap.clear();

        polyLineList = decodePoly(polyLine);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polyLineList) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        googleMap.animateCamera(mCameraUpdate);

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLACK);
        polylineOptions.width(5);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = googleMap.addPolyline(polylineOptions);

        startCarAnimation(latitude, longitude);

    }

    Runnable staticCarRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "staticCarRunnable handler called...");
            if (index < (polyLineList.size() - 1)) {
                index++;
                next = index + 1;
            } else {
                index = -1;
                next = 1;
                stopRepeatingTask();
                return;
            }

            if (index < (polyLineList.size() - 1)) {
//                startPosition = polyLineList.get(index);
                startPosition = carMarker.getPosition();
                endPosition = polyLineList.get(next);
            }

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

//                    Log.i(TAG, "Car Animation Started...");

                    v = valueAnimator.getAnimatedFraction();
                    lng = v * endPosition.longitude + (1 - v)
                            * startPosition.longitude;
                    lat = v * endPosition.latitude + (1 - v)
                            * startPosition.latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    carMarker.setPosition(newPos);
                    carMarker.setAnchor(0.5f, 0.5f);
                    carMarker.setRotation(getBearing(startPosition, newPos));
                    googleMap.moveCamera(CameraUpdateFactory
                            .newCameraPosition
                                    (new CameraPosition.Builder()
                                            .target(newPos)
                                            .zoom(16.5f)
                                            .build()));


                }
            });
            valueAnimator.start();
            handler.postDelayed(this, 5000);

        }
    };

    private void startCarAnimation(Double latitude, Double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);

        carMarker = googleMap.addMarker(new MarkerOptions().position(latLng).
                flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));


        index = -1;
        next = 1;
        handler.postDelayed(staticCarRunnable, 3000);
    }

    void stopRepeatingTask() {

        if (staticCarRunnable != null) {
            handler.removeCallbacks(staticCarRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(false);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (permissions[1].equals(Manifest.permission.ACCESS_COARSE_LOCATION )
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED ||
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION )
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                ///
            }
        }
    }
    private void getLocation(){

        locationUpdate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bee.read.abanob.prog.com.hospitalmangment.Model.Location location=
                        new bee.read.abanob.prog.com.hospitalmangment.Model.Location();
                if (dataSnapshot.hasChild(paramedics_id)) {
                    location = dataSnapshot.child(paramedics_id).getValue(bee.read.abanob.prog.com.hospitalmangment.Model.Location.class);
                    try {

                        CreatePolyLineOnly(location);
                        moveCar(location);
                        //  drawMap(Double.parseDouble(RequestBloodFragment.latitude),Double.parseDouble(RequestBloodFragment.longitude),Double.parseDouble(RequestBloodFragment.hospital.getLat()),Double.parseDouble(RequestBloodFragment.hospital.getLng()));
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void moveCar(bee.read.abanob.prog.com.hospitalmangment.Model.Location location) {

        startLatitude = location.getLat();
        startLongitude = location.getLang();
        latLng=new LatLng(startLatitude,startLongitude);
        Log.d(TAG, startLatitude + "--" + startLongitude);

        if (isFirstPosition) {
            startPosition = new LatLng(startLatitude, startLongitude);

            carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).
                    flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
            carMarker.setAnchor(0.5f, 0.5f);

            googleMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition
                            (new CameraPosition.Builder()
                                    .target(startPosition)
                                    .zoom(16.5f)
                                    .build()));

            isFirstPosition = false;

        } else {
            endPosition = new LatLng(startLatitude, startLongitude);

            Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude + "--Check --" + startPosition.longitude + "--" + endPosition.longitude);

            if ((startPosition.latitude != endPosition.latitude) || (startPosition.longitude != endPosition.longitude)) {

                Log.e(TAG, "NOT SAME");
                startBikeAnimation(startPosition, endPosition);

            } else {

                Log.e(TAG, "SAMME");
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

     }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }


    private void startBikeAnimation(final LatLng start, final LatLng end) {

        Log.i(TAG, "startBikeAnimation called...");

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(ANIMATION_TIME_PER_ROUTE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                //LogMe.i(TAG, "Car Animation Started...");
                v = valueAnimator.getAnimatedFraction();
                lng = v * end.longitude + (1 - v)
                        * start.longitude;
                lat = v * end.latitude + (1 - v)
                        * start.latitude;

                LatLng newPos = new LatLng(lat, lng);
                carMarker.setPosition(newPos);
                carMarker.setAnchor(0.5f, 0.5f);
                carMarker.setRotation(getBearing(start, end));

                // todo : Shihab > i can delay here
                googleMap.moveCamera(CameraUpdateFactory
                        .newCameraPosition
                                (new CameraPosition.Builder()
                                        .target(newPos)
                                        .zoom(16.5f)
                                        .build()));

                startPosition = carMarker.getPosition();

            }

        });
        valueAnimator.start();
    }




  private void CreatePolyLineOnly(bee.read.abanob.prog.com.hospitalmangment.Model.Location location) {
     final Hospital hospital=new PrefManager(getApplicationContext()).getHospital();
        LatLng destination =new LatLng(Double.parseDouble(hospital.getLat()),Double.parseDouble(hospital.getLng()));
      String requestUrl=null;
        try {
            requestUrl="https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+location.getLat()+","+location.getLang()+
                    "&"+"destination="+destination.latitude+","+destination.longitude+"&key=AIzaSyAUFR9r___tvcs5ovF_j8S2abYXDJNr63E";
            RequestQueue queue = Volley.newRequestQueue(this);
            Log.d("url",requestUrl);

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray routes=response.getJSONArray("routes");
                                for (int i=0;i<routes.length();i++)
                                {
                                    JSONObject route=routes.getJSONObject(i);
                                    JSONObject poly=route.getJSONObject("overview_polyline");
                                    try {
                                        JSONArray legs = route.getJSONArray("legs");
                                        if (legs.length() > 0) {
                                            for (int j = 0; j < legs.length(); j++) {
                                                JSONObject all = legs.getJSONObject(i);
                                                JSONObject duration = all.getJSONObject("duration");
                                                String time = duration.getString("text");
                                                textView.setText(time+" To Arrive");
                                            }
                                        }
                                    }
                                    catch (JSONException e)
                                    {
                                      System.out.println(e.getMessage()+"++++++++++");
                                    }
                                    String polyline=poly.getString("points");
                                    polyLineList=decodePoly(polyline);
                                }

                                LatLngBounds.Builder builder=new LatLngBounds.Builder();
                                for(LatLng latLng:polyLineList)
                                    builder.include(latLng);
                                LatLngBounds bounds=builder.build();
                                CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngBounds(bounds,100);
                                googleMap.animateCamera(cameraUpdate);

                                polylineOptions=new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                grayPolyline=googleMap.addPolyline(polylineOptions);

                                blackPolylineOptions=new PolylineOptions();
                                blackPolylineOptions.color(Color.BLACK);
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(JointType.ROUND);
                                blackPolyline=googleMap.addPolyline(blackPolylineOptions);
                                googleMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size()-1)).title(hospital.getName()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.getMessage()+"6++++++++");
                        }
                    }
            );

            queue.add(getRequest);
        }
        catch (Exception e)
        {

        }

    }

    private void drawMap(double s_lat,double s_lng,double e_lat,double e_lng) {
        GoogleDirectionConfiguration.getInstance().setLogEnabled(true);
        Log.e("map", "++");

        GoogleDirection.withServerKey("AIzaSyAUFR9r___tvcs5ovF_j8S2abYXDJNr63E")
                .from(new LatLng(s_lat, s_lng))
                 .to(new LatLng(e_lat, e_lng))
                .transportMode(TransportMode.DRIVING)

                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            googleMap.setMinZoomPreference(8f);
                            com.akexorcist.googledirection.model.Route route = direction.getRouteList().get(0);
                            int legCount = route.getLegList().size();

                            for (int index = 0; index < legCount; index++) {
                                Log.e("map", "++++" + index);
                                Leg leg = route.getLegList().get(index);
                                // mMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()));

                                if (index == 0) {
                                    Log.e("position","0" + leg.getStartLocation().getCoordination());
                                    //   mMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()).title("User"));
                                    googleMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                                } else if (index == legCount - 1) {
                                    //   mMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()).title("User"));
                                    googleMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()) );
                                } else {
                                    googleMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()));
                                }
                                List<Step> stepList = leg.getStepList();
                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(MapsActivityTracking.this, stepList, 5, Color.RED, 3, Color.BLUE);
                                for (PolylineOptions polylineOption : polylineOptionList) {
                                    googleMap.addPolyline(polylineOption);
                                }
                            }
                            setCameraWithCoordinationBounds(route); // animateCamera

                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                        Log.e("error", t.getLocalizedMessage() + t.getMessage() + "");
                        // Do something
                    }
                });
    }

    private void setCameraWithCoordinationBounds(com.akexorcist.googledirection.model.Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
}