package bee.read.abanob.prog.com.hospitalmangment.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import bee.read.abanob.prog.com.hospitalmangment.Model.Blood;
import bee.read.abanob.prog.com.hospitalmangment.PrefManager;
import bee.read.abanob.prog.com.hospitalmangment.R;
import bee.read.abanob.prog.com.hospitalmangment.ViewQR;

import static android.content.Context.WINDOW_SERVICE;

public class IncrementBlood extends Fragment {
    View view;
     EditText Quntity;
    View v;
     Spinner bloodType;
    String bloadid;
    ImageView imageView;
    Button button;
   public static List<Blood> blood;
     TextView exp;
    static String format;
    Date mSelectedFromDate;

    String TAG = "GenerateQRCode";
    public static String inputValue;

    public static Bitmap bitmap;

    QRGEncoder qrgEncoder;

    private ChasingDots mChasingDotsDrawable;
     @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.increment_blood, container, false);
        initialization();
        return v;
    }

    private void initialization() {



        blood= new ArrayList<>();
        Quntity=v.findViewById(R.id.Quntity);
        button=v.findViewById(R.id.AddBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBlood();
            }
        });
        bloodType=v.findViewById(R.id.blood_type);
        exp=v.findViewById(R.id.exp);
        exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onDataExpired();
            }
        });

        //ImageView
        imageView = (ImageView) v. findViewById(R.id.image);
        mChasingDotsDrawable = new ChasingDots();
        mChasingDotsDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        imageView.setImageDrawable(mChasingDotsDrawable);

        view=v.findViewById(R.id.reservation_second_screen);
        view.setVisibility(View.GONE);
        disLoading();
        bloodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bloadid= String.valueOf(blood.get(i).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getAllBlood();
    }

    private void addBlood() {
        showLoading();
        String  url = "http://cinematvone.com/incrementBlood.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            boolean success=jsonObject.getBoolean("success");
                            if (success)
                            {
                                int qurId=jsonObject.getInt("qur_id");
                                Snackbar.make(view, "Blood Add successful",Snackbar.LENGTH_LONG).show();
                                Quntity.setText("");
                                bloodType.setSelection(0);
                                generatQR(String.valueOf(qurId));
                            }
                            else {
                                Snackbar.make(view, "Blood Add Failure",Snackbar.LENGTH_LONG).show();

                            }
                            disLoading();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage()+"++++++++++");

                            disLoading();
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
                                    addBlood();
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
                params.put("quantity",  Quntity.getText().toString());
                params.put("hospitals_id", new PrefManager(getActivity()).getHospital().getId());
                params.put("blood_types_id", bloadid);
                params.put("exp", format);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());

        queue.add(postRequest);
    }
    private void getAllBlood() {
        showLoading();
        final String url = "http://cinematvone.com/smart_ambulance/api/paramedic/bloodTypes";


        final List<String> names=new ArrayList<>();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray=response.getJSONArray("blood_types");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String id=jsonObject.getString("id");
                                String type=jsonObject.getString("type");
                                blood.add(new Blood(id,type));
                                names.add(type);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                    (getActivity(), R.layout.spiner, names);
                            bloodType.setAdapter(adapter);//setting the adapter data into the s
                            bloadid= String.valueOf(blood.get(0).getId());
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                           disLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage()+"++++++++++");
                            disLoading();
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
                                    getAllBlood();
                                }
                            });
                            snackbar.show();
                        }

                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(getRequest);
        getRequest.setShouldCache(false);
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinshed) {
            }
            public void onFinish() {
                if (mChasingDotsDrawable.isRunning())
                {
                    getAllBlood();
                }
            }
        }.start();
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
    public void onDataExpired() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEEE dd MMMMM");
                format = simpleDateFormat.format(calendar.getTime());
                exp.setText(format);
                simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");
                format = simpleDateFormat.format(calendar.getTime());
                Toast.makeText(getActivity(), ""+format, Toast.LENGTH_SHORT).show();
                format=calendar.getTimeInMillis()+"";
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                mSelectedFromDate = calendar.getTime();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(date.getTime());
        datePickerDialog.show();
    }
  public void generatQR(String id)
    {
        inputValue = id;
        if (inputValue.length() > 0) {
            WindowManager manager = (WindowManager)getActivity().getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(
                    inputValue, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
                startActivity();
             } catch (WriterException e) {
                Log.v(TAG, e.toString());
            }
        } else {
         }

    }
    private void startActivity() {
        Intent intent=new Intent(getActivity(), ViewQR.class);
        startActivity(intent);
     }
}
