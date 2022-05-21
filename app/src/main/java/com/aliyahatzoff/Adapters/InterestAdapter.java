package com.aliyahatzoff.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Models.Interest;
import com.aliyahatzoff.R;

import java.util.List;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.InterestHolder>{
   List<Interest> interestList;
   Context context;

    public InterestAdapter(List<Interest> interestList, Context context) {
        this.interestList = interestList;
        this.context = context;
    }

    @NonNull
    @Override
    public InterestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_interest,parent,false);
        return new InterestAdapter.InterestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestHolder holder, int position) {
            holder.textView.setText(interestList.get(position).getInterest());
            if(interestList.get(position).isIsselected())
                holder.tick.setVisibility(View.VISIBLE);
            else
                holder.tick.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return interestList.size();
    }
    public String  getselectedItem()
    {
        String interest=null;
        for(int i=0;i<interestList.size();++i)
        {
            if(interestList.get(i).isIsselected())
            {
                if(interest==null)
                    interest=interestList.get(i).getInterest();
                else
                    interest=interest+","+interestList.get(i).getInterest();
            }
        }
        return  interest;
    }

    class InterestHolder extends RecyclerView.ViewHolder{
            TextView textView;
            ImageView tick;
        public InterestHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.title);
            tick=itemView.findViewById(R.id.tick);
            itemView.setOnClickListener(v -> {
                if(interestList.get(getAdapterPosition()).isIsselected()) {
                    tick.setVisibility(View.GONE);
                    interestList.get(getAdapterPosition()).setIsselected(false);
                    notifyDataSetChanged();
                }
                else {
                    tick.setVisibility(View.VISIBLE);
                    interestList.get(getAdapterPosition()).setIsselected(true);
                    notifyDataSetChanged();

                }
            });
        }
    }
}
