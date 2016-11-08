package be.appreciate.webtickets.model;

import android.database.Cursor;

import be.appreciate.webtickets.database.ScannedTicketTable;
import be.appreciate.webtickets.database.TicketTable;

/**
 * Created by Inneke De Clippel on 30/03/2016.
 */
public class Ticket
{
    private int id;
    private String name;
    private String type;
    private boolean used;
    private boolean scannedOffline;

    public static Ticket constructFromCursor(Cursor cursor)
    {
        Ticket ticket = new Ticket();

        ticket.id = cursor.getInt(cursor.getColumnIndex(TicketTable.COLUMN_TICKET_ID_FULL));
        ticket.name = cursor.getString(cursor.getColumnIndex(TicketTable.COLUMN_NAME_FULL));
        ticket.type = cursor.getString(cursor.getColumnIndex(TicketTable.COLUMN_TYPE_FULL));
        ticket.used = cursor.getInt(cursor.getColumnIndex(TicketTable.COLUMN_USED_FULL)) == 1;

        int offlineColumn = cursor.getColumnIndex(ScannedTicketTable.COLUMN_TICKET_ID_FULL);
        int offlineId = offlineColumn >= 0 ? cursor.getInt(offlineColumn) : 0;
        ticket.scannedOffline = offlineId > 0;

        return ticket;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public boolean isUsed()
    {
        return used;
    }

    public boolean isScannedOffline()
    {
        return scannedOffline;
    }
}
