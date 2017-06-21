package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.android.inventory.data.InventoryContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static int LOADER_ID = 0;
    private ItemAdapter mItemAdapter;
    private View mView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;  //comes as part of recycler view
    private RecyclerView.LayoutManager mLayoutManager;


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


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //define projection, i.e. data columns
        String[] projection = {InventoryContract.InventoryTable._ID,
                InventoryContract.InventoryTable.COLUMN_ITEM_TYPE,
                InventoryContract.InventoryTable.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY
        };
        //and get a CursorLoader from content provider
        return new android.content.CursorLoader(this, InventoryContract.InventoryTable.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        //When the loader has loaded some data (either initially, or the
        //datasource has changed and a new cursor is being provided),
        //Then we'll swap out the curser in our recyclerview's adapter
        // and we'll create the adapter if necessary
        // Set the adapter
        Log.i("setting ItemAdapter", "in Main Act");
        if (mItemAdapter == null) {
            mItemAdapter = new ItemAdapter(this);
            mRecyclerView.setAdapter(mItemAdapter);  // todo : was ItemAdapter(mListener)
        }
        mItemAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        //If the loader is reset, we need to clear out the
        //current cursor from the adapter.
        mItemAdapter.swapCursor(null);
    }
}
