package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
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

import static com.example.android.inventory.MainActivity.LOADER_ID;
import static com.example.android.inventory.R.id.email;
import static com.example.android.inventory.R.id.price;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //track editing
    private boolean mItemHasChanged = false;
    private EditText typeEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private EditText emailEditText;
    private TextView quantityTextView;
    private ImageView imageView;
    private int quantity = 0;
    private String reorderEmail = "";
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };
    private Uri mContentUri;
    private static final String LOG_TAG = EditActivity.class.getSimpleName();


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
        priceEditText = (EditText) findViewById(price);
        emailEditText = (EditText) findViewById(email);
        imageView = (ImageView) findViewById(R.id.image);
        quantityTextView = (TextView) findViewById(R.id.quantity);

        //set touch listener method to edit fields and for buttons
        typeEditText.setOnTouchListener(mTouchListener);
        descriptionEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        emailEditText.setOnTouchListener(mTouchListener);

        //button wiring for "add new item"
        if (mContentUri == null) {
            Button plusButton = (Button) findViewById(R.id.plus_button);
            Button minusButton = (Button) findViewById(R.id.minus_button);
            Button reorderButton = (Button) findViewById(R.id.reorder_button);
            quantityTextView.setText("Quantity\n" + "0");
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    quantity += 1;
                    quantityTextView.setText("Quantity\n" + Integer.toString(quantity));
                }
            });
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (quantity > 0){
                        quantity -= 1;
                        quantityTextView.setText("Quantity\n" + Integer.toString(quantity));
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
        }
    }

    //add com.example.android.inventory.data entered to the DB
    private void saveItem() {
        //get user input, type is mandatory
        String type = typeEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String reorderEmail = emailEditText.getText().toString().trim();

        //set price and quantity to 0, if not entered
        int price;
        if (priceEditText.length() > 0) {
            price = Integer.parseInt(priceEditText.getText().toString().trim());
        } else {
            price = 0;
        }

        // Create a new map of values from user input and insert into DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_TYPE, type);
        contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_PRICE, price);
        contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_DESCRIPTION, description);
        contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY, quantity);
        contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL, reorderEmail);
        //contentValues.put(InventoryContract.InventoryTable.COLUMN_ITEM_IMAGE, 0);   //todo

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
                String type = typeEditText.getText().toString().trim();
                if (type.equals("")) {
                    Toast.makeText(this, getString(R.string.enter_type), Toast.LENGTH_LONG).show();
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
                        // User clicked "Discard" button, close the current activity.
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
            //todo image, reoder
            quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY));
            quantityTextView.setText("Quantity\n" + Integer.toString(quantity));

        }
        //button wiring for edit item
        Button plusButton = (Button) findViewById(R.id.plus_button);
        Button minusButton = (Button) findViewById(R.id.minus_button);
        Button reorderButton = (Button) findViewById(R.id.reorder_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity += 1;
                quantityTextView.setText("Quantity\n" + Integer.toString(quantity));
            }
        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0){
                    quantity -= 1;
                    quantityTextView.setText("Quantity\n" + Integer.toString(quantity));
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
                    Log.e(LOG_TAG, "no email service available");
                    Toast.makeText(EditActivity.this, getString(R.string.no_email), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //no clean-up required, no Adapter used
    }
}



