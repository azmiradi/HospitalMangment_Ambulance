package bee.read.abanob.prog.com.hospitalmangment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static bee.read.abanob.prog.com.hospitalmangment.MapsActivity.latLng;

public class RegistrationActivity extends AppCompatActivity implements OnMapReadyCallback {
    EditText  name;
      ImageView imageView;
    private ChasingDots mChasingDotsDrawable;
     private GoogleMap mMap;
    SupportMapFragment mapFragment;
      View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initialization();
    }
    private void initialization() {
       name=findViewById(R.id.name);

        //ImageView
        imageView = (ImageView)  findViewById(R.id.image);
        imageView.setVisibility(View.GONE);
        mChasingDotsDrawable = new ChasingDots();
        mChasingDotsDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        imageView.setImageDrawable(mChasingDotsDrawable);

       view=findViewById(R.id.reservation_second_screen);
        view.setVisibility(View.GONE);
        disLoading();
}



    private void showLoading(){
        view.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        mChasingDotsDrawable.start();

    }
    private void disLoading(){
        view.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        mChasingDotsDrawable.stop();

    }
    private boolean checkEditText(){
        boolean result=false;

        if (TextUtils.isEmpty(getText(name)))
        {
            result=false;
            name.setError(getResources().getString(R.string.Required));
        }
        else
        {
            result=true;
        }
        if (latLng==null||latLng.longitude==0||latLng.latitude==0)
        {
            result=false;
            Toast.makeText(this,getString(R.string.plese_select) , Toast.LENGTH_LONG).show();
         }
        else
        {
            result=true;
        }


        return result;
    }
    private String getText(EditText editText)
    {
        return editText.getText().toString();
    }


    public void loginOn(View view) {
    }



    private void sendData() {
        showLoading();
         String  url = "http://cinematvone.com/newHospital.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            String message=jsonObject.getString("message");
                            if (message.equals("Success"))
                            {
                                JSONObject Object=new JSONObject(response);
                                JSONArray AllHospital=Object.getJSONArray("Hospital");
                                JSONObject hospital=AllHospital.getJSONObject(0);
                                String id = hospital.getString("id");
                                String name = hospital.getString("name");
                                String lat = hospital.getString("lat");
                                String lng = hospital.getString("long");
                                String createdAt=hospital.getString(" createdAt");
                               new PrefManager(getApplicationContext()).saveLoginDetails(new Hospital(lat,lng,name,id,createdAt));
                                showDialogd(Integer.parseInt(id));
                            }
                            else {
                               Snackbar.make(view, message,Snackbar.LENGTH_LONG).show();

                            }
                            disLoading();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            disLoading();
                            System.out.println(e.getMessage()+"++++++++++");

                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mChasingDotsDrawable.isRunning())
                        {
                            disLoading();
                            Snackbar snackbar= Snackbar.make(view, error.getMessage(),Snackbar.LENGTH_LONG);
                            snackbar.setAction(getString(R.string.tryAgian), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    sendData();
                                }
                            });
                            snackbar.show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("name", getText(name));
                params.put("lat", String.valueOf(latLng.latitude));
                params.put("long", String.valueOf(latLng.longitude));

                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        queue.add(postRequest);

    }

    public void showDialogd(int id) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Hospital ID")
                .setContentText(String.valueOf(id))
                .setConfirmText(getResources().getString(R.string.dialog_ok))

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        startActivity();

                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        })
                .show();
    }

    private void startActivity() {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void Registration(View view) {
        if (checkEditText()) {
            sendData();
        }
    }

    public void Login(View view) {
        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
     }
    @Override
    public void onStart() {
        super.onStart();
        if (latLng!=null)
        {
            mapFragment.getMapAsync(this);
          }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));

            }
        });
        if (latLng!=null)
        {

            LatLng ny = new LatLng(latLng.latitude , latLng.longitude);
            mMap.addMarker(new MarkerOptions().position(ny).title("Your Brand"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ny,20));

        }


    }
}
