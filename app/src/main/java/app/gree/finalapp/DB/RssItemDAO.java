package app.gree.finalapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.gree.finalapp.Model.Channel;
import app.gree.finalapp.Model.RssItem;

/**
 * Created by VH on 08.05.2017.
 */

public class RssItemDAO {
    private Context context;
    SQLiteOpenHelper sqLiteOpenHelper;
    SQLiteDatabase database;

    private static String[] allColumns = {
            DBHelper.COLUMN_ITEM_ID, DBHelper.COLUMN_ITEM_TITLE,
            DBHelper.COLUMN_ITEM_LINK, DBHelper.COLUMN_ITEM_PUB_DATE,
            DBHelper.COLUMN_ITEM_FAVOURITE, DBHelper.COLUMN_ITEM_CHANNEL
    };

    public RssItemDAO() {
    }

    public RssItemDAO(Context context) {
        sqLiteOpenHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() {
        database = sqLiteOpenHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public RssItem addRssItem(RssItem rssItem) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_ITEM_TITLE, rssItem.getTitle());
        cv.put(DBHelper.COLUMN_CHANNEL_LINK, rssItem.getLink());
        cv.put(DBHelper.COLUMN_ITEM_PUB_DATE, rssItem.getPubDate().toString());
        cv.put(DBHelper.COLUMN_ITEM_FAVOURITE, rssItem.getFavourite());
        cv.put(DBHelper.COLUMN_ITEM_CHANNEL, rssItem.getChannel().getId());
        long insertId = database.insert(DBHelper.TABLE_ITEMS, null, cv);
        rssItem.setId(insertId);
        return rssItem;
    }

    public int setFavourite(RssItem rssItem) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_ITEM_TITLE, rssItem.getTitle());
        cv.put(DBHelper.COLUMN_CHANNEL_LINK, rssItem.getLink());
        cv.put(DBHelper.COLUMN_ITEM_PUB_DATE, rssItem.getPubDate().toString());
        if (rssItem.getFavourite() == 1) {
            rssItem.setFavourite(0);
        } else
            rssItem.setFavourite(1);
        cv.put(DBHelper.COLUMN_ITEM_FAVOURITE, rssItem.getFavourite());
        cv.put(DBHelper.COLUMN_ITEM_CHANNEL, rssItem.getChannel().getId());
        return database.update(DBHelper.TABLE_ITEMS, cv, DBHelper.COLUMN_ITEM_ID + "=?", new String[]{String.valueOf(rssItem.getId())});
    }

    private Channel getChannel(long id) {
        ChannelDAO channelDAO = new ChannelDAO(context);
        channelDAO.open();
        Channel channel = channelDAO.getChannel(id);
        channelDAO.close();
        return channel;
    }

    public List<RssItem> getAllRssItems() {
        Cursor cursor = database.query(DBHelper.TABLE_ITEMS, allColumns, null, null, null, null, null);
        List<RssItem> rssItems = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                RssItem rssItem = new RssItem();
                rssItem.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_ID)));
                rssItem.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_TITLE)));
                rssItem.setLink(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_LINK)));
                rssItem.setPubDate(dateFormat(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_PUB_DATE))));
                rssItem.setFavourite(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_FAVOURITE)));
                rssItem.setChannel(getChannel(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_CHANNEL))));
                rssItems.add(rssItem);
            }
        }
        return rssItems;
    }
    private Date dateFormat(String str) {
        SimpleDateFormat inputFormat=new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",Locale.ENGLISH);
        Date dt = null;
        try {
            dt = inputFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }



    public List<RssItem> getChannelItems(long id) {
        Cursor cursor = database.query(DBHelper.TABLE_ITEMS, allColumns, DBHelper.COLUMN_ITEM_CHANNEL + " = ?", new String[]{String.valueOf(id)},null,null,null);
        List<RssItem> rssItems = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                RssItem rssItem = new RssItem();
                rssItem.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_ID)));
                rssItem.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_TITLE)));
                rssItem.setLink(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_LINK)));
                rssItem.setPubDate(dateFormat(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_PUB_DATE))));
                rssItem.setFavourite(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_FAVOURITE)));
                rssItem.setChannel(getChannel(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_CHANNEL))));
                rssItems.add(rssItem);
            }
        }
        return rssItems;
    }

    public List<RssItem> bookmarts() {
        Cursor cursor = database.query(DBHelper.TABLE_ITEMS, allColumns, DBHelper.COLUMN_ITEM_FAVOURITE + " = ?", new String[]{String.valueOf(1)},null,null,null);
        List<RssItem> rssItems = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                RssItem rssItem = new RssItem();
                rssItem.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_ID)));
                rssItem.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_TITLE)));
                rssItem.setLink(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_LINK)));
                rssItem.setPubDate(dateFormat(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_PUB_DATE))));
                rssItem.setFavourite(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_FAVOURITE)));
                rssItem.setChannel(getChannel(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ITEM_CHANNEL))));
                rssItems.add(rssItem);
            }
        }
        return rssItems;
    }
}
