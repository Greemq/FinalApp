package app.gree.finalapp.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by VH on 07.05.2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "FinalAppDatabase.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CHANNELS = "channels";
    public static final String COLUMN_CHANNEL_ID = "_id";
    public static final String COLUMN_CHANNEL_NAME = "name";
    public static final String COLUMN_CHANNEL_LINK = "link";
    public static final String COLUMN_CHANNEL_LAST_UPDATE = "lastUpdate";

    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ITEM_ID = "_id";
    public static final String COLUMN_ITEM_TITLE = "title";
    public static final String COLUMN_ITEM_LINK = "link";
    public static final String COLUMN_ITEM_PUB_DATE = "pub_date";
    public static final String COLUMN_ITEM_FAVOURITE = "favourite";
    public static final String COLUMN_ITEM_CHANNEL = "channel_id";

    private static final String createTableChannel = "CREATE TABLE " + TABLE_CHANNELS + "(" +
            COLUMN_CHANNEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CHANNEL_NAME + " TEXT NOT NULL, " +
            COLUMN_CHANNEL_LINK + " TEXT NOT NULL, "+
            COLUMN_CHANNEL_LAST_UPDATE+" TEXT NOT NULL "
            + ");";

    private static final String createTableItems = "CREATE TABLE " + TABLE_ITEMS + "(" +
            COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ITEM_TITLE + " TEXT NOT NULL, " +
            COLUMN_ITEM_LINK + " TEXT NOT NULL, " +
            COLUMN_ITEM_PUB_DATE + " TEXT NOT NULL, " +
            COLUMN_ITEM_FAVOURITE + " INTEGER NOT NULL, " +
            COLUMN_ITEM_CHANNEL + " INTEGER NOT NULL " + ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableChannel);
        db.execSQL(createTableItems);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TALBE IF EXIST " + TABLE_CHANNELS);
        db.execSQL("DROP TALBE IF EXIST " + TABLE_ITEMS);
        onCreate(db);
    }
}
