package com.example.jasim.plateup.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.Check.Connection;
import com.example.jasim.plateup.Content;
import com.example.jasim.plateup.MainUActivity;
import com.example.jasim.plateup.ProfileRestaurant;
import com.example.jasim.plateup.R;
import com.example.jasim.plateup.Restaurant;
import com.example.jasim.plateup.RestaurantModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by jasim on 02.01.2018.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private ArrayList<RestaurantModel> allContent = new ArrayList<>();
    private ArrayList<RestaurantModel> shownContent = new ArrayList<>();
    private Context ctx;
    private Location location;
    private boolean showItem;

    public RestaurantAdapter(Context ctx, Location location) {
        this.ctx = ctx;
        this.location = location;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mRestaurantName;
        private TextView mCategory;
        private TextView mAddress;
        private ImageView mImage;
        private RelativeLayout mRestLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mRestLayout = itemView.findViewById(R.id.restLayout);
            mRestaurantName = itemView.findViewById(R.id.restaurant_name);
            mCategory = itemView.findViewById(R.id.category_field);
            mAddress = itemView.findViewById(R.id.address_field);
            mImage = itemView.findViewById(R.id.content_image);
        }
    }

    public void clearItems() {
        shownContent.clear();
        allContent.clear();
        notifyDataSetChanged();
    }
    public void addItem(final RestaurantModel content) {
        System.out.println("Legger til items paa nytt.");
        showItem = true;
        final ArrayList<Content> basket = MainUActivity.getBasketList();
        final RestaurantModel model = content;
        boolean closed = true;
        showItem = true;
        Calendar c = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date toDays = new Date();
        try {
            toDays = format.parse(MainUActivity.toDaysDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(toDays);


        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int currentTime = Integer.parseInt(MainUActivity.time);
        System.out.println("Time: " + MainUActivity.time);
        String whichOpening = "first";
        for(int i = 0; i < Integer.parseInt(model.getAmountOpenings())+1; i++) {
            if(i == 1) {
                whichOpening = "second";
            }
            else if(i == 2) {
                whichOpening = "third";
            }
            if(model.getOpenings().get(whichOpening) != null) {
                int openDay = Integer.parseInt(model.getOpenings().get(whichOpening).getFromDay());
                int closeDay = Integer.parseInt(model.getOpenings().get(whichOpening).getToDay());
                // Det vanlige tilfellet hvor fromDay er mindre enn toDay, da maa vi bare sjekke om vi befinner oss mellom.
                if (openDay < closeDay) {
                    if (dayOfWeek >= openDay && dayOfWeek <= closeDay) {
                        break;
                    }
                }
                // Ikke vanlige tilfellet hvor fromDay er storre en toDay.
                else {
                    if (dayOfWeek >= openDay && dayOfWeek >= closeDay || dayOfWeek <= openDay && dayOfWeek <= closeDay) {
                        break;
                    }
                }
            }
            else {
                whichOpening = "closed";
                System.out.println("WhichOpening: Vi er stengt!");
            }
        }
        int openTime = 0;
        int closeTime = 0;

        // Tester om lokken har gaatt over 3 ganger hvis den har det, skjuler vi i feed.
        if(whichOpening.equals("closed")) {
            System.out.println("WhichOpening: Vi er stengt!");
            allContent.add(content);
            showItem = false;
        }
        // Hvis ikke, legger vi inn open og close time for den dagen.
        else {
            openTime = Integer.parseInt(model.getOpenings().get(whichOpening).getOpen());
            closeTime = Integer.parseInt(model.getOpenings().get(whichOpening).getClose());
        }
        // Tester bare om den ikke har gaatt 3 ganger
        if(!whichOpening.equals("closed")) {
            DateFormat formatter = new SimpleDateFormat("HHmm");
            Date openDate = new Date();
            Date closeDate = new Date();
            try {
                openDate = formatter.parse(model.getOpenings().get(whichOpening).getOpen());
                closeDate = formatter.parse(model.getOpenings().get(whichOpening).getClose());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            Calendar calendar = Calendar.getInstance();
            calendar.setTime(openDate);
            calendar.add(Calendar.MINUTE, +30);
            String openString = formatter.format(calendar.getTime());
            calendar.setTime(closeDate);
            calendar.add(Calendar.MINUTE, -15);
            String closeString = formatter.format(calendar.getTime());
            openTime = Integer.parseInt(openString);
            closeTime = Integer.parseInt(closeString);
            System.out.println("WhichOpening: " + openTime + " " + closeTime);

            if (openTime < closeTime && !whichOpening.equals("closed")) {
                // Restauranten er stengt fordi status er closed
                if (model.getStatus().equals("closed")) {
                    allContent.add(content);
                    showItem = false;
                }
                // Restauranten er open.
                else if (currentTime > openTime && currentTime < closeTime) {

                }
                else {
                    allContent.add(content);
                    showItem = false;
                }
            } else if (openTime > closeTime && !whichOpening.equals("closed")) {
                System.out.println(model.getUsername());
                if (model.getStatus().equals("closed")) {
                    allContent.add(content);
                    showItem = false;
                }
                // Tester om restauranten er open. Inni er en if som sjekker et spesialtilfelle.
                else if ((currentTime > openTime && currentTime > closeTime) || (currentTime < openTime && currentTime < closeTime)) {
                    System.out.println("Open");
                }
                else {
                    allContent.add(content);
                    showItem = false;
                }
            }
        }
        System.out.println("showItem: " + content);
        allContent.add(content);
        if(showItem) {
            shownContent.add(content);

            for (RestaurantModel con : shownContent) {
                Location targetLocation = new Location("");
                targetLocation.setLatitude(model.getLatitude());
                targetLocation.setLongitude(model.getLongitude());

                if (location != null) {
                    System.out.println("DistanceTo rest: " + location.distanceTo(targetLocation));
                    content.setDistanceTo(location.distanceTo(targetLocation));

                    System.out.println("Vi har lokasjon!");
                } else {
                    content.setDistanceTo(0);
                }
            }
            Collections.sort(shownContent);
            notifyDataSetChanged();
            for(RestaurantModel con : shownContent) {
                System.out.println("Rearrengement 2: " + con.getUsername());
            }
            System.out.println("Position toString: " + allContent);
        }

        notifyDataSetChanged();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.restaurant_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RestaurantModel model = shownContent.get(position);
        holder.mRestaurantName.setText(model.getUsername());
        holder.mRestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(new Connection(ctx).getConnected()) {
                    ctx.startActivity(new Intent(ctx, ProfileRestaurant.class).putExtra("restaurant", model.getId()));
                }
                else {
                    Toast.makeText(ctx, "Please enable internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.mCategory.setText(model.getCategory());
        holder.mAddress.setText(model.getAddress());
    }

    public int getItemCount() {
        return shownContent.size();
    }
}
