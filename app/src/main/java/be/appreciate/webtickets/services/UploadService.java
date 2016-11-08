package be.appreciate.webtickets.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import be.appreciate.webtickets.api.ApiHelper;
import be.appreciate.webtickets.contentproviders.ScannedTicketContentProvider;
import be.appreciate.webtickets.database.ScannedTicketTable;
import be.appreciate.webtickets.model.ScannedTicket;
import be.appreciate.webtickets.utils.PreferencesHelper;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Inneke De Clippel on 11/04/2016.
 */
public class UploadService extends IntentService
{
    public static boolean RUNNING = false;

    public static Intent getIntent(Context context)
    {
        return new Intent(context, UploadService.class);
    }

    public UploadService()
    {
        super("UploadService");
    }

    public UploadService(String name)
    {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        RUNNING = true;

        List<ScannedTicket> tickets = this.getTickets();

        if(tickets != null && tickets.size() > 0)
        {
            try
            {
                Response response = ApiHelper.postScannedTicketsBlocking(PreferencesHelper.getUserCode(this), tickets);
                String message = this.getResponse(response);

                if (message != null)
                {
                    this.updateScannedTickets(tickets);
                }
            }
            catch (RetrofitError e)
            {
                //Something went wrong while executing the POST
            }
        }

        RUNNING = false;
    }

    private List<ScannedTicket> getTickets()
    {
        String selection = ScannedTicketTable.TABLE_NAME + "." + ScannedTicketTable.COLUMN_SYNCED + " = 0";

        Cursor cursor = this.getContentResolver().query(ScannedTicketContentProvider.CONTENT_URI, null, selection, null, null);

        List<ScannedTicket> tickets = ScannedTicket.constructListFromCursor(cursor);

        if(cursor != null)
        {
            cursor.close();
        }

        return tickets;
    }

    private void updateScannedTickets(List<ScannedTicket> tickets)
    {
        if(tickets == null || tickets.isEmpty())
        {
            return;
        }

        StringBuilder ids = new StringBuilder();
        boolean first = true;

        for(ScannedTicket ticket : tickets)
        {
            if(first)
            {
               first = false;
            }
            else
            {
                ids.append(", ");
            }
            ids.append(ticket.getTicketId());
        }

        ContentValues cv = new ContentValues();
        cv.put(ScannedTicketTable.COLUMN_SYNCED, 1);

        String selection = ScannedTicketTable.TABLE_NAME + "." + ScannedTicketTable.COLUMN_TICKET_ID + " in (" + ids.toString() + ")";
        this.getContentResolver().update(ScannedTicketContentProvider.CONTENT_URI, cv, selection, null);
    }

    private String getResponse(Response response)
    {
        if(response == null || response.getBody() == null)
        {
            return null;
        }

        String responseBody;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try
        {
            inputStreamReader = new InputStreamReader(response.getBody().in());
            bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder out = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                out.append(line);
            }
            responseBody = out.toString();
        }
        catch (IOException e)
        {
            responseBody = null;
        }
        finally
        {
            if(inputStreamReader != null)
            {
                try
                {
                    inputStreamReader.close();
                }
                catch (IOException e)
                {
                }
            }

            if(bufferedReader != null)
            {
                try
                {
                    bufferedReader.close();
                }
                catch (IOException e)
                {
                }
            }
        }

        return responseBody;
    }
}
