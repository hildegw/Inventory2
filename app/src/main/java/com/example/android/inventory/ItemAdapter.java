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

import com.example.android.inventory.data.InventoryContract;
import com.example.android.inventory.data.RVCursorAdapter;

import static java.lang.String.valueOf;

//import com.example.android.inventory.data.InventoryItem;

//RecyclerView Adapter for Items

public class ItemAdapter extends RVCursorAdapter<ItemAdapter.ItemHolder> {

    private LayoutInflater mInflator;
    private Context mContext;

    //Holder class
    public static class ItemHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView typeView;
        public TextView priceView;
        public TextView quantityView;

        //construct item and find its views
        public ItemHolder(View view) {
            super(view);
            mView = view;
        }
    }

    //constructor for ItemAdapter
    public ItemAdapter(Activity context) {
        super(null);
        mContext = context;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.list_item, parent, false);
        //create ViewHolder instance and find its views
        final ItemHolder holder = new ItemHolder(view);
        holder.typeView = (TextView) view.findViewById(R.id.type);
        holder.priceView = (TextView) view.findViewById(R.id.price);
        holder.quantityView = (TextView) view.findViewById(R.id.quantity);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ItemHolder holder, Cursor cursor) {
        //binding views to cursor data
        String type = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_TYPE));
        //String description = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_DESCRIPTION));
        int price = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY));
        //String email = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL));
        //int image = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryTable.COLUMN_ITEM_IMAGE));
        Log.i("ItemAdapt", holder.toString());
        holder.typeView.setText(type);
        holder.priceView.setText(valueOf(price) + " €");
        holder.quantityView.setText("Quantity: " + valueOf(quantity));
    }

    @Override
    public void swapCursor(Cursor newCursor) {
        super.swapCursor(newCursor);

        /*/I have an text view that gets displayed when there are no
        //items in the list, and this code handles hiding/showing it.
        //Whenever there is a new cursor.
        if (getItemCount() > 0) {
            mEmptyText.setVisibility(View.GONE);
        } else {
            mEmptyText.setVisibility(View.VISIBLE);
        }*/
    }
}

    /*/ todo ??? Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mBooks.size();
    }
    */
