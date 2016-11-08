package be.appreciate.webtickets.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class EventTable
{
    public static final String TABLE_NAME = "events";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_IMAGE = "image";

    public static final String COLUMN_ID_FULL = TABLE_NAME + "_" + COLUMN_ID;
    public static final String COLUMN_EVENT_ID_FULL = TABLE_NAME + "_" + COLUMN_EVENT_ID;
    public static final String COLUMN_NAME_FULL = TABLE_NAME + "_" + COLUMN_NAME;
    public static final String COLUMN_DATE_FULL = TABLE_NAME + "_" + COLUMN_DATE;
    public static final String COLUMN_IMAGE_FULL = TABLE_NAME + "_" + COLUMN_IMAGE;

    public static final Map<String, String> PROJECTION_MAP;

    static
    {
        PROJECTION_MAP = new HashMap<>();
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ID, TABLE_NAME + "." + COLUMN_ID + " AS " + COLUMN_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_EVENT_ID, TABLE_NAME + "." + COLUMN_EVENT_ID + " AS " + COLUMN_EVENT_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_NAME, TABLE_NAME + "." + COLUMN_NAME + " AS " + COLUMN_NAME_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_DATE, TABLE_NAME + "." + COLUMN_DATE + " AS " + COLUMN_DATE_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_IMAGE, TABLE_NAME + "." + COLUMN_IMAGE + " AS " + COLUMN_IMAGE_FULL);
    }

    private static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_EVENT_ID + " integer, "
            + COLUMN_NAME + " text, "
            + COLUMN_DATE + " integer, "
            + COLUMN_IMAGE + " text"
            + ");";

    public static void onCreate(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
