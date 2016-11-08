package be.appreciate.webtickets.model;

import be.appreciate.webtickets.model.api.ValidateResponse;

/**
 * Created by Inneke De Clippel on 30/03/2016.
 */
public class Validation
{
    private String name;
    private String type;
    private String errorMessage;
    private boolean apiExecutionError;
    private boolean apiTicketError;
    private boolean offlineNotFoundError;
    private boolean offlineScannedError;

    public static Validation fromApiSuccess(ValidateResponse response)
    {
        Validation validation = new Validation();

        if(response == null)
        {
            validation.apiExecutionError = true;
        }
        else
        {
            validation.name = response.getName();
            validation.type = response.getType();
        }

        return validation;
    }

    public static Validation fromApiError(ValidateResponse response)
    {
        Validation validation = new Validation();

        if(response == null)
        {
            validation.apiExecutionError = true;
        }
        else
        {
            validation.apiTicketError = true;
            validation.errorMessage = response.getError();
        }

        return validation;
    }

    public static Validation fromTicket(Ticket ticket)
    {
        Validation validation = new Validation();

        if(ticket == null)
        {
            validation.offlineNotFoundError = true;
        }
        else if(ticket.isUsed() || ticket.isScannedOffline())
        {
            validation.offlineScannedError = true;
        }
        else
        {
            validation.name = ticket.getName();
            validation.type = ticket.getType();
        }

        return validation;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public boolean isApiExecutionError()
    {
        return apiExecutionError;
    }

    public boolean isApiTicketError()
    {
        return apiTicketError;
    }

    public boolean isOfflineNotFoundError()
    {
        return offlineNotFoundError;
    }

    public boolean isOfflineScannedError()
    {
        return offlineScannedError;
    }
}
