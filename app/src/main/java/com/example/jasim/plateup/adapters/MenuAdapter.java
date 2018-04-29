package com.example.jasim.plateup.adapters;

/**
 * Created by jasim on 10.10.2017.
 */

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jasim.plateup.Content;
import com.example.jasim.plateup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuAdapter extends PagerAdapter {

    private RecyclerView recycler;
    private HashMap<Integer, String> categories;
    private HashMap<Integer, HashMap<String, Content>> dishes;
    private Context context;
    private Activity activity;
    private String restId;
    private ArrayList<Content> offers;

    public MenuAdapter(ArrayList<Content> offers, HashMap<Integer, HashMap<String, Content>> dishes, HashMap<Integer, String> categories, Context context, Activity activity, String restId) {
        this.offers = offers;
        this.categories = categories;
        this.context = context;
        this.activity = activity;
        this.dishes = dishes;
        this.restId = restId;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.layout_page, container, false);

        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) container.getLayoutParams();
        params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        view.requestLayout();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(restId);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                container.addView(view);
                ArrayList<Content> dish = new ArrayList<>();
                if(position == 0) {
                    for(Content content : offers) {
                        dish.add(content);
                    }
                }
                else {
                    for (Object value : dishes.get(position).values()) {
                        Content temp = new Content();
                        if(!(value instanceof Boolean)) {
                            for (Object value2 : ((HashMap<String, Object>) value).values()) {
                                temp.setData(value2);
                            }
                            dish.add(temp);
                        }
                    }
                }
                MenuItemAdapter menuItemAdapter = new MenuItemAdapter(dish, context, activity, restId);
                RecyclerView menuRecycler = view.findViewById(R.id.dishes);
                menuRecycler.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                menuRecycler.setLayoutManager(linearLayoutManager);
                menuRecycler.setAdapter(menuItemAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /*TextView dishnumber = new TextView(context);
        TextView dishname = new TextView(context);
        TextView price = new TextView(context);
        TextView description = new TextView(context);*/


/*      LinearLayout menuItem = view.findViewById(R.id.linear);
        menuItem.addView(dishnumber);
        menuItem.addView(dishname);
        menuItem.addView(price);
        menuItem.addView(description);*/
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public String getPageTitle(int position) {
        return categories.get(position);
    }

}
