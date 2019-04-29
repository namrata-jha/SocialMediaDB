package com.namrata.android.dbmsproject.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.namrata.android.dbmsproject.Comment;
import com.namrata.android.dbmsproject.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewHolder> {

    private List<Comment> dataList;
    private Context context;

    public CommentListAdapter(List<Comment> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }



    @NonNull
    @Override
    public CommentListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.comment_layout, viewGroup, false);
        return new CommentListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.name.setText(dataList.get(i).getCommenterName());
        myViewHolder.comment.setText(dataList.get(i).getCommentText());
        myViewHolder.time.setText(getDate(dataList.get(i).getCommentTimestamp()));
        Picasso.get().load(dataList.get(i).getProfilePictureUrl()).into(myViewHolder.profileImage);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, comment, time;
        ImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.comment_list_name);
            profileImage = itemView.findViewById(R.id.comment_list_image);
            comment = itemView.findViewById(R.id.comment_list_text);
            time = itemView.findViewById(R.id.comment_list_time);


        }
    }

    private String getDate(String timestamp) {
        long time = Long.parseLong(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a, d MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(time));
    }
}