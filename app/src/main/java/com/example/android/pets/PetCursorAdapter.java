package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.petContract;

/**
 * Created by Amalzoheir on 1/23/2018.
 */

public class PetCursorAdapter extends CursorAdapter {
    public PetCursorAdapter(Context context, Cursor c) {
                super(context, c, 0 /* flags */);
          }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView=(TextView)view.findViewById(R.id.name);
        TextView summeryTextView=(TextView)view.findViewById(R.id.summary);
        int nameColumnIndex=cursor.getColumnIndex(petContract.petEntry.COLUMN_PET_NAME);
        int breedColumnIndex=cursor.getColumnIndex(petContract.petEntry.COLUMN_PET_BREED);

        String petName=cursor.getString(nameColumnIndex);
        String petBreed=cursor.getString(breedColumnIndex);
        if (TextUtils.isEmpty(petBreed)) {
            petBreed = context.getString(R.string.unknown_breed);
        }
        nameTextView.setText(petName);
        summeryTextView.setText(petBreed);
    }
}
