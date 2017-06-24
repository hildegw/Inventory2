package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


//contains the data structure used in inventory app and DB
public class InventoryContract {

    //content authority
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
    //content provider URIs
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //content path
    public static final String PATH_INVENTORY = "inventory";

    //constructor
    private InventoryContract() {
    }

    //sub-class for inventory table in DB
    public static final class InventoryTable implements BaseColumns {

        //content uri for table
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        //MIME types
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        //table name (= content path name)
        public static final String TABLE_NAME = "inventory";

        //table setup
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_TYPE = "type";
        public static final String COLUMN_ITEM_DESCRIPTION = "description";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_IMAGE = "image";
        public static final String COLUMN_ITEM_EMAIL = "email";
    }
}
