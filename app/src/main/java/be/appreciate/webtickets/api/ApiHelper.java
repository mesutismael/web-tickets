package be.appreciate.webtickets.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import be.appreciate.webtickets.R;
import be.appreciate.webtickets.contentproviders.EventContentProvider;
import be.appreciate.webtickets.contentproviders.ScannedTicketContentProvider;
import be.appreciate.webtickets.contentproviders.TicketContentProvider;
import be.appreciate.webtickets.database.ScannedTicketTable;
import be.appreciate.webtickets.database.TicketTable;
import be.appreciate.webtickets.model.ScannedTicket;
import be.appreciate.webtickets.model.Validation;
import be.appreciate.webtickets.model.api.EmptyBody;
import be.appreciate.webtickets.model.api.Event;
import be.appreciate.webtickets.model.api.ScannedEvent;
import be.appreciate.webtickets.model.api.ScannedTicketsRequest;
import be.appreciate.webtickets.model.api.Ticket;
import be.appreciate.webtickets.model.api.ValidateResponse;
import be.appreciate.webtickets.utils.PreferencesHelper;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Inneke De Clippel on 24/03/2016.
 */
public class ApiHelper
{
    private static WebticketsApi service;

    private static WebticketsApi getService()
    {
        if(service == null)
        {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://webtickets.be/app")
                    .build();

            service = restAdapter.create(WebticketsApi.class);
        }

        return service;
    }

