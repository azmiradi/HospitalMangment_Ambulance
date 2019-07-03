package bee.read.abanob.prog.com.hospitalmangment.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bee.read.abanob.prog.com.hospitalmangment.Model.Blood;
import bee.read.abanob.prog.com.hospitalmangment.ItemClickListener;
import bee.read.abanob.prog.com.hospitalmangment.PrefManager;
import bee.read.abanob.prog.com.hospitalmangment.R;
import bee.read.abanob.prog.com.hospitalmangment.viewHolder.BloodHolder;

public class BloodAllBank extends Fragment {
    View v;
    List<Blood> blood;
    RecyclerView recyclerView;
    ImageView imageView;
    private ChasingDots mChasingDotsDrawable;
    View view;
    TextView text_no_info;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.orders_fragment, container, false);
        initialization();
        return v;
    }

    private void initialization() {
        blood=new ArrayList<>();

        recyclerView=v.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        text_no_info=v.findViewById(R.id.no_request);
        //ImageView
        imageView = (ImageView) v. findViewById(R.id.image);
        mChasingDotsDrawable = new ChasingDots();
        mChasingDotsDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        imageView.setImageDrawable(mChasingDotsDrawable);

        view=v.findViewById(R.id.reservation_second_screen);
        view.setVisibility(View.GONE);
        disLoading();
        sendData();
    }
    private void sendData() {
        showLoading();
        String  url = "http://cinematvone.com/AllBloodBags.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                                blood.clear();
                                JSONArray Orders=jsonObject.getJSONArray("blood_bags");
                                if (Orders.length()>0)
                                {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    text_no_info.setVisibility(View.GONE);
                                    for (int i=0;i<Orders.length();i++)
                                    {
                                        JSONObject object=Orders.getJSONObject(i);
                                        String blood_types_id=object.getString("blood_types_id");
                                        String quantity=object.getString("quantity");
                                        String exp=object.getString("blood_types_id");
                                        String kind=object.getString("kind");

                                    }
                                    BloodHolder orderHolder=new BloodHolder(blood, getActivity(), new ItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {

                                        }
                                    });
                                    recyclerView.setAdapter(orderHolder);

                                }
                                else
                                {
                                    recyclerView.setVisibility(View.GONE);
                                    text_no_info.setText("No Found Blood Bags");
                                    text_no_info.setVisibility(View.VISIBLE);

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
                params.put("id", new PrefManager(getActivity()).getHospital().getId());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        queue.add(postRequest);

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
}
