package com.example.dipde.digitalcomplainbox;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.squareup.picasso.Picasso;


import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ImageViewHolder> {
    private Context mContext;
    private List<ChatUploads> mUploads;
    private OnItemClickListener mListener;

    public ChatAdapter(Context context, List<ChatUploads> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_items, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        ChatUploads uploadCurrent = mUploads.get(position);
        holder.chat.setText(uploadCurrent.getDes());
        holder.details.setText(uploadCurrent.getDetails());
        if(!uploadCurrent.getImageURL().toString().equals("none")){
           // holder.img.setVisibility(View.VISIBLE);
            GlideApp.with(mContext)
                    .load(uploadCurrent.getImageURL())
                    .centerInside()
                    .placeholder(R.drawable.cam)
                    .into(holder.iv);
        }
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView chat;
        public TextView details;
        public TextView img;
        public ImageView iv;

        public ImageViewHolder(View itemView) {

            super(itemView);
            chat = itemView.findViewById(R.id.chat);
            details = itemView.findViewById(R.id.detailss);
            img = itemView.findViewById(R.id.img);
            iv = itemView.findViewById(R.id.forumiv);

        }

        @Override
        public void onClick(View v) {
            //  if (mListener != null) {
            int position = getAdapterPosition();
            //  if (position != RecyclerView.NO_POSITION) {
            mListener.onItemClick(position);
            //}
            // }
        }
/*
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
            if (MainActivity.flag.equals("judge")){
                MenuItem solved = menu.add(Menu.NONE, 2, 2, "Solved");
                        }

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            return false;
        }
*/

    }
    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}