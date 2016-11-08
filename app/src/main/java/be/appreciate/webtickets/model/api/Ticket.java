package be.appreciate.webtickets.model.api;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import be.appreciate.webtickets.database.TicketTable;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class Ticket
{
    @SerializedName("ticket_id")
    private int id;
    @SerializedName("barcode")
    private String code;
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("used")
    private boolean used;

    public ContentValues getContentValues(int eventId)
    {
        ContentValues cv = new ContentValues();

        cv.put(TicketTable.COLUMN_TICKET_ID, this.id);
        cv.put(TicketTable.COLUMN_EVENT_ID, eventId);
        cv.put(TicketTable.COLUMN_CODE, this.code);
        cv.put(TicketTable.COLUMN_NAME, this.name);
        cv.put(TicketTable.COLUMN_TYPE, this.type);
        cv.put(TicketTable.COLUMN_USED, this.used);

        return cv;
    }
}
