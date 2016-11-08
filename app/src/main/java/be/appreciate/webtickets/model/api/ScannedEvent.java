package be.appreciate.webtickets.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import be.appreciate.webtickets.model.ScannedTicket;

/**
 * Created by Inneke De Clippel on 11/04/2016.
 */
public class ScannedEvent
{
    @SerializedName("tickets")
    private List<ScannedTicket> scannedTickets;

    public ScannedEvent(List<ScannedTicket> scannedTickets)
    {
        this.scannedTickets = scannedTickets;
    }
}
