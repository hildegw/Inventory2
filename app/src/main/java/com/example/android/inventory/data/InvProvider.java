package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

//handles all DB operations from UI to DB and back

public class InvProvider extends ContentProvider {

    //DB helper
    private InvDbHelper mInvDbHelper;

    /** Tag for the log messages */
    public static final String LOG_TAG = InvProvider.class.getSimpleName();

    //prepare URI Matcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int INV = 100;
    private static final int INV_ID = 101;
    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INV);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INV_ID);
    }

    //Instantiate database helper object in onCreate
    @Override
    public boolean onCreate() {
        mInvDbHelper = new InvDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortorder) {
        //get readable DB
        SQLiteDatabase db = mInvDbHelper.getReadableDatabase();
        Cursor cursor;
        //match content Uri
        switch (sUriMatcher.match(uri)) {
            case INV:
                //get all data
                cursor = db.query(InventoryContract.InventoryTable.TABLE_NAME, projection, null, null, null, null, null);
                break;
            case INV_ID:
                // select just one row with _ID
                selection = InventoryContract.InventoryTable._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(InventoryContract.InventoryTable.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                //no match found
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //notify Content Resolver to watch for data changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        //match content URIs
        switch (sUriMatcher.match(uri)) {
            case INV:
                return InventoryContract.InventoryTable.CONTENT_LIST_TYPE;
            case INV_ID:
                return InventoryContract.InventoryTable.CONTENT_ITEM_TYPE;
            default:
                //no match found
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //fetch values to be inserted into DB from content values variable
        //check mandatory type input before inserting into DB
        if(contentValues.containsKey(InventoryContract.InventoryTable.COLUMN_ITEM_TYPE)) {
            String type = contentValues.getAsString(InventoryContract.InventoryTable.COLUMN_ITEM_TYPE);
        } else {
            throw new IllegalArgumentException("Item requires a name");
        }
        String description = contentValues.getAsString(InventoryContract.InventoryTable.COLUMN_ITEM_DESCRIPTION);
        int price = contentValues.getAsInteger(InventoryContract.InventoryTable.COLUMN_ITEM_PRICE);
        int quantity = contentValues.getAsInteger(InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY);
        int image = contentValues.getAsInteger(InventoryContract.InventoryTable.COLUMN_ITEM_IMAGE);
        String email = contentValues.getAsString(InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL);

        //get writable DB
        SQLiteDatabase db = mInvDbHelper.getWritableDatabase();

        //match content URI
        switch (sUriMatcher.match(uri)) {
            case INV:
                //insert data and return row ID, insert: table, all columns, content values
                long newRowId = db.insert(InventoryContract.InventoryTable.TABLE_NAME, null, contentValues);
                //error info
                if (newRowId == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                //notify Content Resolver about change
                getContext().getContentResolver().notifyChange(uri, null);
                //then return URI based on Content URI plus new row ID, i.e. the URI to reach the new pet entry
                return ContentUris.withAppendedId(uri, newRowId);
            default:
                //no match found
                throw new IllegalArgumentException("Cannot insert with unknown URI " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //get writable DB
        SQLiteDatabase db = mInvDbHelper.getWritableDatabase();
        //initiate return value for update method
        int numberRowsDeleted;
        //match content URIs
        switch (sUriMatcher.match(uri)) {
            case INV_ID:
                //find data entry matching the row ID provided by URI
                selection = InventoryContract.InventoryTable._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                //insert updated pet data, returns the number of rows that were updated
                numberRowsDeleted = db.delete(InventoryContract.InventoryTable.TABLE_NAME, selection, selectionArgs);
                //error info
                if (numberRowsDeleted == 0) {
                    Log.e(LOG_TAG, "Failed to delete row for " + uri);
                    return 0;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return numberRowsDeleted;
            default:
                //no match found
                throw new IllegalArgumentException("Cannot delete with unknown URI " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        String type = contentValues.getAsString(InventoryContract.InventoryTable.COLUMN_ITEM_TYPE);
        String description = contentValues.getAsString(InventoryContract.InventoryTable.COLUMN_ITEM_DESCRIPTION);
        int price = contentValues.getAsInteger(InventoryContract.InventoryTable.COLUMN_ITEM_PRICE);
        int quantity = contentValues.getAsInteger(InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY);
        int image = contentValues.getAsInteger(InventoryContract.InventoryTable.COLUMN_ITEM_IMAGE);
        String email = contentValues.getAsString(InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL);
        //get writable DB
        SQLiteDatabase db = mInvDbHelper.getWritableDatabase();
        //initiate return value for update method
        int numberRowsUpdated;
        //match content Uri
        switch (sUriMatcher.match(uri)) {
            case INV_ID:
                // select just one row with _ID
                selection = InventoryContract.InventoryTable._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                //insert updated pet data, returns the number of rows that were updated
                numberRowsUpdated = db.update(InventoryContract.InventoryTable.TABLE_NAME, contentValues, selection,selectionArgs);                //error info
                if (numberRowsUpdated == 0) {
                    Log.e(LOG_TAG, "Failed to update row for " + uri);
                    return 0;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return numberRowsUpdated;
            default:
                //no match found
                throw new IllegalArgumentException("Cannot update with unknown URI " + uri);
        }
    }
}
