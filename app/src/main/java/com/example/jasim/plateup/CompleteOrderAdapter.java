package com.example.jasim.plateup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CompleteOrderAdapter extends RecyclerView.Adapter<CompleteOrderAdapter.ContentClickViewHolder> {
    private Activity context;
    private RadioButton[] rb;

    public class ContentClickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView dishName;
        private TextView price;
        private TextView amount;
        private TextView emptyCart;
        private TextView totalPrice_field;

        ContentClickViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            dishName = itemView.findViewById(R.id.dishname_field);
            price = itemView.findViewById(R.id.price_field);
            amount = itemView.findViewById(R.id.amount_field);
            totalPrice_field = itemView.findViewById(R.id.total_price);
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

    public CompleteOrderAdapter(Activity context) {
        mCustomObjects = MainUActivity.getBasketList();
        this.context = context;
    }

    @Override
    public int getItemCount() {

        return MainUActivity.getBasketList().size();
    }


    @Override
    public ContentClickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completeorder_row, parent, false);
        return new ContentClickViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ContentClickViewHolder holder, int position) {
        final Content object = MainUActivity.getBasketList().get(position);
        // My example assumes CustomClass objects have getFirstText() and getSecondText() methods
        String dishNumber = "";
        if(object.getDishnumber() != null) {
            dishNumber = object.getDishnumber() + ". ";
        }
        holder.dishName.setText(dishNumber + object.getDishname());
        holder.dishName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("Title...");
                // TextView chooseBetween = dialog.findViewById(R.id.choosebetween_text);

                rb = null;
                //final RadioGroup rg = dialog.findViewById(R.id.choosebetween);
                int size = 0;
                HashMap<String, String> chooses = new HashMap<>();
                if(object.getChoose() != null) {
                    // chooseBetween.setVisibility(View.VISIBLE);
                    if (object.getChoose().getChoosemeat().getMeat() != null) {
                        size = object.getChoose().getChoosemeat().getMeat().size();
                        chooses = object.getChoose().getChoosemeat().getMeat();
                    } else if (object.getChoose().getChoosesides().getSides() != null) {
                        size = object.getChoose().getChoosesides().getSides().size();
                        chooses = object.getChoose().getChoosesides().getSides();
                    }
                    rb = new RadioButton[chooses.size()];
                    int i = 0;
                    for (Map.Entry<String, String> entry : chooses.entrySet()) {
                        rb[i] = new RadioButton(context);
                        rb[i].setText(entry.getKey());
                        rb[i].setId(i);
                        //rg.addView(rb[i]);
                        i++;
                    }
                    for(i = 0; i < chooses.size(); i++) {
                        if(rb[i].getText().toString().equals(object.getExtraInfo())) {
                            rb[i].setChecked(true);
                        }
                    }
                }

                Button mOk = dialog.findViewById(R.id.ok);
                Button mCancel = dialog.findViewById(R.id.cancel);
                final EditText mComments = dialog.findViewById(R.id.comment);
                if(object.getComment() != null) {
                    mComments.setText(object.getComment());
                }
                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();

                    }
                });

                mOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        object.setComment(mComments.getText().toString());
//                        if(rb != null) {
//                            if(object.getExtraInfo().containsKey(rb[rg.getCheckedRadioButtonId()].getText().toString())) {
//                                object.getExtraInfo().put(rb[rg.getCheckedRadioButtonId()].getText().toString(), object.getExtraInfo().get(rb[rg.getCheckedRadioButtonId()].getText().toString()) + 1);
//                            }
//                            else {
//                                object.getExtraInfo().put(rb[rg.getCheckedRadioButtonId()].getText().toString(), 1);
//                            }
//                        }
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });
        holder.amount.setText(object.getAntall() + "pcs");
        holder.price.setText(Integer.parseInt(object.getPrice()) * object.getAntall() + ",-");
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

}/* */