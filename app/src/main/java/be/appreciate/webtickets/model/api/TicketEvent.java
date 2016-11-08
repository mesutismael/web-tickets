package be.appreciate.webtickets.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke De Clippel on 30/03/2016.
 */
public class TicketEvent
{
    @SerializedName("tickets")
    private List<Ticket> tickets;

    public List<Ticket> getTickets()
    {
        return this.tickets;
    }
}
