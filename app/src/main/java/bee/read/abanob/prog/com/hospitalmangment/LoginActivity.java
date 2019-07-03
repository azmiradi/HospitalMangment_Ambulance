package bee.read.abanob.prog.com.hospitalmangment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    PinView code;
    ImageView imageView;
    private ChasingDots mChasingDotsDrawable;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialization();
    }
    private void initialization() {

        code=findViewById(R.id.pinView);

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

        if (TextUtils.isEmpty(getText(code)))
        {
            result=false;
            code.setError(getResources().getString(R.string.Required));
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
        if (checkEditText()) {
            sendData();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!(new PrefManager(this).isUserLogedOut()))
        {
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void sendData() {
        showLoading();
         String  url = "http://cinematvone.com/HospitalLogin.php";
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
                                startActivity();
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
                 params.put("id", getText(code));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        queue.add(postRequest);

    }

    private void startActivity() {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void registerOn(View view) {
        Intent intent=new Intent(getApplicationContext(),RegistrationActivity.class);
        startActivity(intent);
    }
}
