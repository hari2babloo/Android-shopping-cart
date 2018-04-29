package com.example.jasim.plateup.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
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

import com.example.jasim.plateup.Order;
import com.example.jasim.plateup.R;
import com.example.jasim.plateup.SmallContent;
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
import java.util.Map;

public class OrderAdapterDishes extends RecyclerView.Adapter<OrderAdapterDishes.ContentClickViewHolder> {

    private Order order;
    private DatabaseReference mDatabase;
    private ArrayList<SmallContent> orders;
    private boolean pastOrder = true;

    public OrderAdapterDishes(Context context, ArrayList<SmallContent> orders) {
        this.orders = orders;
    }

    public class ContentClickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mAmount;
        private TextView mDishName;
        private TextView mPrice;
        private TextView mComment;
        private TextView mExtraInfo;


        ContentClickViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAmount = itemView.findViewById(R.id.amount);
            mDishName = itemView.findViewById(R.id.dish_name);
            mPrice = itemView.findViewById(R.id.price);
            mComment = itemView.findViewById(R.id.comment);
            mExtraInfo = itemView.findViewById(R.id.extra_info);

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


    public void setPastOrder(boolean pastOrder) {
        this.pastOrder = pastOrder;
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public ContentClickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.u_orders_dishes, parent, false);
        return new ContentClickViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContentClickViewHolder holder, int position) {
        System.out.println("Dishes: " + position);
        SmallContent sm = orders.get(position);
        System.out.println("PRICE!: " + sm.getPrice());
        //holder.mAmount.setText();
        holder.mAmount.setText(sm.getAntall() + "x");
        holder.mDishName.setText(sm.getDishname());
        holder.mPrice.setText(sm.getPrice()  + ",-");
        if(sm.getComment() != null) {
            holder.mComment.setText(sm.getComment());
        }
        else {
            holder.mComment.setVisibility(View.GONE);
        }
        holder.mExtraInfo.setVisibility(View.GONE);
        if(sm.getExtraInfo().size() > 0) {
            System.out.println("KOMMER VI INN HIT pAA " + sm.getDishname());
            String key = "";
            int i = 1;
            for (Map.Entry<String, Integer> entry : sm.getExtraInfo().entrySet()) {
                if(i == sm.getExtraInfo().size()) {
                    key += entry.getKey() + " x" + entry.getValue();
                }
                else {
                    key += entry.getKey() + " x" + entry.getValue() + "\n";
                }
                i++;
            }
            holder.mExtraInfo.setVisibility(View.VISIBLE);
            holder.mExtraInfo.setText(key);
        }
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
        orders.remove(position);
        notifyItemRemoved(position);
    }
}