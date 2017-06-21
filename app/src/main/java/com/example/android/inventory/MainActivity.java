package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static int LOADER_ID = 0;
    private TextView mEmptyStateTextView;
    private ItemAdapter mItemAdapter;
    private View mView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;  //comes as part of recycler view
    private RecyclerView.LayoutManager mLayoutManager;
    private int emptyCursor = 1;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open Editor Activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        //show progress bar and instructions
        ProgressBar pg = (ProgressBar) findViewById(R.id.loading);
        pg.setVisibility(View.GONE);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mEmptyStateTextView.setText(R.string.empty_view);

        //start Recycler
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //and prepare loading data from DB
        getLoaderManager().initLoader(LOADER_ID, null, this);
        //insertDummyData(); //todo remove
        Log.i("ActMain on Create", "after getLoaderManager");
    }

    //todo remove
    private void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryTable.COLUMN_ITEM_TYPE, "Toto");
        values.put(InventoryContract.InventoryTable.COLUMN_ITEM_PRICE, 41);
        values.put(InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY, 11);
        values.put(InventoryContract.InventoryTable.COLUMN_ITEM_DESCRIPTION, "Toto");
        values.put(InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL, "Toto");
        Uri newUri = getContentResolver().insert(InventoryContract.InventoryTable.CONTENT_URI, values);
    }

    private void deleteInventory() {
        int rowsDeleted = getContentResolver().delete(InventoryContract.InventoryTable.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.main_delete_db_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.main_delete_db),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem = menu.findItem(R.id.action_delete_all_entries);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    //confirming user's delete choice
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteInventory();
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //define projection, i.e. all data columns
        String[] projection = {InventoryContract.InventoryTable._ID,
                InventoryContract.InventoryTable.COLUMN_ITEM_TYPE,
                InventoryContract.InventoryTable.COLUMN_ITEM_DESCRIPTION,
                InventoryContract.InventoryTable.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY,
                InventoryContract.InventoryTable.COLUMN_ITEM_IMAGE,
                InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL
        };
        //and get a CursorLoader from content provider
        return new android.content.CursorLoader(this, InventoryContract.InventoryTable.CONTENT_URI, projection, null, null, null);
    }

    //hide loading circle, empty view instructions and Recycler View depending on situation
    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        ProgressBar pg = (ProgressBar) findViewById(R.id.loading);
        pg.setVisibility(View.GONE);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        Log.i("onLoadFin", "getCount" + valueOf(cursor.getCount()));
        if (cursor.getCount() == 0) {
            //in case there is no data to display, set Instructions
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            if (menuItem != null){menuItem.setVisible(false);}
        } else {
            mEmptyStateTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            if (menuItem != null){menuItem.setVisible(true);}
            // Set the adapter if cursor data exists
            if (mItemAdapter == null) {
                mItemAdapter = new ItemAdapter(this);
                mRecyclerView.setAdapter(mItemAdapter);  // todo : was ItemAdapter(mListener)
            }
            mItemAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // clear out current cursor from the adapter.
        mItemAdapter.swapCursor(null);
    }
}
