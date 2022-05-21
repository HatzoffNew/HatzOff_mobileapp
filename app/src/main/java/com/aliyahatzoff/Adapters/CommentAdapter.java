package com.aliyahatzoff.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Models.Comment;
import com.aliyahatzoff.R;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    List<Comment> commentList;
    Context context;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.model_comment_layout,parent,false);
        return new CommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.username.setText(commentList.get(position).getUsername());
        holder.comment.setText(StringEscapeUtils.unescapeJava(commentList.get(position).getComment()));

        try{
            Picasso.get().
                    load(commentList.get(position).getProfilepic())
                    .resize(50,50)
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .into(holder.profilepic);

        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView username,comment;
        ImageView profilepic;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
          username=itemView.findViewById(R.id.username);
          comment=itemView.findViewById(R.id.message);
          profilepic=itemView.findViewById(R.id.user_pic);
        }
    }
}
