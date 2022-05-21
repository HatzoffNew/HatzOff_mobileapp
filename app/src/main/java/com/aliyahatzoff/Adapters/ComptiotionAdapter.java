package com.aliyahatzoff.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Activities.ComptitionInstruction;
import com.aliyahatzoff.Models.ComptitionModel;
import com.aliyahatzoff.R;

import java.util.List;

public class ComptiotionAdapter extends RecyclerView.Adapter<ComptiotionAdapter.Viewholder> {
    Context context;
    List<ComptitionModel> comptitionModelList;

    public ComptiotionAdapter(Context context, List<ComptitionModel> comptitionModelList) {
        this.context = context;
        this.comptitionModelList = comptitionModelList;
    }

    @NonNull
    @Override
    public ComptiotionAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.competition_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComptiotionAdapter.Viewholder holder, int position) {
        holder.comp_name.setText(comptitionModelList.get(position).getInterest());
        holder.comp_price.setText(String.valueOf(comptitionModelList.get(position).getPrice()));
        holder.imageView.setImageResource(comptitionModelList.get(position).getImage());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ComptitionInstruction.class);
                intent.putExtra("price",String.valueOf(comptitionModelList.get(position).getPrice()));
                intent.putExtra("intrest",comptitionModelList.get(position).getInterest());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return comptitionModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView comp_name, comp_price;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.comptition_img);
            comp_name = itemView.findViewById(R.id.comptition_name);
            comp_price = itemView.findViewById(R.id.comptition_price);

        }
    }
}
