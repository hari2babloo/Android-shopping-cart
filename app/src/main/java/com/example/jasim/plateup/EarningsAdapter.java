package com.example.jasim.plateup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EarningsAdapter extends RecyclerView.Adapter<EarningsAdapter.ContentClickViewHolder> {

    ActivityChanger mCallBack = null;
    private DatabaseReference mDatabase;

    public EarningsAdapter(ActivityChanger callBack){
        this.mCallBack=callBack;
    }

    public class ContentClickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mUsername;
        private TextView mPrice_wo_vat;
        private TextView mVat;
        private TextView mPrice_w_vat;


        ContentClickViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mUsername = (TextView) itemView.findViewById(R.id.username);
            mPrice_wo_vat = (TextView) itemView.findViewById(R.id.price_wo_vat);
            mVat = (TextView) itemView.findViewById(R.id.vat);
            mPrice_w_vat = (TextView) itemView.findViewById(R.id.price_w_vat);
        }

        @Override
        public void onClick(View v) {
            // The user may not set a click listener for list items, in which case our listener
            // will be null, so we need to check for this
            if (mOnEntryClickListener != null) {
                mOnEntryClickListener.onEntryClick(v, getLayoutPosition());
            }
        }
    }

    private ArrayList<Order> mCustomObjects;

    public EarningsAdapter(ArrayList<Order> mCustomObjects) {
        this.mCustomObjects = mCustomObjects;
    }

    @Override
    public int getItemCount() {
        return mCustomObjects.size();
    }


    @Override
    public ContentClickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.earnings_row, parent, false);
        return new ContentClickViewHolder(view);
    }




    @Override
    public void onBindViewHolder(final ContentClickViewHolder holder, int position) {

        final Order object = mCustomObjects.get(position);
        holder.mUsername.setText(object.getUsername());
        holder.mPrice_wo_vat.setText(object.getPrice_wo_vat());
        holder.mVat.setText(object.getVat());
        holder.mPrice_w_vat.setText(object.getPrice_w_vat());


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    private OnEntryClickListener mOnEntryClickListener;

    public interface OnEntryClickListener {
        void onEntryClick(View view, int position);
    }

    public void setOnEntryClickListener(OnEntryClickListener onEntryClickListener) {

        mOnEntryClickListener = onEntryClickListener;
    }
    public void removeAt(int position) {
        mCustomObjects.remove(position);
        notifyItemRemoved(position);
    }

}