    public static Observable<Object> getEvents(Context context, String key)
    {
        return ApiHelper.getService().getEvents(key)
                .flatMap(eventsResponse ->
                {
                    if (eventsResponse != null && eventsResponse.getEvents() != null)
                    {
                        return Observable.from(eventsResponse.getEvents());
                    }
                    else
                    {
                        String error = "The events response is null";
                        return Observable.error(new IOException(error));
                    }
                })
                .flatMap(event ->
                {
                    Observable<List<Ticket>> tickets = ApiHelper.getTickets(key, event.getId());
                    return Observable.zip(tickets, Observable.just(event), ((tickets1, event1) -> new Pair<>(event1, tickets1)));
                })
                .toList()
                .doOnNext(pairs ->
                {
                    context.getContentResolver().delete(EventContentProvider.CONTENT_URI, null, null);
                    context.getContentResolver().delete(TicketContentProvider.CONTENT_URI, null, null);

                    List<ContentValues> cvEvent = new ArrayList<>();
                    List<ContentValues> cvTicket = new ArrayList<>();

                    for (Pair<Event, List<Ticket>> pair : pairs)
                    {
                        Event event = pair.first;
                        List<Ticket> tickets = pair.second;

                        if (event != null)
                        {
                            cvEvent.add(event.getContentValues());

                            for (Ticket ticket : tickets)
                            {
                                if (ticket != null)
                                {
                                    cvTicket.add(ticket.getContentValues(event.getId()));
                                }
                            }
                        }
                    }

                    context.getContentResolver().bulkInsert(EventContentProvider.CONTENT_URI, cvEvent.toArray(new ContentValues[cvEvent.size()]));
                    context.getContentResolver().bulkInsert(TicketContentProvider.CONTENT_URI, cvTicket.toArray(new ContentValues[cvTicket.size()]));

                    PreferencesHelper.saveLastRefresh(context, System.currentTimeMillis());
                })
                .flatMap(pairs -> Observable.empty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Observable<List<Ticket>> getTickets(String key, int eventId)
    {
        return ApiHelper.getService().getTickets(key, String.valueOf(eventId))
                .flatMap(ticketsResponse ->
                {
                    if (ticketsResponse != null && ticketsResponse.getTickets() != null)
                    {
                        return Observable.just(ticketsResponse.getTickets());
                    }
                    else
                    {
                        String error = "The tickets response is null";
                        return Observable.error(new IOException(error));
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public static Observable<Validation> validate(Context context, String key, int eventId, String code)
    {
        return ApiHelper.getService().validate(key, String.valueOf(eventId), code, new EmptyBody())
                .map(validateResponse -> Validation.fromApiSuccess(validateResponse))
                .onErrorResumeNext(throwable -> Observable.just(Validation.fromApiError(ApiHelper.getResponseFromError(throwable))))
                .flatMap(validation ->
                {
                    if (validation.isApiExecutionError())
                    {
                        return ApiHelper.validateOffline(context, eventId, code);
                    }
                    else if (validation.isApiTicketError())
                    {
                        ApiHelper.updateTicketScanned(context, eventId, code);
                        return Observable.error(new IOException(validation.getErrorMessage()));
                    }
                    else if (ApiHelper.isTicketScannedOffline(context, eventId, code))
                    {
                        return Observable.error(new IOException(context.getString(R.string.scan_error_offline_scanned)));
                    }
                    else
                    {
                        ApiHelper.updateTicketScanned(context, eventId, code);
                        return Observable.just(validation);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Observable<Validation> validateOffline(Context context, int eventId, String code)
    {
        return Observable.defer(() ->
        {
            be.appreciate.webtickets.model.Ticket ticket = ApiHelper.queryTicket(context, eventId, code);
            Validation validation = Validation.fromTicket(ticket);

            if (validation.isOfflineNotFoundError())
            {
                return Observable.error(new IOException(context.getString(R.string.scan_error_offline_not_found)));
            }
            else if (validation.isOfflineScannedError())
            {
                return Observable.error(new IOException(context.getString(R.string.scan_error_offline_scanned)));
            }
            else
            {
                ApiHelper.insertScannedTicket(context, ticket.getId(), code);
                return Observable.just(validation);
            }
        });
    }

    public static Response postScannedTicketsBlocking(String key, List<ScannedTicket> tickets)
    {
        return ApiHelper.getService().postScannedTicketsBlocking(key, new ScannedTicketsRequest(new ScannedEvent(tickets)));
    }

    private static boolean isTicketScannedOffline(Context context, int eventId, String code)
    {
        be.appreciate.webtickets.model.Ticket ticket = ApiHelper.queryTicket(context, eventId, code);
        return ticket != null && (ticket.isUsed() || ticket.isScannedOffline());
    }

    private static void updateTicketScanned(Context context, int eventId, String code)
    {
        ContentValues cv = new ContentValues();
        cv.put(TicketTable.COLUMN_USED, true);

        String selection = TicketTable.TABLE_NAME + "." + TicketTable.COLUMN_EVENT_ID + " = ?"
                + " AND " + TicketTable.TABLE_NAME + "." + TicketTable.COLUMN_CODE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(eventId), code};

        context.getContentResolver().update(TicketContentProvider.CONTENT_URI, cv, selection, selectionArgs);
    }

    private static void insertScannedTicket(Context context, int ticketId, String code)
    {
        ContentValues cv = new ContentValues();
        cv.put(ScannedTicketTable.COLUMN_TICKET_ID, ticketId);
        cv.put(ScannedTicketTable.COLUMN_CODE, code);
        cv.put(ScannedTicketTable.COLUMN_TIME, System.currentTimeMillis());

        context.getContentResolver().insert(ScannedTicketContentProvider.CONTENT_URI, cv);
    }

    private static be.appreciate.webtickets.model.Ticket queryTicket(Context context, int eventId, String code)
    {
        String selection = TicketTable.TABLE_NAME + "." + TicketTable.COLUMN_EVENT_ID + " = ?"
                + " AND " + TicketTable.TABLE_NAME + "." + TicketTable.COLUMN_CODE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(eventId), code};
        Cursor cursor = context.getContentResolver().query(TicketContentProvider.CONTENT_URI_SCANNED, null, selection, selectionArgs, null);

        be.appreciate.webtickets.model.Ticket ticket = cursor != null && cursor.moveToFirst() ? be.appreciate.webtickets.model.Ticket.constructFromCursor(cursor) : null;

        if (cursor != null)
        {
            cursor.close();
        }

        return ticket;
    }

    private static ValidateResponse getResponseFromError(Throwable throwable)
    {
        if(throwable instanceof RetrofitError)
        {
            RetrofitError error = (RetrofitError) throwable;
            try
            {
                return (ValidateResponse) error.getBodyAs(ValidateResponse.class);
            }
            catch (Exception e)
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
}
