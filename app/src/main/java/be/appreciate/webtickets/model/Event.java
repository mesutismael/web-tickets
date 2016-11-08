package be.appreciate.webtickets.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.webtickets.database.EventTable;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class Event
{
    private int id;
    private String name;
    private long date;
    private String image;

    public static List<Event> constructListFromCursor(Cursor cursor)
    {
        List<Event> events = new ArrayList<>();

        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                events.add(Event.constructFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        return events;
    }

    public static Event constructFromCursor(Cursor cursor)
    {
        Event event = new Event();

        event.id = cursor.getInt(cursor.getColumnIndex(EventTable.COLUMN_EVENT_ID_FULL));
        event.name = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_NAME_FULL));
        event.date = cursor.getLong(cursor.getColumnIndex(EventTable.COLUMN_DATE_FULL));
        event.image = cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_IMAGE_FULL));

        return event;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public long getDate()
    {
        return date;
    }

    public String getImage()
    {
        return image;
    }
}
