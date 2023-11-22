package com.bongachat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TransDataAdapter extends RecyclerView.Adapter<TransDataAdapter.ViewHolder> {
    private List<TransactionData> transactionDataList;
    private Context context;

    public TransDataAdapter(Context context,List<TransactionData> transactionDataList) {
        this.transactionDataList = transactionDataList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card2, parent, false);

        //StringValueManager stringManager = new StringValueManager(context.getApplicationContext());

        /*String theme = "default";
        // To retrieve the current value
        String currentValue = stringManager.getValue();
        if(currentValue == null){
            theme = "default";
        } else if (currentValue != null) {
            theme = currentValue;
        }
        if(theme.equals("default")){
            //

        } else if (theme.contains("rown")) {
            //brown theme apply here
           view.setBackgroundColor(ContextCompat.getColor(context,R.color.b3));

        }else if (theme.contains("fault")) {
            //default theme apply there
            view.setBackgroundColor(ContextCompat.getColor(context,R.color.g2));



        }*/


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TransactionData transactionData = transactionDataList.get(position);
        holder.datail.setText(transactionData.getDetail());
        holder.transaction_info1.setText(transactionData.getTrans_info1().trim());
        holder.transaction_info2.setText(transactionData.getTrans_info2());
        holder.time.setText(transactionData.getTime());
        Glide.with(context).asBitmap().load(transactionData.getImage()).into(holder.imageView);
        //Picasso.get().load(Arrays.toString(transactionData.getImage())).into(holder.imageView);

        //Glide.with(context).asBitmap().load(transactionData.getImage()).into(holder.imageView);

        /*//applying theme
        //calling our data fetcher for theme
        StringValueManager stringManager = new StringValueManager(context);

        String theme = "default";
        // To retrieve the current value
        String currentValue = stringManager.getValue();
        if(currentValue == null){
            theme = "default";
        } else if (currentValue != null) {
            theme = currentValue;
        }
        if(theme.equals("default")){
            //

        } else if (theme.contains("rown")) {
            //brown theme apply here
            holder.datail.setTextColor(ContextCompat.getColor(context, R.color.b1));
            holder.transaction_info1.setTextColor(ContextCompat.getColor(context, R.color.b1));
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.b1));
            holder.transaction_info2.setTextColor(ContextCompat.getColor(context, R.color.b2));

        }else if (theme.contains("fault")) {
            //default theme apply there

            holder.datail.setTextColor(ContextCompat.getColor(context, R.color.g1));
            holder.transaction_info1.setTextColor(ContextCompat.getColor(context, R.color.g1));
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.g1));
            holder.transaction_info2.setTextColor(ContextCompat.getColor(context, R.color.g0));


        }*/

    }




    @Override
    public int getItemCount() {
        return transactionDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView datail;
        public TextView transaction_info1;
        public  TextView transaction_info2;
        public TextView time;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            datail = view.findViewById(R.id.detail);
            transaction_info1= view.findViewById(R.id.transaction_info);
            transaction_info2 = view.findViewById(R.id.amount_tr);
            time = view.findViewById(R.id.time_stamp);
            imageView = view.findViewById(R.id.imageView2);
        }
    }
}
