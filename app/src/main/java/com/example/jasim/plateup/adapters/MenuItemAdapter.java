package com.example.jasim.plateup.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jasim.plateup.Content;
import com.example.jasim.plateup.MainUActivity;
import com.example.jasim.plateup.ProfileRestaurant;
import com.example.jasim.plateup.R;
import com.example.jasim.plateup.RestaurantModel;
import com.example.jasim.plateup.YourOrders;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MenuItemAdapter extends Adapter<MenuItemAdapter.ViewHolder> {
    private ArrayList<Content> mDataset;
    private RestaurantModel modelO;
    private Context context;
    private Activity activity;
    private String restId;
    private Content content;
    private DatabaseReference mDatabase;

    public MenuItemAdapter(final ArrayList<Content> dataset, Context context, Activity activity, String restId) {
        mDataset = dataset;
        this.context = context;
        this.activity = activity;
        this.restId = restId;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(restId);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                modelO = dataSnapshot.getValue(RestaurantModel.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Collections.sort(mDataset);
    }


    // Not use static
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDishNumber;
        public TextView mDishName;
        public TextView mTypeField;
        public TextView mPrice;
        public TextView mDescription;
        public Button mAllergens;
        public Button eat_it;

        public void setImage(Context ctx, String image) {
            ImageView content_image = itemView.findViewById(R.id.content_image);
            content_image.setVisibility(View.VISIBLE);
            Picasso.with(ctx).load(image).into(content_image);
        }

        public ViewHolder(View itemView) {
            super(itemView);
            mDishNumber = itemView.findViewById(R.id.menu_number);
            mDishName = itemView.findViewById(R.id.menu_dish);
            mTypeField = itemView.findViewById(R.id.pqField);
            mPrice = itemView.findViewById(R.id.menu_price);
            mDescription = itemView.findViewById(R.id.menu_description);
            mAllergens = itemView.findViewById(R.id.allergies);
            eat_it = itemView.findViewById(R.id.eat_it);

        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(mDataset.get(position).getImage() != null) {
            holder.setImage(context, mDataset.get(position).getImage());
        }
        holder.mDishNumber.setText(mDataset.get(position).getDishnumber());
        holder.mDishName.setText(mDataset.get(position).getDishname());
        holder.mPrice.setText(mDataset.get(position).getPrice());
        holder.mDescription.setText(mDataset.get(position).getDescription());


        if(mDataset.get(position).getType() == null) {
            System.out.println("ingen");
        }
        else if(mDataset.get(position).getType().equals("2")) {
            holder.mTypeField.setText("Every: " + mDataset.get(position).getEvery());
        }
        else if(mDataset.get(position).getType().equals("3")) {
            holder.mTypeField.setText("Period: " + mDataset.get(position).getPeriod());
        }
        else if(mDataset.get(position).getType().equals("1")) {
            holder.mTypeField.setText("Quantity: " + mDataset.get(position).getQuantity());
        }
        setEatIt(mDatabase, mDataset.get(position), holder, holder.eat_it, holder.mAllergens);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.menu_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    private void setEatIt(final DatabaseReference mRestModel, Content inModel, ViewHolder viewHolder, final Button eat_it, final Button mAllergens) {
        final Content model = inModel;
        final ViewHolder viewHolder2 = viewHolder;
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //mShowOpen = (TextView) findViewById(R.id.open_time);
        mRestModel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(mAuth.getCurrentUser() != null) {
                    System.out.println("Mauth: " + mAuth + " mAuth.getCurrent: " + mAuth.getCurrentUser());
                    modelO = dataSnapshot.getValue(RestaurantModel.class);
                    if (mAuth.getCurrentUser().getUid().equals(modelO.getId())) {
                        eat_it.setText("Update");
                        if (model.getDishnumber() != null) {
                            eat_it.setVisibility(View.GONE);
                        }
                        eat_it.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ProfileRestaurant.updateContent(model.getId());
                            }
                        });

                        //viewHolder2.mView.findViewById(R.id.content_row).setOnClickListener(new View.OnClickListener() {
                        //  @Override
                        // public void onClick(View v) {
                        //      startActivity(new Intent(context, EditContent.class).putExtra("offer_id", model.getId()));
                        //}
                        //});
                    }
                }
                mAllergens.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Intent myIntent = new Intent(view.getContext(), agones.class);
                        //startActivityForResult(myIntent, 0);
                        ProfileRestaurant.showAlert(model, activity);
                    }
                });

                boolean closed = true;
                if(mAuth.getCurrentUser() != null) {
                    if (!mAuth.getCurrentUser().getUid().equals(modelO.getId())) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(MainUActivity.day);
                        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                        int currentTime = Integer.parseInt(MainUActivity.time);
                        String whichOpening = "first";
                        for (int i = 0; i < Integer.parseInt(modelO.getAmountOpenings()) + 1; i++) {
                            if (i == 1) {
                                whichOpening = "second";
                            } else if (i == 2) {
                                whichOpening = "third";
                            } else if (i == 3) {
                                whichOpening = "closed";
                                break;
                            }
                            int openDay = Integer.parseInt(modelO.getOpenings().get(whichOpening).getFromDay());
                            int closeDay = Integer.parseInt(modelO.getOpenings().get(whichOpening).getToDay());
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
                        int openTime = 0;
                        int closeTime = 0;
                        DateFormat formatter = new SimpleDateFormat("HHmm");
                        Date openDate = new Date();
                        Date closeDate = new Date();
                        // Tester om lokken har gaatt over 3 ganger hvis den har det, skjuler vi i feed.
                        if (whichOpening.equals("closed")) {
                            eat_it.setVisibility(View.GONE);
                        }
                        // Hvis ikke, legger vi inn open og close time for den dagen.
                        else {
                            try {
                                openDate = formatter.parse(modelO.getOpenings().get(whichOpening).getOpen());
                                closeDate = formatter.parse(modelO.getOpenings().get(whichOpening).getClose());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
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
                        // Tester bare om den ikke har gaatt 3 ganger.
                        if (openTime < closeTime && !whichOpening.equals("closed")) {

                            if (modelO.getStatus().equals("closed")) {
                                eat_it.setVisibility(View.GONE);
                                closed = false;

                            } else if (currentTime > openTime && currentTime < closeTime) {
                                setRestSaleType(viewHolder2, model, dayOfWeek);
                                closed = false;
                                eat_it.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (model.getChoose() != null) {
                                            ProfileRestaurant.showChoose(model, modelO, restId, activity);
                                        } else {
                                            ProfileRestaurant.addToCart(modelO, model, restId);
                                        }
                                    }
                                });
                            }
                        } else if (openTime > closeTime && !whichOpening.equals("closed")) {
                            if (modelO.getStatus().equals("closed")) {
                                eat_it.setVisibility(View.GONE);
                                closed = false;
                            } else if ((currentTime > openTime && currentTime > closeTime) || (currentTime < openTime && currentTime < closeTime)) {
                                setRestSaleType(viewHolder2, model, dayOfWeek);
                                closed = false;
                                eat_it.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (model.getChoose() != null) {
                                            int position = -1;
                                            System.out.println("extra_info: " + model.getExtraInfo());
                                            for(int i = 0; i < MainUActivity.getBasketList().size(); i++) {
                                                if(MainUActivity.getBasketList().get(i).getId().equals(model.getId())) {
                                                   position = i;
                                                   break;
                                                }
                                            }
                                            if(position != -1) {
                                                ProfileRestaurant.showChoose(MainUActivity.getBasketList().get(position), modelO, restId, activity);
                                            }
                                            else {
                                                ProfileRestaurant.showChoose(model, modelO, restId, activity);
                                            }
                                        } else {
                                            ProfileRestaurant.addToCart(modelO, model, restId);
                                        }
                                    }
                                });
                            }
                        }
                        if (closed) {
                            eat_it.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }

    public void setRestSaleType(ViewHolder viewHolder, Content model, int dayOfWeek) {
        Button eat_it = viewHolder.itemView.findViewById(R.id.eat_it);
        if(model.getType() != null) {
            switch (model.getType()) {
                case "1":
                    if (model.getQuantity().equals("0")) {
                        eat_it.setVisibility(View.GONE);
                    }
                    break;
                case "2":
                    int day_of_week = convertDaytoNumber(model.getEvery());
                    if (day_of_week != dayOfWeek) {
                        eat_it.setVisibility(View.GONE);
                    }
                    break;
                case "3":
                    SimpleDateFormat after = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date newDate = after.parse(model.getPeriod());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(newDate);
                        calendar.add(Calendar.HOUR_OF_DAY, 23);
                        calendar.add(Calendar.MINUTE, 59);
                        newDate = calendar.getTime();

                        if (MainUActivity.day.after(newDate)) {
                            eat_it.setVisibility(View.GONE);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public int convertDaytoNumber(String day) {
        if(day.equals("Monday")) {
            return 2;
        }
        else if(day.equals("Tuesday")) {
            return 3;
        }
        else if(day.equals("Wednesday")) {
            return 4;
        }
        else if(day.equals("Thursday")) {
            return 5;
        }
        else if(day.equals("Friday")) {
            return 6;
        }
        else if(day.equals("Saturday")) {
            return 7;
        }
        else if(day.equals("Sunday")) {
            return 1;
        }
        return 0;
    }
}