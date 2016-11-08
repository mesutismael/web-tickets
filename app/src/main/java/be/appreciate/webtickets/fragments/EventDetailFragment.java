package be.appreciate.webtickets.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import be.appreciate.webtickets.R;
import be.appreciate.webtickets.activities.ScanActivity;
import be.appreciate.webtickets.contentproviders.EventContentProvider;
import be.appreciate.webtickets.contentproviders.TicketContentProvider;
import be.appreciate.webtickets.database.EventTable;
import be.appreciate.webtickets.database.ScannedTicketTable;
import be.appreciate.webtickets.database.TicketTable;
import be.appreciate.webtickets.model.Event;
import be.appreciate.webtickets.utils.Constants;
import be.appreciate.webtickets.utils.DateUtils;
import be.appreciate.webtickets.utils.ImageUtils;
import be.appreciate.webtickets.utils.PermissionHelper;

/**
 * Created by Inneke De Clippel on 30/03/2016.
 */
public class EventDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener
{
    private ImageView imageViewEvent;
    private TextView textViewTitle;
    private TextView textViewDate;
    private TextView textViewScanCount;
    private Event event;

    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final String KEY_EVENT_ID = "event_id";

    public static EventDetailFragment newInstance(int eventId)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_EVENT_ID, eventId);

        EventDetailFragment fragment = new EventDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        this.imageViewEvent = (ImageView) view.findViewById(R.id.imageView_event);
        this.textViewTitle = (TextView) view.findViewById(R.id.textView_name);
        this.textViewDate = (TextView) view.findViewById(R.id.textView_date);
        this.textViewScanCount = (TextView) view.findViewById(R.id.textView_scanCount);
        Button buttonStartScan = (Button) view.findViewById(R.id.button_startScan);

        buttonStartScan.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.getLoaderManager().initLoader(Constants.LOADER_EVENT, null, this);
        this.getLoaderManager().initLoader(Constants.LOADER_TICKETS, null, this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_startScan:
                if(PermissionHelper.hasCameraPermission(v.getContext()))
                {
                    this.startNextActivity();
                }
                else
                {
                    this.requestPermission();
                }
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        if(this.getView() == null)
        {
            return null;
        }

        int eventId = this.getArguments() != null ? this.getArguments().getInt(KEY_EVENT_ID) : 0;

        switch (id)
        {
            case Constants.LOADER_EVENT:
                String selection = EventTable.TABLE_NAME + "." + EventTable.COLUMN_EVENT_ID + " = " + eventId;
                return new CursorLoader(this.getView().getContext(), EventContentProvider.CONTENT_URI, null, selection, null, null);

            case Constants.LOADER_TICKETS:
                String selectionTickets = TicketTable.TABLE_NAME + "." + TicketTable.COLUMN_EVENT_ID + " = " + eventId;
                return new CursorLoader(this.getView().getContext(), TicketContentProvider.CONTENT_URI_SCANNED, null, selectionTickets, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_EVENT:
                if(data != null && data.moveToFirst())
                {
                    this.event = Event.constructFromCursor(data);

                    this.textViewTitle.setText(this.event.getName());
                    this.textViewDate.setText(DateUtils.formatEventDate(this.event.getDate()));
                    ImageUtils.loadImage(this.imageViewEvent, this.event.getImage(), R.drawable.placeholder_large);

                    if(this.getActivity() != null)
                    {
                        this.getActivity().setTitle(this.event.getName());
                    }
                }
                break;

            case Constants.LOADER_TICKETS:
                int scannedTickets = 0;
                int offlineTickets = 0;
                int totalTickets = data != null ? data.getCount() : 0;

                if(data != null && data.moveToFirst())
                {
                    do
                    {
                        boolean used = data.getInt(data.getColumnIndex(TicketTable.COLUMN_USED_FULL)) == 1;
                        int offlineColumn = data.getColumnIndex(ScannedTicketTable.COLUMN_TICKET_ID_FULL);
                        boolean scannedOffline = offlineColumn >= 0 && data.getInt(offlineColumn) != 0;

                        if(used || scannedOffline)
                        {
                            scannedTickets ++;
                        }
                        if(scannedOffline)
                        {
                            offlineTickets ++;
                        }
                    }
                    while (data.moveToNext());
                }

                if(scannedTickets == totalTickets)
                {
                    this.textViewScanCount.setText(this.getString(R.string.event_scan_count_all, offlineTickets));
                }
                else
                {
                    this.textViewScanCount.setText(this.getString(R.string.event_scan_count, scannedTickets, totalTickets, offlineTickets));
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_PERMISSION:
                if(PermissionHelper.allPermissionsGranted(grantResults))
                {
                    this.startNextActivity();
                }
                else
                {
                    this.showSettingsDialog();
                }
                break;
        }
    }

    private void requestPermission()
    {
        this.requestPermissions(PermissionHelper.getCameraPermission(), REQUEST_CODE_PERMISSION);
    }

    private void showSettingsDialog()
    {
        if(this.getContext() != null)
        {
            new MaterialDialog.Builder(this.getContext())
                    .title(R.string.event_permission_declined_title)
                    .content(R.string.event_permission_declined_message)
                    .positiveText(R.string.event_permission_declined_positive)
                    .negativeText(R.string.dialog_negative)
                    .onPositive((dialog, which) ->
                    {
                        if (this.getContext() != null)
                        {
                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            settingsIntent.setData(Uri.parse("package:" + this.getContext().getPackageName()));
                            this.startActivity(settingsIntent);
                        }
                    })
                    .show();
        }
    }

    private void startNextActivity()
    {
        if(this.getContext() != null && this.event != null)
        {
            this.startActivity(ScanActivity.getIntent(this.getContext(), this.event.getId()));
        }
    }
}
