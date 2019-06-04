package com.example.dipde.digitalcomplainbox;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;

    public ImageAdapter(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);

        if(uploadCurrent.getStatus().equals("solved")){
            holder.status.setTextColor(Color.GREEN);
        }
        else if(uploadCurrent.getStatus().equals("In Progress")){
            holder.status.setTextColor(Color.YELLOW);
        }
        else{
            holder.status.setTextColor(Color.RED);
        }
        holder.description.setText("Details: "+uploadCurrent.getDes());
        holder.authority.setText("Authority: "+uploadCurrent.getDept());
        holder.category.setText("Category: "+uploadCurrent.getCat());
        holder.time.setText("Time: "+uploadCurrent.getTime());
        holder.date.setText("Date: "+uploadCurrent.getDate());
        holder.status.setText("Status: "+uploadCurrent.getStatus());
        if (!MainActivity.flag.equals("user")){
            holder.registrationNo.setVisibility(View.VISIBLE);
            holder.registrationNo.setText("Submitted By: "+uploadCurrent.getName());
        }

        if (!uploadCurrent.getImageUrl().toString().equals("none")) {

            holder.imgstatus.setText("Tap here to see related image");
        }
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            , View.OnCreateContextMenuListener
            , MenuItem.OnMenuItemClickListener {


        public TextView description;
        public  TextView time;
        public  TextView date;
        public  TextView category;
        public  TextView registrationNo;
        public  TextView authority;
        public  TextView status;
        public  TextView imgstatus;

        public ImageViewHolder(View itemView) {

            super(itemView);
            description = itemView.findViewById(R.id.prbdes);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            category = itemView.findViewById(R.id.category);
            authority = itemView.findViewById(R.id.authority);
            registrationNo = itemView.findViewById(R.id.reg);
            status = itemView.findViewById(R.id.stat);
            imgstatus = itemView.findViewById(R.id.imgstatus);

            itemView.setOnClickListener(this);
            if(!MainActivity.flag.equals("solves"))
            {

                itemView.setOnCreateContextMenuListener(this);
            }
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

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
            if (MainActivity.flag.equals("judge")){
                MenuItem inProg = menu.add(Menu.NONE, 2, 2, "In Progress");
                inProg.setOnMenuItemClickListener(this);
                MenuItem solved = menu.add(Menu.NONE, 3, 3, "Solved");
                solved.setOnMenuItemClickListener(this);
            }

            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            //if (mListener != null) {
            int position = getAdapterPosition();
            //  if (position != RecyclerView.NO_POSITION) {

            switch (item.getItemId()) {
                case 1:
                    mListener.onDeleteClick(position);
                    return true;
                case 2:
                    mListener.onInProgClick(position);
                    return true;
                case 3:
                    mListener.onSolvedClick(position);
                    return true;
            }
            //  }
            //  }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);

        void onSolvedClick(int position);

        void onInProgClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}