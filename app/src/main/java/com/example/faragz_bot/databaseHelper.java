package com.example.faragz_bot;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.airbnb.lottie.animation.content.Content;

public class databaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABSE_NAME = "remider.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME ="notes";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "note_title";
    private static final String COLUMN_NOTE = "note";
    public databaseHelper(@Nullable Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query  =
                "CREATE TABLE "+TABLE_NAME +
                        " ( "+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        COLUMN_TITLE+" TEXT,"+
                        COLUMN_NOTE+" TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    void addNote(String title ,String note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE,title);
        cv.put(COLUMN_NOTE,note);
        long result = db.insert(TABLE_NAME,null,cv);
         if(result == -1){
             Toast.makeText(context, "inserting in database failed", Toast.LENGTH_SHORT).show();
         }else {
             Toast.makeText(context, "Added successfully !َ", Toast.LENGTH_SHORT).show();
         }
    }
}