package bee.read.abanob.prog.com.hospitalmangment.viewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import bee.read.abanob.prog.com.hospitalmangment.Fragment.IncrementBlood;
import bee.read.abanob.prog.com.hospitalmangment.Model.Blood;
import bee.read.abanob.prog.com.hospitalmangment.ItemClickListener;
import bee.read.abanob.prog.com.hospitalmangment.R;

public class BloodHolder extends RecyclerView.Adapter<BloodHolder.ViewHolder> {
    int colorPosition = 0;
    private List<Blood> bloodList;
    private Context context;
    private ItemClickListener listener;
    private RequestOptions requestOptions;

     public BloodHolder(List<Blood> bloods, Context ctx, ItemClickListener itemClickListener) {
         this.listener=itemClickListener;
        bloodList = bloods;
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
        Blood blood=bloodList.get(position);
        String blood_type = null;
        if (IncrementBlood.blood!=null)
         {
             for (int i=0;i< IncrementBlood.blood.size();i++)
             {
                 if (IncrementBlood.blood.get(i).getId().equals(blood.getId()))
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
        holder.date.setVisibility(View.GONE);
        holder.quntity.setText(blood.getType()+" gram");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
     }


    @Override
    public int getItemCount() {
        return bloodList.size();
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
