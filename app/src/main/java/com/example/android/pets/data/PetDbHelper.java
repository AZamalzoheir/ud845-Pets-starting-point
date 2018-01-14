package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Amalzoheir on 1/11/2018.
 */

public class PetDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG=PetDbHelper.class.getName();
    private static final String DATABASE_NAME="shelter.db";
    private static final int DATABASE_VERSION=1;
    public  PetDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + petContract.petEntry.TABLE_NAME + " ("
                + petContract.petEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + petContract.petEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + petContract.petEntry.COLUMN_PET_BREED + " TEXT, "
                + petContract.petEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + petContract.petEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
        sqLiteDatabase.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
