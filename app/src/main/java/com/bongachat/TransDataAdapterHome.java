package com.example.bingamoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransDataAdapterHome extends RecyclerView.Adapter<TransDataAdapterHome.ViewHolder> {
    private List<TransactionDataHome> transactionDataList;
    private Context context;

    public TransDataAdapterHome(List<TransactionDataHome> transactionDataList, Context context) {
        this.transactionDataList = transactionDataList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TransactionDataHome transactionData = transactionDataList.get(position);
        holder.datail.setText(transactionData.getDetail());
        holder.transaction_info1.setText(transactionData.getTrans_info1().trim());
        holder.transaction_info2.setText(transactionData.getTrans_info2());
        holder.time.setText(transactionData.getTime());
        //Glide.with(context).asBitmap().load(transactionData.getImage()).into(holder.imageView);
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

        public ViewHolder(View view) {
            super(view);
            datail = view.findViewById(R.id.detail);
            transaction_info1= view.findViewById(R.id.transaction_info);
            transaction_info2 = view.findViewById(R.id.amount_tr);
            time = view.findViewById(R.id.time_stamp);
        }
    }
}
