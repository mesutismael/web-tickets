package be.appreciate.webtickets.contentproviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.appreciate.webtickets.database.DatabaseHelper;
import be.appreciate.webtickets.database.ScannedTicketTable;
import be.appreciate.webtickets.database.TicketTable;

/**
 * Created by Inneke De Clippel on 30/03/2016.
 */
public class TicketContentProvider extends ContentProvider
{
    private DatabaseHelper databaseHelper;

    private static final String PROVIDER_NAME = "be.appreciate.webtickets.contentproviders.TicketContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/");
    public static final Uri CONTENT_URI_SCANNED = Uri.parse("content://" + PROVIDER_NAME + "/scanned/");
    private static final int TICKETS = 1;
    private static final int TICKETS_SCANNED = 2;
    private static final UriMatcher URI_MATCHER;

    static
    {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, null, TICKETS);
        URI_MATCHER.addURI(PROVIDER_NAME, "scanned", TICKETS_SCANNED);
    }

    @Override
    public boolean onCreate()
    {
        this.databaseHelper = new DatabaseHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        switch (URI_MATCHER.match(uri))
        {
            case TICKETS:
            case TICKETS_SCANNED:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME;
        }

        return null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Cursor cursor;
        String tables;
        Map<String, String> projectionMap;

        switch (URI_MATCHER.match(uri))
        {
            case TICKETS:
                tables = TicketTable.TABLE_NAME;
                projectionMap = TicketTable.PROJECTION_MAP;
                queryBuilder.setTables(tables);
                queryBuilder.setProjectionMap(projectionMap);

                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(this.getContext().getContentResolver(), CONTENT_URI);
                return cursor;

            case TICKETS_SCANNED:
                tables = TicketTable.TABLE_NAME
                        + " left join " + ScannedTicketTable.TABLE_NAME
                        + " on " + TicketTable.TABLE_NAME + "." + TicketTable.COLUMN_TICKET_ID + " = " + ScannedTicketTable.TABLE_NAME + "." + ScannedTicketTable.COLUMN_TICKET_ID;

                projectionMap = new HashMap<>();
                projectionMap.putAll(TicketTable.PROJECTION_MAP);
                projectionMap.putAll(ScannedTicketTable.PROJECTION_MAP);

                queryBuilder.setTables(tables);
                queryBuilder.setProjectionMap(projectionMap);

                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(this.getContext().getContentResolver(), CONTENT_URI_SCANNED);
                return cursor;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        long rowId;

        switch (URI_MATCHER.match(uri))
        {
            case TICKETS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowId = db.replace(TicketTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowId > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            return Uri.withAppendedPath(CONTENT_URI, String.valueOf(rowId));
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int rowsDeleted;

        switch (URI_MATCHER.match(uri))
        {
            case TICKETS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsDeleted = db.delete(TicketTable.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsDeleted > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int rowsUpdated;

        switch (URI_MATCHER.match(uri))
        {
            case TICKETS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsUpdated = db.update(TicketTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsUpdated > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        int rowsInserted = 0;

        switch (URI_MATCHER.match(uri))
        {
            case TICKETS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(db, TicketTable.TABLE_NAME);

                db.beginTransaction();
                try
                {
                    if(values != null)
                    {
                        for (ContentValues cv : values)
                        {
                            if(cv != null)
                            {
                                inserter.prepareForInsert();
                                inserter.bind(inserter.getColumnIndex(TicketTable.COLUMN_TICKET_ID), cv.getAsInteger(TicketTable.COLUMN_TICKET_ID));
                                inserter.bind(inserter.getColumnIndex(TicketTable.COLUMN_EVENT_ID), cv.getAsInteger(TicketTable.COLUMN_EVENT_ID));
                                inserter.bind(inserter.getColumnIndex(TicketTable.COLUMN_CODE), cv.getAsString(TicketTable.COLUMN_CODE));
                                inserter.bind(inserter.getColumnIndex(TicketTable.COLUMN_NAME), cv.getAsString(TicketTable.COLUMN_NAME));
                                inserter.bind(inserter.getColumnIndex(TicketTable.COLUMN_TYPE), cv.getAsString(TicketTable.COLUMN_TYPE));
                                inserter.bind(inserter.getColumnIndex(TicketTable.COLUMN_USED), cv.getAsBoolean(TicketTable.COLUMN_USED));

                                long rowId = inserter.execute();

                                if (rowId != -1)
                                {
                                    rowsInserted++;
                                }
                            }
                        }
                    }

                    db.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    rowsInserted = 0;
                }
                finally
                {
                    db.endTransaction();
                    inserter.close();
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (rowsInserted > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }

        return rowsInserted;
    }
}
