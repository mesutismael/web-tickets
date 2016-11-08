package be.appreciate.webtickets.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import be.appreciate.webtickets.R;
import be.appreciate.webtickets.activities.LoginActivity;
import be.appreciate.webtickets.contentproviders.ScannedTicketContentProvider;
import be.appreciate.webtickets.database.ScannedTicketTable;
import be.appreciate.webtickets.model.ScannedTicket;
import be.appreciate.webtickets.services.UploadService;
import be.appreciate.webtickets.utils.Constants;
import be.appreciate.webtickets.utils.PreferencesHelper;

/**
 * Created by Inneke De Clippel on 31/03/2016.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>
{
    private TextView textViewSync;
    private Button buttonSync;
    private EditText editTextSuccessDuration;
    private List<ScannedTicket> scannedTickets;

    public static SettingsFragment newInstance()
    {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        this.textViewSync = (TextView) view.findViewById(R.id.textView_sync);
        this.buttonSync = (Button) view.findViewById(R.id.button_sync);
        this.editTextSuccessDuration = (EditText) view.findViewById(R.id.editText_duration);
        Button buttonSaveDuration = (Button) view.findViewById(R.id.button_saveDuration);
        Button buttonLogOut = (Button) view.findViewById(R.id.button_logOut);

        this.buttonSync.setOnClickListener(this);
        buttonSaveDuration.setOnClickListener(this);
        buttonLogOut.setOnClickListener(this);

        this.updateTicketCount();

        int successDuration = PreferencesHelper.getScanSuccessDuration(view.getContext());
        this.editTextSuccessDuration.setText(String.valueOf(successDuration));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.getLoaderManager().initLoader(Constants.LOADER_SCANNED_TICKETS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        if(this.getView() == null)
        {
            return null;
        }

        switch (id)
        {
            case Constants.LOADER_SCANNED_TICKETS:
                String selection = ScannedTicketTable.TABLE_NAME + "." + ScannedTicketTable.COLUMN_SYNCED + " = 0";
                return new CursorLoader(this.getView().getContext(), ScannedTicketContentProvider.CONTENT_URI, null, selection, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_SCANNED_TICKETS:
                this.scannedTickets = ScannedTicket.constructListFromCursor(data);
                this.updateTicketCount();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    @Override
    public void onClick(View v)
    {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        switch (v.getId())
        {
            case R.id.button_sync:
                this.sync();
                break;

            case R.id.button_saveDuration:
                String secondsAsString = this.editTextSuccessDuration.getText().toString();
                int seconds;
                try
                {
                    seconds = Integer.parseInt(secondsAsString);
                }
                catch (NumberFormatException e)
                {
                    seconds = 0;
                }

                if(seconds > 0)
                {
                    PreferencesHelper.saveScanSuccessDuration(v.getContext(), seconds);
                }
                else
                {
                    this.editTextSuccessDuration.setText(String.valueOf(PreferencesHelper.getScanSuccessDuration(v.getContext())));
                }
                break;

            case R.id.button_logOut:
                this.logOut();
                break;
        }
    }

    private void logOut()
    {
        if(this.getContext() != null)
        {
            PreferencesHelper.clearUser(this.getContext());

            Intent intent = LoginActivity.getIntent(this.getContext());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        }
    }

    private void sync()
    {
        if(!UploadService.RUNNING && this.getTicketCount() > 0 && this.getContext() != null)
        {
            this.getContext().startService(UploadService.getIntent(this.getContext()));
        }
    }

    private int getTicketCount()
    {
        return this.scannedTickets != null ? this.scannedTickets.size() : 0;
    }

    private void updateTicketCount()
    {
        int count = this.getTicketCount();
        this.textViewSync.setText(this.getString(R.string.settings_sync_message, count));
        this.buttonSync.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }
}
