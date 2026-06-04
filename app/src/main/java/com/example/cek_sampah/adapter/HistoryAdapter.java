package com.example.cek_sampah.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cek_sampah.R;
import com.example.cek_sampah.databases.ScanHistoryEntity;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnItemActionListener {
        void onDeleteClick(int position, ScanHistoryEntity item);
        void onItemClick(ScanHistoryEntity item);
    }

    private final Context context;
    private final List<ScanHistoryEntity> historyList;
    private final OnItemActionListener listener;

    public HistoryAdapter(Context context, List<ScanHistoryEntity> historyList, OnItemActionListener listener) {
        this.context     = context;
        this.historyList = historyList;
        this.listener    = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_card, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ScanHistoryEntity item = historyList.get(position);

        holder.tvResult.setText(item.result);
        holder.tvAccuracy.setText("Akurasi: " + item.accuracy);
        holder.tvDescription.setText(item.description);
        holder.tvDate.setText(item.date);

        if (item.imagePath != null && !item.imagePath.isEmpty()) {
            holder.ivThumbnail.setImageBitmap(BitmapFactory.decodeFile(item.imagePath));
        } else {
            holder.ivThumbnail.setImageResource(R.drawable.ic_launcher);
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(holder.getAdapterPosition(), item);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView    ivThumbnail;
        TextView     tvResult, tvAccuracy, tvDescription, tvDate;
        Button  btnDelete;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail   = itemView.findViewById(R.id.ivCardThumbnail);
            tvResult      = itemView.findViewById(R.id.tvCardResult);
            tvAccuracy    = itemView.findViewById(R.id.tvCardAccuracy);
            tvDescription = itemView.findViewById(R.id.tvCardDescription);
            tvDate        = itemView.findViewById(R.id.tvCardDate);
            btnDelete     = itemView.findViewById(R.id.btnDeleteCard);
        }
    }
}