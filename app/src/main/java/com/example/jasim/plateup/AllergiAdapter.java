package com.example.jasim.plateup;

import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.example.jasim.plateup.PostContent.adapter;

public class AllergiAdapter extends RecyclerView.Adapter<AllergiAdapter.ContentClickViewHolder> {




    public class ContentClickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView allergi;

        ContentClickViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            allergi = (TextView) itemView.findViewById(R.id.allergi_item);
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

    private ArrayList<String> mCustomObjects;

    public AllergiAdapter(ArrayList<String> mCustomObjects) {
        this.mCustomObjects = mCustomObjects;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return mCustomObjects.size();
    }


    @Override
    public ContentClickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allergi_row, parent, false);
        return new ContentClickViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ContentClickViewHolder holder, int position) {
        final String object = mCustomObjects.get(position);
        holder.allergi.setText(object);
        holder.allergi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomObjects.remove(object);
                //PostContent.selectedItems.remove(object);
                //PostContent.adapter.notifyDataSetChanged();
                //PostContent.dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                notifyDataSetChanged();
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

}/* */