package be.appreciate.webtickets.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class TicketTable
{
    public static final String TABLE_NAME = "tickets";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TICKET_ID = "ticket_id";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_USED = "used";

    public static final String COLUMN_ID_FULL = TABLE_NAME + "_" + COLUMN_ID;
    public static final String COLUMN_TICKET_ID_FULL = TABLE_NAME + "_" + COLUMN_TICKET_ID;
    public static final String COLUMN_EVENT_ID_FULL = TABLE_NAME + "_" + COLUMN_EVENT_ID;
    public static final String COLUMN_CODE_FULL = TABLE_NAME + "_" + COLUMN_CODE;
    public static final String COLUMN_NAME_FULL = TABLE_NAME + "_" + COLUMN_NAME;
    public static final String COLUMN_TYPE_FULL = TABLE_NAME + "_" + COLUMN_TYPE;
    public static final String COLUMN_USED_FULL = TABLE_NAME + "_" + COLUMN_USED;

    public static final Map<String, String> PROJECTION_MAP;

    static
    {
        PROJECTION_MAP = new HashMap<>();
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ID, TABLE_NAME + "." + COLUMN_ID + " AS " + COLUMN_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_TICKET_ID, TABLE_NAME + "." + COLUMN_TICKET_ID + " AS " + COLUMN_TICKET_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_EVENT_ID, TABLE_NAME + "." + COLUMN_EVENT_ID + " AS " + COLUMN_EVENT_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_CODE, TABLE_NAME + "." + COLUMN_CODE + " AS " + COLUMN_CODE_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_NAME, TABLE_NAME + "." + COLUMN_NAME + " AS " + COLUMN_NAME_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_TYPE, TABLE_NAME + "." + COLUMN_TYPE + " AS " + COLUMN_TYPE_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_USED, TABLE_NAME + "." + COLUMN_USED + " AS " + COLUMN_USED_FULL);
    }

    private static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TICKET_ID + " integer, "
            + COLUMN_EVENT_ID + " integer, "
            + COLUMN_CODE + " text, "
            + COLUMN_NAME + " text, "
            + COLUMN_TYPE + " text, "
            + COLUMN_USED + " integer"
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
