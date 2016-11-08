package be.appreciate.webtickets.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class TicketsResponse
{
    @SerializedName("event")
    private TicketEvent event;

    public List<Ticket> getTickets()
    {
        return this.event != null ? this.event.getTickets() : null;
    }
}
