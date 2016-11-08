package be.appreciate.webtickets.model.api;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import be.appreciate.webtickets.database.EventTable;

/**
 * Created by Inneke De Clippel on 24/03/2016.
 */
public class Event
{
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String name;
    @SerializedName("date")
    private long date;
    @SerializedName("background_image")
    private String image;

    public int getId()
    {
        return id;
    }

    public ContentValues getContentValues()
    {
        ContentValues cv = new ContentValues();

        cv.put(EventTable.COLUMN_EVENT_ID, this.id);
        cv.put(EventTable.COLUMN_NAME, this.name);
        cv.put(EventTable.COLUMN_DATE, this.date * 1000);
        cv.put(EventTable.COLUMN_IMAGE, this.image);

        return cv;
    }
}
