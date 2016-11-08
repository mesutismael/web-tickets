package be.appreciate.webtickets.model.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke De Clippel on 11/04/2016.
 */
public class ScannedTicketsRequest
{
    @SerializedName("event")
    private ScannedEvent event;

    public ScannedTicketsRequest(ScannedEvent event)
    {
        this.event = event;
    }
}
