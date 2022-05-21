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

import com.aliyahatzoff.Activities.OthersProfile;
import com.aliyahatzoff.Api.ApiClient;
import com.aliyahatzoff.Models.User;
import com.aliyahatzoff.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserHolder> {
    List<User> userList;
    Context context;

    public UserSearchAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_usersearch,parent,false);
        return new UserSearchAdapter.UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
            holder.txtname.setText(userList.get(position).getName());
            holder.txtid.setText(String.valueOf(userList.get(position).getId()));
        String imageFilePath="";
        if(userList.get(position).getProfile_pic()!=null) {
            if (userList.get(position).getProfile_pic().contains("http"))
                imageFilePath = userList.get(position).getProfile_pic();
            else
                imageFilePath = ApiClient.image_URL + "/profile/" + userList.get(position).getProfile_pic();

            try {
                Picasso.get().
                        load(imageFilePath)
                        .resize(150, 150)
                        .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                        .into(holder.imageView);

            } catch (Exception e) {

            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, OthersProfile.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserHolder extends RecyclerView.ViewHolder{
                TextView txtname,txtid;
                ImageView imageView;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            txtname=itemView.findViewById(R.id.username);
            txtid=itemView.findViewById(R.id.userid);
            imageView=itemView.findViewById(R.id.img);

        }
    }
}
