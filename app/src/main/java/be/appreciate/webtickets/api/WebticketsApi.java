package be.appreciate.webtickets.api;

import be.appreciate.webtickets.model.api.EmptyBody;
import be.appreciate.webtickets.model.api.EventsResponse;
import be.appreciate.webtickets.model.api.ScannedTicketsRequest;
import be.appreciate.webtickets.model.api.TicketsResponse;
import be.appreciate.webtickets.model.api.ValidateResponse;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by Inneke De Clippel on 24/03/2016.
 */
public interface WebticketsApi
{
    @GET("/events")
    Observable<EventsResponse> getEvents(@Header("Wt-Unique-Key") String key);

    @GET("/list_tickets/{event_id}")
    Observable<TicketsResponse> getTickets(@Header("Wt-Unique-Key") String key,
                                           @Path("event_id") String eventId);

    @POST("/validate_ticket/{event_id}/{code}")
    Observable<ValidateResponse> validate(@Header("Wt-Unique-Key") String key,
                                          @Path("event_id") String eventId,
                                          @Path("code") String code,
                                          @Body EmptyBody body);

    @POST("/scanned_tickets")
    Response postScannedTicketsBlocking(@Header("Wt-Unique-Key") String key,
                                        @Body ScannedTicketsRequest request);
}
