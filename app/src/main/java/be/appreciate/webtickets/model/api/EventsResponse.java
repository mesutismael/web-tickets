package be.appreciate.webtickets.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke De Clippel on 24/03/2016.
 */
public class EventsResponse
{
    @SerializedName("events")
    private List<Event> events;

    public List<Event> getEvents()
    {
        return events;
    }
}
