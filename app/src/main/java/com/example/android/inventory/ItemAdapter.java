package com.example.android.inventory;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.inventory.data.RVCursorAdapter;

//import com.example.android.inventory.data.InventoryItem;

//RecyclerView Adapter for Items

public class ItemAdapter extends RVCursorAdapter<ItemAdapter.ItemHolder> {

    private LayoutInflater mInflator;
    private Context mContext;
    private InventoryItem mItem;


    public ItemAdapter(Activity context) {
        super(null);
        mContext = context;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public ItemAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.list_item, parent, false);
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ItemAdapter.ItemHolder holder, Cursor cursor) {
        //get item from dataset at each position and replace the contents of the view with the item
        final InventoryItem  currentItem = mItem.getFromCursor(cursor);
        Log.i("onBindVH", "ItemAdapter");
        holder.bindItem(currentItem);
    }


    public static class ItemHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTypeView;
        public final TextView mPriceView;
        public final TextView mQuantityView;

        //construct item and find its views
        public ItemHolder(View view) {
            super(view);
            mView = view;
            mTypeView = (TextView) view.findViewById(R.id.type);
            mPriceView = (TextView) view.findViewById(R.id.price);
            mQuantityView = (TextView) view.findViewById(R.id.quantity);
        }

        public void bindItem(InventoryItem item) {
            //todo ??? I put code here to load item data into the various views in my ViewHolder.
            //binding views
            mTypeView.setText("Amsterdam"); //implement getType, getPrice usw.
            mPriceView.setText("34€");
            mQuantityView.setText("quantity: 23");
            Log.i("binding item", "");

        }
    }

}