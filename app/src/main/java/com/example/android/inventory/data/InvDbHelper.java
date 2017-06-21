package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//SQLite commands to create DB

public class InvDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    //DB commands
    private static final String SQL_CREATE_DB_TABLE =
            "CREATE TABLE " + InventoryContract.InventoryTable.TABLE_NAME + " (" +
                    InventoryContract.InventoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    InventoryContract.InventoryTable.COLUMN_ITEM_TYPE + " TEXT NOT NULL," +
                    InventoryContract.InventoryTable.COLUMN_ITEM_DESCRIPTION + " TEXT,"  +
                    InventoryContract.InventoryTable.COLUMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0," +
                    InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                    InventoryContract.InventoryTable.COLUMN_ITEM_IMAGE + " INTEGER," +
                    InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL + " TEXT);";

    private static final String SQL_UPGRADE_ENTRIES =
            "DROP TABLE IF EXISTS " + InventoryContract.InventoryTable.TABLE_NAME;

    //constructor
    public InvDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_DB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_UPGRADE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
