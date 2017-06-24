package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//SQLite commands to create DB

public class InvDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";
    private Context mContext;

    //DB commands
    private static final String SQL_CREATE_DB_TABLE =
            "CREATE TABLE " + InventoryContract.InventoryTable.TABLE_NAME + " (" +
                    InventoryContract.InventoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    InventoryContract.InventoryTable.COLUMN_ITEM_TYPE + " TEXT NOT NULL," +
                    InventoryContract.InventoryTable.COLUMN_ITEM_DESCRIPTION + " TEXT,"  +
                    InventoryContract.InventoryTable.COLUMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0," +
                    InventoryContract.InventoryTable.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                    InventoryContract.InventoryTable.COLUMN_ITEM_IMAGE + " TEXT NOT NULL," +
                    InventoryContract.InventoryTable.COLUMN_ITEM_EMAIL + " TEXT);";

    private static final String SQL_UPGRADE_ENTRIES =
            "DROP TABLE IF EXISTS " + InventoryContract.InventoryTable.TABLE_NAME;

    //constructor
    public InvDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
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


    /*/todo method to save an image to device storage, returns the path to be stored in DB
    public String saveImagetoStorage(long imageId, Bitmap image) {
        // Saves the new picture to the internal storage with the unique identifier of the report as
        // the name. That way, there will never be two report pictures with the same name.
        String imagePath = "";
        File internalStorage = mContext.getDir("InventoryImages", Context.MODE_PRIVATE);
        File imagesFilePath = new File(internalStorage, imageId + ".png");
        imagePath = imagesFilePath.toString();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagesFilePath);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception ex) {
            Log.i("DATABASE", "Problem updating picture", ex);
            imagePath = "";
        }
        return imagePath;
    }*/
}
