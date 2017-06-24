package com.example.android.inventory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract;

import java.io.File;

import static com.example.android.inventory.MainActivity.LOADER_ID;
import static com.example.android.inventory.R.id.email_text;
import static com.example.android.inventory.R.id.price_text;


public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //track editing
    private boolean mItemHasChanged = false;
    private EditText typeEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private EditText emailEditText;
    private TextView quantityTextView;
    private ImageView imageView;
    private int quantity = -1;
    private String reorderEmail = "";
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };
    private Uri mContentUri;
    private String imagePath;
    private static final String LOG_TAG = EditActivity.class.getSimpleName();
    private static final int RESULT_LOAD_IMAGE = 100;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //check edit vs. new intent
        Intent intent = getIntent();
        mContentUri = intent.getData();
        if (mContentUri == null) {
            setTitle(getString(R.string.edit_activity_new_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_activity));
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        //find all views
        typeEditText = (EditText) findViewById(R.id.type);
        descriptionEditText = (EditText) findViewById(R.id.description);
        priceEditText = (EditText) findViewById(price_text);
        emailEditText = (EditText) findViewById(email_text);
        quantityTextView = (TextView) findViewById(R.id.quantity);
        Button plusButton = (Button) findViewById(R.id.plus_button);
        Button minusButton = (Button) findViewById(R.id.minus_button);
        Button reorderButton = (Button) findViewById(R.id.reorder_button);
        Button imageButton = (Button) findViewById(R.id.image_button);

        //set touch listener method to edit fields and for buttons
        typeEditText.setOnTouchListener(mTouchListener);
        descriptionEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        emailEditText.setOnTouchListener(mTouchListener);
        plusButton.setOnTouchListener(mTouchListener);
        minusButton.setOnTouchListener(mTouchListener);
        imageButton.setOnTouchListener(mTouchListener);

        //ask for permission to read from Gallery
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }

        //button wiring for "add new item"
        if (mContentUri == null) {
            quantityTextView.setText(getResources().getString(R.string.quantity) + "\n" + "");
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    quantity += 1;
                    quantityTextView.setText(getResources().getString(R.string.quantity) + "\n" + Integer.toString(quantity));
                }
            });
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (quantity > 0){
                        quantity -= 1;
                        quantityTextView.setText(getResources().getString(R.string.quantity) + "\n" + Integer.toString(quantity));
                    }
                }
            });
            reorderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reorderEmail = emailEditText.getText().toString().trim();
                    Intent reorder = new Intent(Intent.ACTION_SENDTO);
                    reorder.setData(Uri.parse("mailto:" + reorderEmail));
                    try {
                        startActivity(reorder);
                    } catch (ActivityNotFoundException e) {
                        Log.e(LOG_TAG, "could not send email");
                        Toast.makeText(EditActivity.this, getString(R.string.no_email), Toast.LENGTH_LONG).show();
                    }
                }
            });
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.setType("image/*");
                    try {
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                    } catch (ActivityNotFoundException e) {
                        Log.e(LOG_TAG, "could not find gallery");
                        Toast.makeText(EditActivity.this, getString(R.string.no_gallery), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //receiving Uri for image from Gallery Intent on Image Button Click
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri;
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImageUri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndex(projection[0]);
            imagePath = cursor.getString(column_index);
            cursor.close();
            //show selected image
            File imageFile = new File(imagePath);
            if (imageFile.exists()){
                //Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
                //imageView.setImageBitmap(imageBitmap);
                imageView = (ImageView) findViewById(R.id.image_view);
                imageView.setImageURI(Uri.fromFile(imageFile));
            }
        } else {
            Toast.makeText(EditActivity.this, getString(R.string.no_image), Toast.LENGTH_SHORT).show();
        }
    }

    //show toast if type, price, quantity, and image are not entered
    private boolean checkMandatoryUserInput(){
        if (TextUtils.isEmpty(typeEditText.getText().toString().trim())
                || quantity < 0
                || TextUtils.isEmpty(priceEditText.getText().toString().trim())
                || imagePath == null) {
            Toast.makeText(this, getString(R.string.mandatory), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //save edit text input to DB
    private void saveItem() {
        String type = typeEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String reorderEmail = emailEditText.getText().toString().trim();
        int price = Integer.parseInt(priceEditText.getText().toString().trim());

        // Create a new map of values from user input and insert into DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_TYPE, type);
        contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_PRICE, price);
        contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_DESCRIPTION, description);
        contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY, quantity);
        contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_IMAGE, imagePath);
        contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL, reorderEmail);

        //call Content Resolver with Content URI and the content values entered by user
        //update item
        if (mContentUri != null) {
            int numberRowsUpdated = getContentResolver().update(mContentUri, contentValues, null, null);
            if (numberRowsUpdated == 0) {
                Toast.makeText(this, getString(R.string.editor_insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_successful), Toast.LENGTH_SHORT).show();
            }
            //new item
        } else {
            Uri mNewUri = getContentResolver().insert(InventoryContract.InventoryTable.CONTENT_URI, contentValues);
            if (mNewUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteItem() {
        if (mContentUri != null) {
            //call Content Resolver to delete
            int rowsDeleted = getContentResolver().delete(mContentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    //setting up menu and save dialog
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // hide the "Delete" menu item.
        if (mContentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (!checkMandatoryUserInput()) {
                    checkMandatoryUserInput();
                } else {
                    saveItem();
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //confirming user's delete choice
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //define projection to query DB, here: all columns
        String[] projection = {InventoryContract.InventoryTable._ID,
                InventoryContract.InventoryTable.COLUMN_ITEM_TYPE,
                InventoryContract.InventoryTable.COLUMN_ITEM_DESCRIPTION,
                InventoryContract.InventoryTable.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY,
                InventoryContract.InventoryTable.COLUMN_ITEM_IMAGE,
                InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL
        };
        return new CursorLoader(this, mContentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            // Attach cursor information to populate the EditText fields
            typeEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_TYPE)));
            descriptionEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_DESCRIPTION)));
            priceEditText.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_PRICE))));
            reorderEmail = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL));
            emailEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL)));
            quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY));
            quantityTextView.setText(getResources().getString(R.string.quantity) + "\n" + Integer.toString(quantity));
            imagePath = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_IMAGE));
            //show selected image
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                //Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
                //imageView.setImageBitmap(imageBitmap);
                imageView = (ImageView) findViewById(R.id.image_view);
                imageView.setImageURI(Uri.fromFile(imageFile));
            } else {
                Toast.makeText(EditActivity.this, getString(R.string.no_image), Toast.LENGTH_SHORT).show();
            }
        }

        //button wiring for edit item
        Button plusButton = (Button) findViewById(R.id.plus_button);
        Button minusButton = (Button) findViewById(R.id.minus_button);
        Button reorderButton = (Button) findViewById(R.id.reorder_button);
        Button imageButton = (Button) findViewById(R.id.image_button);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity += 1;
                quantityTextView.setText(getResources().getString(R.string.quantity) + "\n"  + Integer.toString(quantity));
            }
        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0){
                    quantity -= 1;
                    quantityTextView.setText(getResources().getString(R.string.quantity) + "\n"  + Integer.toString(quantity));
                }
            }
        });
        reorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reorder = new Intent(Intent.ACTION_SENDTO);
                reorder.setData(Uri.parse("mailto:" + reorderEmail));
                try {
                    startActivity(reorder);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(EditActivity.this, getString(R.string.no_email), Toast.LENGTH_LONG).show();
                }
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //no clean-up required, no Adapter used
    }
}



