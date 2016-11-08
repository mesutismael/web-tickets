package be.appreciate.webtickets.model;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.webtickets.database.ScannedTicketTable;

/**
 * Created by Inneke De Clippel on 11/04/2016.
 */
public class ScannedTicket
{
    @SerializedName("ticket_id")
    private int ticketId;
    @SerializedName("barcode")
    private String code;
    @SerializedName("scan_date")
    private long time;

    public static List<ScannedTicket> constructListFromCursor(Cursor cursor)
    {
        List<ScannedTicket> tickets = new ArrayList<>();

        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                tickets.add(ScannedTicket.constructFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        return tickets;
    }

    public static ScannedTicket constructFromCursor(Cursor cursor)
    {
        ScannedTicket ticket = new ScannedTicket();

        ticket.ticketId = cursor.getInt(cursor.getColumnIndex(ScannedTicketTable.COLUMN_TICKET_ID_FULL));
        ticket.code = cursor.getString(cursor.getColumnIndex(ScannedTicketTable.COLUMN_CODE_FULL));
        ticket.time = cursor.getLong(cursor.getColumnIndex(ScannedTicketTable.COLUMN_TIME_FULL));

        return ticket;
    }

    public int getTicketId()
    {
        return ticketId;
    }

    public String getCode()
    {
        return code;
    }

    public long getTime()
    {
        return time;
    }
}
