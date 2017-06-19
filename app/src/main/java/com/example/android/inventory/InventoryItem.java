package com.example.android.inventory;

//Holds data structure for inventory and reads out cursor

import android.database.Cursor;

public class InventoryItem {

    private String mType;
    private String mDescription;
    private int mImageResId;
    private int mPrice;
    private int mQuantity;
    private String mEmail;

    public InventoryItem(String type, String description, int price, int quanatity, String email) {
        mType = type;
        mDescription = description;
        //mImageResId = imageResId;
        mPrice = price;
        mQuantity = quanatity;
        mEmail = email;
    }

    //get item position from cursor
    public static InventoryItem getFromCursor(Cursor cursor) {
        //todo getter methods
        InventoryItem item = new InventoryItem("Amsterdam", "fresh", 35, 4, "email:xxxx" );
        return item;
    }
}
