package com.example.jasim.plateup;

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

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ContentClickViewHolder> {

    private Order orderi;

    ActivityChanger mCallBack = null;
    private DatabaseReference mDatabase;

    public OrderAdapter(ActivityChanger callBack) {
        this.mCallBack = callBack;
    }

    public class ContentClickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView orderID;
        private TextView username;
        private TextView price;
        private TextView date;
        private TextView deliveryType;
        private TextView deliveryTypeUser;
        private TextView dishes;
        private TextView amount;
        private TextView mPriceField;
        private TextView mDishPrice;
        private TextView mVat;
        private Button mPrint;
        private Button mTrack;
        private Button mMinus;
        private Button mPlus;
        private TextView mAmount;
        private TextView mExtraInfo;


        ContentClickViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            orderID = itemView.findViewById(R.id.order_id);
            username = itemView.findViewById(R.id.username);
            dishes = itemView.findViewById(R.id.dishes);
            deliveryType = itemView.findViewById(R.id.delivery_type);
            deliveryTypeUser = itemView.findViewById(R.id.delivery_type_user);
            price = itemView.findViewById(R.id.price);
            mVat = itemView.findViewById(R.id.vat);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            mPrint = itemView.findViewById(R.id.print);
            mPriceField = itemView.findViewById(R.id.price_field);
            mDishPrice = itemView.findViewById(R.id.dish_price);
            mTrack = itemView.findViewById(R.id.track_order);
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

    private ArrayList<Order> mCustomObjects;
    private boolean pastOrder = true;
    private boolean forRest;


    public void setPastOrder(boolean pastOrder) {
        this.pastOrder = pastOrder;
    }

    public OrderAdapter(ArrayList<Order> mCustomObjects, boolean forRest) {
        this.mCustomObjects = mCustomObjects;
        this.forRest = forRest;
    }

    @Override
    public int getItemCount() {
        return mCustomObjects.size();
    }


    @Override
    public ContentClickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rest_feed_row, parent, false);
        return new ContentClickViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContentClickViewHolder holder, int position) {

        final Order object = mCustomObjects.get(position);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(object.getR_id()).child("username");
        if (forRest) {
            holder.username.setText(object.getUsername());
        } else {
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.username.setText((String) dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        holder.orderID.setText(object.getId());
        String dishnames = "";
        String amount = "";
        String dishPrice = "";
        String extraInfo = "";
        String comment = "";
        int i = 0;
        for (SmallContent s : object.getDishes()) {
            if (i == object.getDishes().size() - 1) {
                String dishNumber = "";
                dishnames += s.getDishname();
                amount += s.getAntall() + "x";
                dishPrice += s.getPrice() * s.getAntall() + ",-";
                for (Map.Entry<String, Integer> entry : s.getExtraInfo().entrySet()) {
                    dishnames += " \n " + entry.getValue() + "x " + entry.getKey() + "\n";
                    amount += "\n";
                    dishPrice += "\n";
                }
                dishnames += "\n" + s.getComment() + "\n";
            } else {
                dishnames += s.getDishname() + "\n";
                amount += s.getAntall() + "x\n";

                dishPrice += s.getPrice() * s.getAntall() + ",- \n";


                for (Map.Entry<String, Integer> entry : s.getExtraInfo().entrySet()) {
                    dishnames += " " + entry.getValue() + "x " + entry.getKey() + "\n";
                    amount += "\n";
                    dishPrice += "\n";

                }
                amount += "\n";
                dishPrice += "\n";
                dishnames += "test" + "\n";
            }
            i++;
        }
        holder.mDishPrice.setText(dishPrice);
        holder.price.setText(object.getPrice_w_vat() + ",-");
        holder.dishes.setText(dishnames);
        holder.amount.setText(amount);
        holder.amount.setTypeface(holder.amount.getTypeface(), Typeface.BOLD);
        if (forRest) {
            holder.date.setText(object.getDateOfArrival() + " - " + object.getFoodReadyAt());
            holder.mPrint.setVisibility(View.VISIBLE);
            holder.mPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderi = object;
                    mDatabase.getParent().child("orders").child("unprinted").child(orderi.getId()).removeValue();

                }
            });
            holder.deliveryType.setVisibility(View.VISIBLE);
            holder.deliveryType.setText(object.getDelivery());
            holder.mExtraInfo.setVisibility(View.VISIBLE);
        } else {
            holder.price.setText(Integer.parseInt(object.getPrice_w_vat()) + Integer.parseInt(object.getDeliveryPrice()) + ",-");
            holder.deliveryTypeUser.setVisibility(View.VISIBLE);
            holder.deliveryTypeUser.setText(object.getDelivery() + " " + object.getDeliveryPrice() + ",-");
            holder.mPriceField.setText("Total: ");
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date d = null;
            try {
                d = df.parse(object.getTimeOfOrder());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (object.getDelivery().equals("Delivery")) {
                if (!pastOrder) {
                    holder.mTrack.setVisibility(View.VISIBLE);
                }
            }
            holder.date.setText(object.getDateOfArrival() + " - " + object.getTimeOfArrival());
        }

        // My example assumes CustomClass objects have getFirstText() and getSecondText() methods
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