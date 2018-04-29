package com.example.jasim.plateup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.settings.SettingsRest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.example.jasim.plateup.BaseSampleActivity;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class PostContent extends BaseSampleActivity implements IPickResult {

    /* private ImageView mSelectImage; */
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
        dialog = new AlertDialog.Builder(this)
                .setTitle("Select allergens")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
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

        mPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(PostContent.this);
                alertD.show();
            }
        });

        allergies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(PostContent.this);
                dialog.show();
                if(globaldialog != null) {
                    AlertDialog d = (AlertDialog) globaldialog;
                    ListView v2 = d.getListView();
                    for(int i = 0; i < itemsPosition.size(); i++) {
                        v2.setItemChecked(i, false);
                    }

                    for(int i = 0; i < selectedItems.size(); i++) {
                        v2.setItemChecked(itemsPosition.indexOf(selectedItems.get(i)),true);
                    }

                    if (selectedItems.size() == 1) {
                        System.out.println(selectedItems.get(0));
                    }
                }
            }
        });



        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("offers");


        mContentName = findViewById(R.id.dishField);
        mContentPrice = findViewById(R.id.price);
        mContentDesc = findViewById(R.id.descriptionField);
        mSubmitButton = findViewById(R.id.contentSubmit);





        mProgress = new ProgressDialog(this);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }

        });
        /* mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Legger til bildet..");
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getApplicationContext());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
                mPictureError.setVisibility(View.GONE);

            }
        }); */
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowImage.setImageURI(null);
                mDelete.setVisibility(View.GONE);
            }
        });


    }

    @Override
    protected void onImageViewClick() {
        hideKeyboard(PostContent.this);
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
        else if(!TextUtils.isEmpty(dishName) && !TextUtils.isEmpty(price) && (quantityBtn.isChecked()) || (fixedBtn.isChecked() || (periodBtn.isChecked()))) {

            mProgress.show();
            final DatabaseReference newPost = mDatabase.push();
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
            newPost.child("id").setValue(key);
            selectedItems.clear();

            mProgress.dismiss();

            startActivity(new Intent(PostContent.this, MainRActivity.class));
        }
    }
    @Override
    public void onPickResult(final PickResult r) {
        if (r.getError() == null) {


            //r.setBitmap(Bitmap.createScaledBitmap(r.getBitmap(), 600, 300, false));


            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //r.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), r.getBitmap(), "Title", null);

            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            getImageView().setImageURI(null);

            //Setting the real returned image.
            getImageView().setImageURI(Uri.parse(path));
            mImageUri = Uri.parse(path);
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

    private Bitmap scaleBitmap(Bitmap bm, int maxWidth, int maxHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        System.out.println("Pictures" + "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int)(width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        System.out.println("Pictures" + "Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
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



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
