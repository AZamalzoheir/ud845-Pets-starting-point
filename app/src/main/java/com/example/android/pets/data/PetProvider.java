package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.R.attr.id;
import static android.R.attr.value;

/**
 * Created by Amalzoheir on 1/20/2018.
 */

public class PetProvider extends ContentProvider {
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private static final int PETS=100;
    private static final int PETS_ID=101;
    public static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(petContract.CONTENT_AUTHORITY,petContract.PATH_PETS,PETS);
        sUriMatcher.addURI(petContract.CONTENT_AUTHORITY,petContract.PATH_PETS+"/#",PETS_ID);
    }
    private PetDbHelper mdHelper;

    @Override
    public boolean onCreate() {
        mdHelper=new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortOrder) {
        SQLiteDatabase database=mdHelper.getReadableDatabase();
        Cursor cursor = null;
        int match=sUriMatcher.match(uri);
        switch (match){
            case PETS:
                cursor=database.query(petContract.petEntry.TABLE_NAME,projection,null,null,null,null,sortOrder);
                break;
            case PETS_ID:
                selection=petContract.petEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor=database.query(petContract.petEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot make query"+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return petContract.petEntry.CONTENT_LIST_TYPE;
            case PETS_ID:
                return petContract.petEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Override
    public Uri insert(Uri uri,ContentValues contentValues) {
        final int match=sUriMatcher.match(uri);
        switch (match){
            case PETS:
                return insertPet(uri,contentValues);
            default:
                throw new IllegalArgumentException("cannot make query"+uri);
        }

    }
    private  Uri insertPet(Uri uri,ContentValues contentValues){
        String name = contentValues.getAsString(petContract.petEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        Integer gender = contentValues.getAsInteger(petContract.petEntry.COLUMN_PET_GENDER);
        if (gender == null || !petContract.petEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }
        Integer weight = contentValues.getAsInteger(petContract.petEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }
        SQLiteDatabase db= mdHelper.getWritableDatabase();

        Long id=db.insert(petContract.petEntry.TABLE_NAME,null,contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);//insert new item in database make notify
        return ContentUris.withAppendedId(uri,id);
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mdHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                rowsDeleted=database.delete(petContract.petEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PETS_ID:
                // Delete a single row given by the ID in the URI
                selection = petContract.petEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted= database.delete(petContract.petEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = petContract.petEntry._ID+ "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(petContract.petEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(petContract.petEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(petContract.petEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(petContract.petEntry.COLUMN_PET_GENDER);
            if (gender == null || !petContract.petEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(petContract.petEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(petContract.petEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mdHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement

        int rowsUpdated =database.update(petContract.petEntry.TABLE_NAME, values, selection, selectionArgs);

            if (rowsUpdated != 0) {
                       getContext().getContentResolver().notifyChange(uri, null);
                    }
        return rowsUpdated;
    }

}
