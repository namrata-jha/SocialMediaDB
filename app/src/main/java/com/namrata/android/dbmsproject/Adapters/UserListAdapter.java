package com.namrata.android.dbmsproject.Adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.namrata.android.dbmsproject.ProfileActivity;
import com.namrata.android.dbmsproject.R;
import com.namrata.android.dbmsproject.User;
import com.squareup.picasso.Picasso;


import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    private List<User> dataList;
    private Context context;

    public UserListAdapter(List<User> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.user_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.name.setText(dataList.get(i).getName());
        Picasso.get().load(dataList.get(i).getProfilePictureUrl()).into(myViewHolder.profileImage);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView profileImage;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_list_name);
            profileImage = itemView.findViewById(R.id.user_list_image);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ProfileActivity.class);
                    intent.putExtra("userId", dataList.get(getAdapterPosition()).getUniqueId());
                    intent.putExtra("name", dataList.get(getAdapterPosition()).getName());
                    intent.putExtra("email", dataList.get(getAdapterPosition()).getEmail());
                    intent.putExtra("url", dataList.get(getAdapterPosition()).getProfilePictureUrl());

                    itemView.getContext().startActivity(intent);
                }
            });


        }
    }
}
