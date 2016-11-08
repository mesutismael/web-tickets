package be.appreciate.webtickets.model.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke De Clippel on 30/03/2016.
 */
public class ValidateResponse
{
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("message")
    private String error;

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public String getError()
    {
        return error;
    }
}
