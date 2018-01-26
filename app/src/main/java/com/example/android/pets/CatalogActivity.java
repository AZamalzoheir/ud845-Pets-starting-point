/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.petContract;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {
    //private  PetDbHelper mDBHlper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
               // displayDatabaseInfo();

            }
        });
       // mDBHlper=new PetDbHelper(this);
        displayDatabaseInfo();


    }
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
      //  PetDbHelper mDbHelper = new PetDbHelper(this);

        // Create and/or open a database to read from it
        //SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.

        String []projection={
                petContract.petEntry.COLUMN_PET_BREED,
                petContract.petEntry.COLUMN_PET_GENDER,
                petContract.petEntry._ID,
                petContract.petEntry.COLUMN_PET_NAME,
                petContract.petEntry.COLUMN_PET_WEIGHT


        };
       /*Cursor cursor=db.query(petContract.petEntry.TABLE_NAME,projection,null,null,null,null,null);*/

        Cursor cursor=getContentResolver().query(petContract.petEntry.CONTENT_URI,projection,null,null,null);
        ListView lViewItem=(ListView)findViewById(R.id.list);
        PetCursorAdapter petCursorAdapter=new PetCursorAdapter(this,cursor);
        View emptyView = findViewById(R.id.empty_view);
        lViewItem.setEmptyView(emptyView);
        lViewItem.setAdapter(petCursorAdapter);

    }
    /*private void insertPet(){
        SQLiteDatabase db=mDBHlper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(petContract.petEntry.COLUMN_PET_NAME, "Toto");
        contentValues.put(petContract.petEntry.COLUMN_PET_BREED, "Terrier");
        contentValues.put(petContract.petEntry.COLUMN_PET_GENDER,petContract.petEntry.GENDER_MALE);
        contentValues.put(petContract.petEntry.COLUMN_PET_WEIGHT, 7);
        long rowId=db.insert(petContract.petEntry.TABLE_NAME,null,contentValues);
        Log.v("row Id","effected"+rowId);
    }*/
    private void insertPet(){
        ContentValues contentValues=new ContentValues();
        contentValues.put(petContract.petEntry.COLUMN_PET_NAME, "Toto");
        contentValues.put(petContract.petEntry.COLUMN_PET_BREED, "Terrier");
        contentValues.put(petContract.petEntry.COLUMN_PET_GENDER,petContract.petEntry.GENDER_MALE);
        contentValues.put(petContract.petEntry.COLUMN_PET_WEIGHT, 7);
        Uri newUri =getContentResolver().insert(petContract.petEntry.CONTENT_URI,contentValues);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
            {
                insertPet();
                displayDatabaseInfo();}
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
