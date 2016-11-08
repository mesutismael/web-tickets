package be.appreciate.webtickets.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import be.appreciate.webtickets.R;
import be.appreciate.webtickets.activities.EventDetailActivity;
import be.appreciate.webtickets.adapters.EventAdapter;
import be.appreciate.webtickets.api.ApiHelper;
import be.appreciate.webtickets.contentproviders.EventContentProvider;
import be.appreciate.webtickets.contentproviders.ScannedTicketContentProvider;
import be.appreciate.webtickets.database.EventTable;
import be.appreciate.webtickets.database.ScannedTicketTable;
import be.appreciate.webtickets.decorations.DividerDecoration;
import be.appreciate.webtickets.model.Event;
import be.appreciate.webtickets.services.UploadService;
import be.appreciate.webtickets.utils.ConnectivityManager;
import be.appreciate.webtickets.utils.Constants;
import be.appreciate.webtickets.utils.DateUtils;
import be.appreciate.webtickets.utils.ImageUtils;
import be.appreciate.webtickets.utils.Observer;
import be.appreciate.webtickets.utils.PreferencesHelper;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class EventsFragment extends Fragment implements EventAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor>, ConnectivityManager.ConnectivityListener
{
    private TextView textViewOffline;
    private TextView textViewSync;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EventAdapter eventAdapter;
    private boolean connected;
    private int offlineTicketCount;
    private long lastSyncAttempt;

    private static final long SYNC_ATTEMPT_INTERVAL = 3 * 60 * 1000; // 3 minutes

    public static EventsFragment newInstance()
    {
        return new EventsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        RecyclerView recyclerViewEvents = (RecyclerView) view.findViewById(R.id.recyclerView_events);
        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_events);
        this.textViewOffline = (TextView) view.findViewById(R.id.textView_offline);
        this.textViewSync = (TextView) view.findViewById(R.id.textView_sync);

        ImageUtils.setDrawableLeft(this.textViewOffline, R.drawable.ic_warning, view.getContext());
        ImageUtils.setDrawableLeft(this.textViewSync, R.drawable.ic_sync, view.getContext());

        ImageUtils.tintDrawables(this.textViewOffline, R.color.general_text_error);
        ImageUtils.tintDrawables(this.textViewSync, R.color.general_text_light);

        this.eventAdapter = new EventAdapter();
        this.eventAdapter.setListener(this);
        recyclerViewEvents.setAdapter(this.eventAdapter);
        DividerDecoration dividerDecoration = new DividerDecoration(view.getContext());
        recyclerViewEvents.addItemDecoration(dividerDecoration);

        this.swipeRefreshLayout.setOnRefreshListener(this);

        this.updateLastSync(view.getContext());

        ApiHelper.getEvents(view.getContext(), PreferencesHelper.getUserCode(view.getContext())).subscribe(this.eventsObserver);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.getLoaderManager().initLoader(Constants.LOADER_EVENTS, null, this);
        this.getLoaderManager().initLoader(Constants.LOADER_SCANNED_TICKETS, null, this);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        ConnectivityManager.addListener(this);
    }

    @Override
    public void onStop()
    {
        ConnectivityManager.removeListener(this);

        super.onStop();
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
            case Constants.LOADER_EVENTS:
                String sortOrder = EventTable.TABLE_NAME + "." + EventTable.COLUMN_DATE;
                return new CursorLoader(this.getView().getContext(), EventContentProvider.CONTENT_URI, null, null, null, sortOrder);

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
            case Constants.LOADER_EVENTS:
                List<Event> events = Event.constructListFromCursor(data);
                this.eventAdapter.setEvents(events);
                break;

            case Constants.LOADER_SCANNED_TICKETS:
                this.offlineTicketCount = data != null ? data.getCount() : 0;
                this.postOfflineTickets();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    @Override
    public void onEventClick(View v, Event event)
    {
        if(event != null)
        {
            this.startActivity(EventDetailActivity.getIntent(v.getContext(), event.getId()));
        }
    }

    @Override
    public void onRefresh()
    {
        if(this.getContext() != null)
        {
            ApiHelper.getEvents(this.getContext(), PreferencesHelper.getUserCode(this.getContext())).subscribe(this.eventsObserver);
        }
        else
        {
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onConnected()
    {
        this.textViewOffline.setVisibility(View.GONE);
        this.connected = true;
        this.postOfflineTickets();
    }

    @Override
    public void onDisconnected()
    {
        this.textViewOffline.setVisibility(View.VISIBLE);
        this.connected = false;
    }

    private void updateLastSync(Context context)
    {
        long lastSync = PreferencesHelper.getLastRefresh(context);
        boolean today = android.text.format.DateUtils.isToday(lastSync);
        String formattedLastSync = today ? DateUtils.formatShortDate(lastSync) : DateUtils.formatLongDate(lastSync);
        this.textViewSync.setText(this.getString(R.string.events_last_sync, formattedLastSync));
    }

    private void postOfflineTickets()
    {
        if(this.connected && this.offlineTicketCount > 0)
        {
            long now = System.currentTimeMillis();
            long elapsedTime = now - this.lastSyncAttempt;
            boolean sync = elapsedTime > SYNC_ATTEMPT_INTERVAL;

            this.lastSyncAttempt = now;

            if(sync && this.getContext() != null)
            {
                this.getContext().startService(UploadService.getIntent(this.getContext()));
            }
        }
    }

    private Observer<Object> eventsObserver = new Observer<Object>()
    {
        @Override
        public void onCompleted()
        {
            EventsFragment.this.swipeRefreshLayout.setRefreshing(false);

            if(EventsFragment.this.getContext() != null)
            {
                EventsFragment.this.updateLastSync(EventsFragment.this.getContext());
            }
        }

        @Override
        public void onError(Throwable e)
        {
            EventsFragment.this.swipeRefreshLayout.setRefreshing(false);
        }
    };
}
