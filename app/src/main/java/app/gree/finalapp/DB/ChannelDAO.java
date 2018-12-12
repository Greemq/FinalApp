package app.gree.finalapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.gree.finalapp.Model.Channel;

/**
 *
 */

public class ChannelDAO {
    SQLiteOpenHelper sqLiteOpenHelper;
    SQLiteDatabase database;

    private static final String[] allColumns = {
            DBHelper.COLUMN_CHANNEL_ID,
            DBHelper.COLUMN_CHANNEL_LINK,
            DBHelper.COLUMN_CHANNEL_NAME,
            DBHelper.COLUMN_CHANNEL_LAST_UPDATE};

    public ChannelDAO(){}
    public ChannelDAO(Context context) {
        sqLiteOpenHelper = new DBHelper(context);
    }

    public void open() {
        database = sqLiteOpenHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public Channel addChannel(Channel channel) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_CHANNEL_LINK, channel.getLink());
        cv.put(DBHelper.COLUMN_CHANNEL_NAME, channel.getName());
        cv.put(DBHelper.COLUMN_CHANNEL_LAST_UPDATE, "never");
        long insertId = database.insert(DBHelper.TABLE_CHANNELS, null, cv);
        channel.setId(insertId);
        return channel;
    }

    public int updateChannel(Channel channel, String date) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_CHANNEL_LINK, channel.getLink());
        cv.put(DBHelper.COLUMN_CHANNEL_NAME, channel.getName());
        cv.put(DBHelper.COLUMN_CHANNEL_LAST_UPDATE, date.toString());
        return database.update(DBHelper.TABLE_CHANNELS, cv, DBHelper.COLUMN_CHANNEL_ID + "=?", new String[]{String.valueOf(channel.getId())});
    }

    public Date dateFormat(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date dt = null;
        try {
            dt = formatter.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public Channel getChannel(long id) {
        Cursor cursor = database.query(DBHelper.TABLE_CHANNELS, allColumns,
                DBHelper.COLUMN_CHANNEL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Channel channel = new Channel(Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2), dateFormat(cursor.getString(3)));
        return channel;
    }

    public List<Channel> getAllChannels() {
        Cursor cursor = database.query(DBHelper.TABLE_CHANNELS, allColumns, null, null, null, null, null);
        List<Channel> channelList = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Channel channel = new Channel(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_CHANNEL_ID)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CHANNEL_LINK)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CHANNEL_NAME)),
                        dateFormat(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CHANNEL_LAST_UPDATE))));
                channelList.add(channel);
            }
        }
        return channelList;
    }

    public void deleteChannel(Channel channel) {
        database.delete(DBHelper.TABLE_ITEMS,DBHelper.COLUMN_ITEM_CHANNEL+"="+channel.getId(),null);
        database.delete(DBHelper.TABLE_CHANNELS, DBHelper.COLUMN_CHANNEL_ID + "=" + channel.getId(), null);
    }
}
