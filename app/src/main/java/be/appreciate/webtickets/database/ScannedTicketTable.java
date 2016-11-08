package be.appreciate.webtickets.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class ScannedTicketTable
{
    public static final String TABLE_NAME = "scanned_tickets";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TICKET_ID = "ticket_id";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_SYNCED = "synced";

    public static final String COLUMN_ID_FULL = TABLE_NAME + "_" + COLUMN_ID;
    public static final String COLUMN_TICKET_ID_FULL = TABLE_NAME + "_" + COLUMN_TICKET_ID;
    public static final String COLUMN_CODE_FULL = TABLE_NAME + "_" + COLUMN_CODE;
    public static final String COLUMN_TIME_FULL = TABLE_NAME + "_" + COLUMN_TIME;
    public static final String COLUMN_SYNCED_FULL = TABLE_NAME + "_" + COLUMN_SYNCED;

    public static final Map<String, String> PROJECTION_MAP;

    static
    {
        PROJECTION_MAP = new HashMap<>();
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ID, TABLE_NAME + "." + COLUMN_ID + " AS " + COLUMN_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_TICKET_ID, TABLE_NAME + "." + COLUMN_TICKET_ID + " AS " + COLUMN_TICKET_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_CODE, TABLE_NAME + "." + COLUMN_CODE + " AS " + COLUMN_CODE_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_TIME, TABLE_NAME + "." + COLUMN_TIME + " AS " + COLUMN_TIME_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_SYNCED, TABLE_NAME + "." + COLUMN_SYNCED + " AS " + COLUMN_SYNCED_FULL);
    }

    private static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TICKET_ID + " integer, "
            + COLUMN_CODE + " text, "
            + COLUMN_TIME + " integer, "
            + COLUMN_SYNCED + " integer default 0"
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
