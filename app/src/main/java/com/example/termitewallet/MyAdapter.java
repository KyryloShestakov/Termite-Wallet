package com.example.termitewallet;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<CryptoUtils.TransactionModel> dataList;
    private String myAddress;

    public MyAdapter(List<CryptoUtils.TransactionModel> dataList, String myAddress) {
        this.dataList = dataList;
        this.myAddress = myAddress;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CryptoUtils.TransactionModel transaction = dataList.get(position);
        holder.textViewId.setText("Id: " + transaction.getId());
        holder.textViewSender.setText("Adress: " + transaction.getSender());
        holder.textViewAmount.setText(transaction.getAmount().toString() + " TER");

        if (transaction.getReceiver().equals(myAddress)) {
            holder.textViewAmount.setTextColor(Color.GREEN);
        } else if (transaction.getSender().equals(myAddress)) {
            holder.textViewAmount.setTextColor(Color.RED);
        } else {
            holder.textViewAmount.setTextColor(Color.BLACK);
        }
    }



    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewId, textViewSender, textViewAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewId = itemView.findViewById(R.id.transactionId);
            textViewSender = itemView.findViewById(R.id.transactionSender);
            textViewAmount = itemView.findViewById(R.id.transactionAmount);
        }
    }
}
