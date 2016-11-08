package be.appreciate.webtickets.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "webtickets.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        EventTable.onCreate(db);
        TicketTable.onCreate(db);
        ScannedTicketTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        EventTable.onUpgrade(db, oldVersion, newVersion);
        TicketTable.onUpgrade(db, oldVersion, newVersion);
        ScannedTicketTable.onUpgrade(db, oldVersion, newVersion);
    }
}
