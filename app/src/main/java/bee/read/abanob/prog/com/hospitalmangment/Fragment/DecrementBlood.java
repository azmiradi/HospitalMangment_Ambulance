package bee.read.abanob.prog.com.hospitalmangment.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import bee.read.abanob.prog.com.hospitalmangment.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class DecrementBlood extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions( //Method of Fragment
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ,Manifest.permission.CAMERA},
                    0
            );
        } else {

        }
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        decrementBlood(rawResult.getText());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(DecrementBlood.this);
            }
        }, 2000);
    }
    private void decrementBlood(final String id) {
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("wait....");
        progressDialog.show();
        String  url = "http://cinematvone.com/decrmentBlood.php";
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
                                showDialogd();
                            }
                            else {
                                Snackbar.make(mScannerView, "Blood Decrement Failure",Snackbar.LENGTH_LONG).show();

                            }
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage()+"++++++++++");
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog.isShowing())
                        {
                            progressDialog.dismiss();
                             Snackbar snackbar= Snackbar.make(mScannerView, error.getMessage(),Snackbar.LENGTH_LONG);
                            snackbar.setAction(getString(R.string.tryAgian), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    decrementBlood(id);
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
                params.put("id",id );

                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());

        queue.add(postRequest);
    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    public void showDialogd( ) {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Blood Decrement successful")
                .setConfirmText(getResources().getString(R.string.dialog_ok))

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                     sDialog.dismissWithAnimation();
                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        })
                .show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (permissions[1].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED ||
                    permissions[0].equals(Manifest.permission.CAMERA )
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {

            }
        }
    }
}