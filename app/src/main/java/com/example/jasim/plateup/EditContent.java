package com.example.jasim.plateup;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class EditContent extends BaseSampleActivity implements IPickResult {

    private ImageView mShowImage;
    private EditText mContentName;
    private EditText mContentPrice;
    private EditText mContentDesc;
    private RecyclerView recyclerView;
    public static AllergiAdapter adapter;
    private Button mSubmitButton;
    private Button allergies;
    private Button mPeriod;
    private Button mOk;
    private Button mCancel;
    private Button mDelete;
    private RadioButton quantityBtn;
    private RadioButton fixedBtn;
    private RadioButton periodBtn;
    static AlertDialog dialog;
    private DialogInterface globaldialog;
    private ArrayAdapter<CharSequence> dayAdapter;
    private TextView periodField;
    private TextView quantityField;
    private Spinner fixedField;
    private TextView mPeriodText;
    private int choice;
    private int storedChoice;
    private int dayMain, monthMain, yearMain;
    private TextView mPictureError;

    private static final int PICK_IMAGE_ID = 234;


    private TextView allergies0, allergies1, allergies2, allergies3, allergies4, allergies5, allergies6,
            allergies7, allergies8, allergies9, allergies10, allergies11, allergies12, allergies13, allergies14;

    private Uri mImageUri = null;
    private ProgressDialog mProgress;

    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStorage;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final String[] items = {"Gluten","Shellfish","Egg","Fish","Peanuts","Soy","Milk","Nuts","Celery","Mustard","Sesame seeds","Lupine","Molluscs", "Sulphur dioxide", "Sulphites"};
    private ArrayList<String> itemsPosition = new ArrayList<>(Arrays.asList(items));

    // arraylist to keep the selected items
    public static final ArrayList<String> selectedItems = new ArrayList<>();




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = (RecyclerView) findViewById(R.id.allerg_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AllergiAdapter(selectedItems);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        //adapter.notifyDataSetChanged();
        mPeriod = (Button)findViewById(R.id.period);
        allergies = (Button)findViewById(R.id.allergies);
        mPictureError = (TextView)findViewById(R.id.choose_picture_error);
        final ArrayList<String> indexs = new ArrayList<>();
        final ArrayList<String> removedindexs = new ArrayList<>();

        dayAdapter = ArrayAdapter.createFromResource(this, R.array.days_array, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View promptView = layoutInflater.inflate(R.layout.period, null);
        final AlertDialog alertD = new AlertDialog.Builder(this).create();

        // EditText userInput = (EditText) promptView.findViewById(R.id.userInput);

        mShowImage = findViewById(R.id.result_image);
        quantityBtn = (RadioButton) promptView.findViewById(R.id.btnQuantity);
        quantityField = (TextView)promptView.findViewById(R.id.quantityField);
        fixedBtn = (RadioButton) promptView.findViewById(R.id.btnFixed);
        fixedField = (Spinner)promptView.findViewById(R.id.fixedField);
        fixedField.setAdapter(dayAdapter);
        periodBtn = (RadioButton) promptView.findViewById(R.id.btnPeriod);
        periodField = (TextView)promptView.findViewById(R.id.periodField);
        mOk = (Button)promptView.findViewById(R.id.ok);
        mCancel = (Button)promptView.findViewById(R.id.cancel);
        mPeriodText = (TextView)findViewById(R.id.periodText);
        mDelete = (Button)findViewById(R.id.delete_picture);
        mDelete.setVisibility(View.VISIBLE);

        quantityBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fixedField.setVisibility(View.GONE);
                quantityField.setVisibility(View.VISIBLE);
                periodField.setVisibility(View.GONE);
                choice = 1;
            }
        });

        fixedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fixedField.setVisibility(View.VISIBLE);
                quantityField.setVisibility(View.GONE);
                periodField.setVisibility(View.GONE);
                choice = 2;
            }
        });

        periodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fixedField.setVisibility(View.GONE);
                quantityField.setVisibility(View.GONE);
                showDatePickerDialog(promptView);
                choice = 3;
            }
        });

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Choice: " + choice);
                if(choice == 1) {
                    mPeriodText.setText("Quantity: " + quantityField.getText().toString().trim());
                }
                else if(choice == 2) {
                    mPeriodText.setText("Every: " + fixedField.getSelectedItem().toString().trim());
                }
                else if(choice == 3) {
                    mPeriodText.setText("Period: " + periodField.getText().toString().trim());
                }
                storedChoice = choice;
                alertD.dismiss();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storedChoice == 1) {
                    quantityBtn.performClick();
                }
                else if(storedChoice == 2){
                    fixedBtn.performClick();
                }
                else if(storedChoice == 3) {
                    periodBtn.performClick();
                }
                alertD.dismiss();
            }
        });

        alertD.setView(promptView);

        mPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertD.show();
            }
        });

        allergies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                if(globaldialog != null) {

                    AlertDialog d = (AlertDialog) globaldialog;
                    ListView v2 = d.getListView();
                    v2.setItemChecked(0, false);
                    for(int i = 0; i < itemsPosition.size(); i++) {
                        v2.setItemChecked(i, true);
                    }

                    for(int i = 0; i < selectedItems.size(); i++) {
                        v2.setItemChecked(itemsPosition.indexOf(selectedItems.get(i)),true);
                    }

                    if (selectedItems.size() == 1) {
                        System.out.println(selectedItems.get(0));
                    }
                }
            }
        });/* */



        mStorage = FirebaseStorage.getInstance().getReference();
        Intent intent = getIntent();
        String id = intent.getStringExtra("offer_id");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("offers").child(id);

        mContentName = (EditText) findViewById(R.id.dishField);
        mContentPrice = (EditText) findViewById(R.id.price);
        mContentDesc = (EditText) findViewById(R.id.descriptionField);
        mSubmitButton = (Button) findViewById(R.id.contentSubmit);
        mSubmitButton.setText("Update");

        mProgress = new ProgressDialog(this);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowImage.setImageURI(null);
                mShowImage.setImageResource(R.mipmap.default_image);
                mDelete.setVisibility(View.GONE);
            }
        });

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Content model = dataSnapshot.getValue(Content.class);
                selectedItems.clear();
                mContentName.setText(model.getDishname());
                mContentDesc.setText(model.getDescription());
                mContentPrice.setText(model.getPrice());
                if(model.getType().equals("1")) {
                    mPeriodText.setText("Quantity: " + model.getQuantity());
                }
                else if(model.getType().equals("2")) {
                    mPeriodText.setText("Every: " + model.getEvery());
                }
                else if(model.getType().equals("3")) {
                    mPeriodText.setText("Until: " +model.getPeriod());
                }
                mPeriodText.setVisibility(View.VISIBLE);

                if(model.getAllergies().containsKey("Gluten")) {
                    selectedItems.add("Gluten");
                }
                if(model.getAllergies().containsKey("Shellfish")) {
                    selectedItems.add("Shellfish");
                }
                if(model.getAllergies().containsKey("Egg")) {
                    selectedItems.add("Egg");
                }
                if(model.getAllergies().containsKey("Fish")) {
                    selectedItems.add("Fish");
                }
                if(model.getAllergies().containsKey("Peanuts")) {
                    selectedItems.add("Peanuts");
                }
                if(model.getAllergies().containsKey("Soy")) {
                    selectedItems.add("Soy");
                }
                if(model.getAllergies().containsKey("Milk")) {
                    selectedItems.add("Milk");
                }
                if(model.getAllergies().containsKey("Nuts")) {
                    selectedItems.add("Nuts");
                }
                if(model.getAllergies().containsKey("Celery")) {
                    selectedItems.add("Celery");
                }
                if(model.getAllergies().containsKey("Mustard")) {
                    selectedItems.add("Mustard");
                }
                if(model.getAllergies().containsKey("Sesame seeds")) {
                    selectedItems.add("Sesame seeds");
                }
                if(model.getAllergies().containsKey("Lupine")) {
                    selectedItems.add("Lupine");
                }
                if(model.getAllergies().containsKey("Molluscs")) {
                    selectedItems.add("Molluscs");
                }
                if(model.getAllergies().containsKey("Sulphur dioxide")) {
                    selectedItems.add("Sulphur dioxide");
                }
                if(model.getAllergies().containsKey("Sulphites")) {
                    selectedItems.add("Sulphites");
                }
                System.out.println("Items: " + selectedItems);
                int L = items.length;
                boolean[] b2 = new boolean[L];
                for(int i=0 ; i<L ; i++){
                    if(selectedItems.contains(items[i])) {
                        b2[i] = true;
                        System.out.println("Items ++");
                    }
                    else {
                        b2[i] = false;
                        System.out.println("Items --");
                    }
                }

                dialog = new AlertDialog.Builder(EditContent.this)
                        .setTitle("Select allergens")
                        .setMultiChoiceItems(items, b2, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if (isChecked) {
                                    AlertDialog d = (AlertDialog) dialog;
                                    ListView v = d.getListView();

                                    globaldialog = d;
                                    if(!indexs.contains(Integer.toString(indexSelected))) {
                                        indexs.add(Integer.toString(indexSelected));
                                    }

                                    // If the user checked the item, add it to the selected items


                                }
                                else if(selectedItems.contains(items[indexSelected])) {
                                    removedindexs.add(Integer.toString(indexSelected));
                                }
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                for(int i = 0; i < removedindexs.size(); i++) {
                                    selectedItems.remove(itemsPosition.get(Integer.parseInt(removedindexs.get(i))));
                                }
                                for(int i = 0; i < indexs.size(); i++) {
                                    selectedItems.add(itemsPosition.get(Integer.parseInt(indexs.get(i))));
                                }
                                adapter.notifyDataSetChanged();
                                removedindexs.clear();
                                indexs.clear();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                removedindexs.clear();
                                indexs.clear();
                            }
                        }).create();


                adapter.notifyDataSetChanged();
                System.out.println("GetImage: " + model.getImage());
                if(model.getImage() != null) {
                    mImageUri = Uri.parse(model.getImage());
                }
                System.out.println("mImageUri: " + mImageUri);
                Picasso.with(getApplicationContext()).load(model.getImage()).into(mShowImage);

                if(model.getType().equals("1")) {
                    quantityBtn.performClick();
                    quantityField.setText(model.getQuantity());
                }
                else if(model.getType().equals("2")) {
                    fixedBtn.setChecked(true);
                    fixedField.setSelection(1);
                }
                else if(model.getType().equals("3")){
                    periodBtn.setChecked(true);
                    periodField.setText(model.getPeriod());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onImageViewClick() {
        System.out.println("Clicking");
        PickSetup setup = new PickSetup();

        PickImageDialog.build(setup)
                //.setOnClick(this)
                .show(this);

        //If you don't have an Activity, you can set the FragmentManager
        /*PickImageDialog.build(setup, new IPickResult() {
            @Override
            public void onPickResult(PickResult r) {
                r.getBitmap();
                r.getError();
                r.getUri();
            }
        }).show(getSupportFragmentManager());*/

        //For overriding the click events you can do this
        /*PickImageDialog.build(setup)
                .setOnClick(new IPickClick() {
                    @Override
                    public void onGalleryClick() {
                        Toast.makeText(SampleActivity.this, "Gallery Click!", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCameraClick() {
                        Toast.makeText(SampleActivity.this, "Camera Click!", Toast.LENGTH_LONG).show();
                    }
                }).show(this);*/
    }

    private void startPosting() {

        mProgress.setMessage("Adding content ...");

        final String dishName = mContentName.getText().toString().trim();
        final String price = mContentPrice.getText().toString().trim();
        final String description = mContentDesc.getText().toString().trim();
        final String quantity = quantityField.getText().toString().trim();
        final String every = fixedField.getSelectedItem().toString().trim();
        final String dayField = Integer.toString(dayMain);
        final String monthField = Integer.toString(monthMain);
        final String yearField = Integer.toString(yearMain);
        final String date = dayField + "/" + monthField + "/" + yearField;
        if(TextUtils.isEmpty(dishName)) {
            mContentName.setError("Please fill in the dish name!");
        }
        else if(TextUtils.isEmpty(description)) {
            mContentDesc.setError("Please fill in a description");
        }
        else if(!(quantityBtn.isChecked()) && !(fixedBtn.isChecked()) && !(periodBtn.isChecked())) {
            mPeriod.setError("Please select one sale type");
        }
        else if(TextUtils.isEmpty(price)) {
            mContentPrice.setError("Please fill in the price for the dish!");
        }
//        else if(mImageUri == null) {
//            Toast.makeText(this, "Velg et bilde", Toast.LENGTH_SHORT).show();
//        }
        else if(!TextUtils.isEmpty(dishName) && !TextUtils.isEmpty(price) && (quantityBtn.isChecked()) || (fixedBtn.isChecked() || (periodBtn.isChecked()))) {

            mProgress.show();
            final DatabaseReference newPost = mDatabase;
            String key = newPost.getKey();
            if(mImageUri != null) {
                StorageReference filepath = mStorage.child("Offer_Pictures").child(key);
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        if (downloadUrl != null) {
                            newPost.child("image").setValue(downloadUrl.toString());
                        }
                    }
                });
            }
            HashMap<String, String> allergens = new HashMap<String, String>();
            for (String allergen : selectedItems) {
                allergens.put(allergen, "1");
            }
            newPost.child("allergies").setValue(allergens);
            newPost.child("dishname").setValue(dishName);
            newPost.child("price").setValue(price);
            newPost.child("description").setValue(description);
            for(int i = 0; i < selectedItems.size(); i++) {
                newPost.child("allergies").child(selectedItems.get(i)).setValue("1");
            }
            if(choice == 1) {
                newPost.child("quantity").setValue(quantity);
                newPost.child("type").setValue("1");
            }
            else if(choice == 2) {
                newPost.child("every").setValue(every);
                newPost.child("type").setValue("2");
            }
            else if(choice == 3) {
                newPost.child("period").setValue(date);
                newPost.child("type").setValue("3");
            }
            newPost.child("rest_id").setValue(mAuth.getCurrentUser().getUid());
            selectedItems.clear();

            mProgress.dismiss();

            startActivity(new Intent(EditContent.this, MainRActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("kommer inn i den forste");
        if(resultCode == RESULT_OK) {
            System.out.println("kommer inn i den andre");
            Uri imageUri = ImagePicker.getImageFromResult(this, resultCode, data);
            mImageUri = ImagePicker.getImageFromResult(this, resultCode, data);
            mShowImage.setImageURI(imageUri);
            mDelete.setVisibility(View.VISIBLE);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPickResult(final PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            getImageView().setImageURI(null);

            //Setting the real returned image.
            getImageView().setImageURI(r.getUri());
            mImageUri = r.getUri();
            System.out.println("FUNKER DET? " + r.getUri());

            //If you want the Bitmap.
            getImageView().setImageBitmap(r.getBitmap());

            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }

        /* scrollToTop(); */
    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePicker = new DatePickerDialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog, this, year, month, day);
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            return datePicker;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            yearMain = year;
            monthMain = month+1;
            dayMain = day;
            periodField.setVisibility(View.VISIBLE);
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
            periodField.setText(timeStamp + " - " + day + "/" + String.format("%02d", month+1) + "/" + year);
        }

    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
