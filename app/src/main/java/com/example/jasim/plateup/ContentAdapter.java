package com.example.jasim.plateup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentClickViewHolder> {


    ActivityChanger mCallBack = null;
    public ContentAdapter(ActivityChanger callBack){
        this.mCallBack=callBack;
    }
    private int type;
    private String[] items;

    public class ContentClickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView dishName;
        private TextView price;
        private TextView amount;
        private Button plus;
        private Button minus;
        private TextView emptyCart;
        private TextView restName;
        private TextView totalPrice_field;
        private Context context;

        ContentClickViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            dishName = itemView.findViewById(R.id.dishname_field);
            price = itemView.findViewById(R.id.price_field);
            amount = itemView.findViewById(R.id.amount_field);
            plus = itemView.findViewById(R.id.plus_field);
            minus = itemView.findViewById(R.id.minus_field);
            restName = itemView.findViewById(R.id.rest_name_cart);
            totalPrice_field = itemView.findViewById(R.id.total_price);
            context = itemView.getContext();
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

    private ArrayList<Content> mCustomObjects;

    public ContentAdapter() {
        mCustomObjects = MainUActivity.getBasketList();
    }

    @Override
    public int getItemCount() {
        return MainUActivity.getBasketList().size();
    }


    @Override
    public ContentClickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row, parent, false);
        return new ContentClickViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContentClickViewHolder holder, final int position) {
        final Content object = MainUActivity.getBasketList().get(position);
        // My example assumes CustomClass objects have getFirstText() and getSecondText() methods
        String dishNumber = "";
        if(object.getDishnumber() != null) {
            dishNumber = object.getDishnumber() + ". ";
        }
        holder.dishName.setText(dishNumber + object.getDishname());

        holder.amount.setText("" + object.getAntall());
        holder.price.setText("" + Integer.parseInt(object.getPrice()) * object.getAntall() + "kr");
        this.mCallBack.UpdateRestName(MainUActivity.getRest_name());

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(object.getChoose() != null) {
                    int i = 0;
                    ChooseMeat chooseMeat = object.getChoose().getChoosemeat();
                    ChooseSides chooseSides = object.getChoose().getChoosesides();

                    if(chooseMeat != null) {
                        items = new String[chooseMeat.getMeat().size()];
                        for (String s : chooseMeat.getMeat().keySet()) {
                            items[i] = s;
                            if(!object.getExtraInfo().containsKey(items[i])) {
                                object.getExtraInfo().put(items[i], 0);
                            }
                            i++;
                        }
                        System.out.println("Amount: " + i);
                    }
                    else if(chooseSides != null) {
                        items = new String[chooseSides.getSides().size()];
                        for (String s : chooseSides.getSides().keySet()) {
                            items[i] = s;
                            if(!object.getExtraInfo().containsKey(items[i])) {
                                object.getExtraInfo().put(items[i], 0);
                            }
                            i++;
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.context);
                    type = 0;

                    String[] itemsValue = new String[items.length];
                    System.out.println("Items inneholder: " + itemsValue.length);
                    System.out.println("Items inneholder2: " + object.getExtraInfo());
                    i = 0;
                    for (Map.Entry<String, Integer> entry : object.getExtraInfo().entrySet()) {
                        items[i] = entry.getKey();
                        itemsValue[i] = entry.getKey() + " x" + entry.getValue();
                        i++;
                    }
                    builder.setTitle("Choose between")
                            .setSingleChoiceItems(itemsValue, 0, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    type = which;
                                }
                            })

                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    System.out.println("TESTING: " + items[type] + " " + object.getExtraInfo().get(items[type]) + 1);
                                    if(object.getExtraInfo().get(items[type]) == null) {
                                        object.getExtraInfo().put(items[type], 1);
                                    }
                                    else {
                                        object.getExtraInfo().put(items[type], object.getExtraInfo().get(items[type]) + 1);
                                    }
                                    object.setAntall(object.getAntall() + 1);
                                    object.setTotalPrice(Integer.parseInt(object.getPrice()) * object.getAntall());
                                    holder.amount.setText("" + object.getAntall());
                                    holder.price.setText("" + object.getTotalPrice() + " kr");
                                    MainUActivity.setTotalPrice(MainUActivity.getTotalPrice() + Integer.parseInt(object.getPrice()));
                                    changePrice();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).create();
                    builder.show();
                    System.out.println("Object: " + object.getExtraInfo() + " Object: " + object.getAntall());
                }
                else if(object.getQuantity() == null || Integer.parseInt(object.getQuantity()) >= object.getAntall()+1) {
                    object.setAntall(object.getAntall() + 1);
                    object.setTotalPrice(Integer.parseInt(object.getPrice()) * object.getAntall());
                    holder.amount.setText("" + object.getAntall());
                    holder.price.setText("" + object.getTotalPrice() + " kr");
                    MainUActivity.setTotalPrice(MainUActivity.getTotalPrice() + Integer.parseInt(object.getPrice()));
                    changePrice();
                    //YourOrders.getTotalField().setText("" + (MainUActivity.getTotalPrice()) + " kr");
                }
                else {
                    YourOrders.makeToast("There are only " + object.getAntall() + " quantities left of this item.");
                }

            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(object.getAntall() > 0) {
                    if(object.getChoose() != null) {
                        if(object.getAntall() == 1) {
                            object.setAntall(object.getAntall() - 1);
                            object.setTotalPrice(Integer.parseInt(object.getPrice()) * object.getAntall());
                            holder.amount.setText("" + object.getAntall());
                            holder.price.setText("" + object.getTotalPrice() + " kr");
                            MainUActivity.setTotalPrice(MainUActivity.getTotalPrice() - Integer.parseInt(object.getPrice()));
                            changePrice();
                        }
                        else {
                            int i = 0;
                            items = new String[object.getExtraInfo().size()];
                            String[] itemsValue = new String[object.getExtraInfo().size()];
                            for (Map.Entry<String, Integer> entry : object.getExtraInfo().entrySet()) {
                                items[i] = entry.getKey();
                                itemsValue[i] = entry.getKey() + " x" + entry.getValue();
                                i++;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.context);
                            type = 0;
                            builder.setTitle("Choose between")
                                    .setSingleChoiceItems(itemsValue, 0, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            type = which;
                                        }
                                    })

                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (object.getExtraInfo().get(items[type]) == null) {
                                                object.getExtraInfo().put(items[type], 1);
                                            } else {
                                                object.getExtraInfo().put(items[type], object.getExtraInfo().get(items[type]) - 1);
                                                if(object.getExtraInfo().get(items[type]) == 0) {
                                                    object.getExtraInfo().remove(items[type]);
                                                }
                                            }
                                            object.setAntall(object.getAntall() - 1);
                                            object.setTotalPrice(Integer.parseInt(object.getPrice()) * object.getAntall());
                                            holder.amount.setText("" + object.getAntall());
                                            holder.price.setText("" + object.getTotalPrice() + "kr");
                                            MainUActivity.setTotalPrice(MainUActivity.getTotalPrice() - Integer.parseInt(object.getPrice()));
                                            //YourOrders.getTotalField().setText("" + (MainUActivity.getTotalPrice()) + " kr");
                                            changePrice();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    }).create();
                            builder.show();
                            System.out.println("Object: " + object.getExtraInfo() + " Object: " + object.getAntall());
                        }
                    }
                    else {
                        object.setAntall(object.getAntall() - 1);
                        object.setTotalPrice(Integer.parseInt(object.getPrice()) * object.getAntall());
                        holder.amount.setText("" + object.getAntall());
                        holder.price.setText("" + object.getTotalPrice() + "kr");
                        MainUActivity.setTotalPrice(MainUActivity.getTotalPrice() - Integer.parseInt(object.getPrice()));
                        //YourOrders.getTotalField().setText("" + (MainUActivity.getTotalPrice()) + " kr");
                        changePrice();
                    }
                    System.out.println("TESTINGamount: " + object.getAntall());
                    if (object.getAntall() == 0) {
                        System.out.println("TESTINGvi kommer inn i remove");
                        removeAt(holder.getAdapterPosition());
                        if (MainUActivity.getBasketList().size() == 0) {
                            //YourOrders.setRestName("");
                            MainUActivity.setRest_name(null);
                            MainUActivity.setRest_id(null);
                        }
                    }
                }
            }
        });
    }
    public void changePrice() {
        this.mCallBack.UpdateTotalPrice(MainUActivity.getTotalPrice() + " kr");
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
        MainUActivity.getBasketList().remove(position);
        notifyItemRemoved(position);
    }

}