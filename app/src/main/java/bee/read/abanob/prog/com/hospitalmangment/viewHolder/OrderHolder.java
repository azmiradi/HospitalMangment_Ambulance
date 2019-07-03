package bee.read.abanob.prog.com.hospitalmangment.viewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.DefaultSliderView;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import bee.read.abanob.prog.com.hospitalmangment.Fragment.IncrementBlood;
import bee.read.abanob.prog.com.hospitalmangment.ItemClickListener;
import bee.read.abanob.prog.com.hospitalmangment.MapsActivityTracking;
import bee.read.abanob.prog.com.hospitalmangment.Model.Order;
import bee.read.abanob.prog.com.hospitalmangment.R;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrderHolder extends RecyclerView.Adapter<OrderHolder.ViewHolder> {
    int colorPosition = 0;
    private List<Order> offerList;
    private Context context;
    private ItemClickListener listener;
    private RequestOptions requestOptions;

     public OrderHolder(List<Order> offers, Context ctx, ItemClickListener itemClickListener) {
         this.listener=itemClickListener;
        offerList = offers;
        context = ctx;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Order order=offerList.get(position);
        String blood_type = null;
        if (IncrementBlood.blood!=null)
        {
            for (int i=0;i< IncrementBlood.blood.size();i++)
            {
                if (IncrementBlood.blood.get(i).getId().equals(order.getBlood_types_id()))
                {
                    blood_type=IncrementBlood.blood.get(i).getType();
                }
            }
        }
        switch (blood_type)
        {
            case "A+":
                holder.blood_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_a, 0, 0, 0);
                break;
            case "O+":
                holder.blood_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_o, 0, 0, 0);
                break;
            case "B+":
                holder.blood_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_b, 0, 0, 0);
                break;
            case "AB+":
                holder.blood_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ab, 0, 0, 0);
                break;
            case "A-":
                holder.blood_type.setText("-");
                holder.blood_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_a, 0, 0, 0);
                break;
            case "O-":
                holder.blood_type.setText("-");
                holder.blood_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_o, 0, 0, 0);
                break;
            case "B-":
                holder.blood_type.setText("-");
                holder.blood_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_b, 0, 0, 0);
                break;
            case "AB-":
                holder.blood_type.setText("-");
                holder.blood_type.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ab, 0, 0, 0);
                break;

        }
        holder.date.setText(order.getDate());
        holder.quntity.setText(order.getQuantity()+" gram");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, MapsActivityTracking.class);
                intent.putExtra("id",order.getParamedics_id());
                context.startActivity(intent);
            }
        });
     }


    @Override
    public int getItemCount() {
        return offerList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
         TextView quntity,date,blood_type;

        private WeakReference<ItemClickListener> listenerRef;

        @SuppressLint("WrongViewCast")
         public ViewHolder(View view) {
            super(view);
            listenerRef = new WeakReference<>(listener);
            view.setOnClickListener(this);
            quntity=view.findViewById(R.id.Quntity);
            date=view.findViewById(R.id.date);
            blood_type=view.findViewById(R.id.blood_type);

        }


        @Override
        public void onClick(View view) {

        }
    }


}
