package com.example.android.inventory.data;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

//CursorAdapter based on Recycler View
//the Adapter takes no responsibility for closing the cursor

public abstract class RVCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIDColumn;

    public abstract void onBindViewHolder(VH holder, Cursor cursor);

    public RVCursorAdapter(Cursor c) {
        setHasStableIds(true);
        swapCursor(c);
    }

    //view holder receives a cursor for each item to be displayed
    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("Cannot bind viewholder when cursor is in invalid state.");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position + " when trying to bind viewholder");
        }
        Log.i("onBindVH", holder.toString());
        onBindViewHolder(holder, mCursor);
    }

    @Override
    public int getItemCount() {
        if (mDataValid) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        if (!mDataValid) {
            throw new IllegalStateException("Cannot lookup item id when cursor is in invalid state.");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position + " when trying to get an item id");
        }
        return mCursor.getLong(mRowIDColumn);
    }


    //per row_ID get cursor adapted to item view
    public void swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return;
        }
        if (newCursor != null) {
            mCursor = newCursor;
            mRowIDColumn = mCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            mCursor = null;
            mRowIDColumn = -1;
            mDataValid = false;
        }
    }
}