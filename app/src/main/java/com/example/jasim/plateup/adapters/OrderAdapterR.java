package com.example.jasim.plateup.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
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

public class OrderAdapterR extends RecyclerView.Adapter<OrderAdapterR.ContentClickViewHolder> {

    private ArrayList<Order> orders;
    private Context context;
    DatabaseReference mDatabase;
    private boolean pastOrders = false;

    public void setPastOrder(boolean pastOrder) {
        this.pastOrders = pastOrder;
    }

    public OrderAdapterR(Context context, ArrayList<Order> orders) {
        this.orders = orders;
        this.context = context;
    }

    public class ContentClickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mUsername;
        private TextView date;
        private TextView orderID;
        private RecyclerView dishes;
        private TextView deliveryType;
        private TextView mTotalPrice;
        private Button mPrint;


        ContentClickViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mUsername = itemView.findViewById(R.id.username);
            orderID = itemView.findViewById(R.id.order_id);
            dishes = itemView.findViewById(R.id.dishes);
            deliveryType = itemView.findViewById(R.id.delivery_type);
            date = itemView.findViewById(R.id.date);
            mTotalPrice = itemView.findViewById(R.id.total_price);
            mPrint = itemView.findViewById(R.id.print);
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

    @Override
    public int getItemCount() {
        return orders.size();
    }


    @Override
    public ContentClickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_orders, parent, false);
        return new ContentClickViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContentClickViewHolder holder, final int position) {

        int deliveryPrice = Integer.parseInt(orders.get(position).getDeliveryPrice());
        int totalPrice = Integer.parseInt(orders.get(position).getPrice_w_vat());
        ArrayList<SmallContent> orderAdapter = orders.get(position).getDishes();

        OrderAdapterDishes orderAdapterDishes = new OrderAdapterDishes(context, orderAdapter);

        System.out.println("Dishes Main: " + position + " " + orderAdapterDishes.getItemCount());


        holder.mUsername.setText(orders.get(position).getUsername());
        holder.dishes.setAdapter(orderAdapterDishes);
        holder.dishes.setLayoutManager(new LinearLayoutManager(context));
        holder.deliveryType.setText(orders.get(position).getDelivery());
        holder.mTotalPrice.setText(totalPrice + deliveryPrice + ",-");
        holder.orderID.setText(orders.get(position).getId());
        holder.date.setText(orders.get(position).getFoodReadyAt() + " - " + orders.get(position).getDateOfArrival());

        holder.mPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pastOrders) {
                    Toast.makeText(context, "Printing again. Make sure you are near the printer", Toast.LENGTH_SHORT).show();
                }
                else {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(orders.get(position).getR_id());
                    mDatabase.child("orders").child("unprinted").child(orders.get(position).getId()).removeValue();
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(1);
                }
            }
        });
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