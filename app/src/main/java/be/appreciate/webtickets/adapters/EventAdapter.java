package be.appreciate.webtickets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import be.appreciate.webtickets.R;
import be.appreciate.webtickets.model.Event;
import be.appreciate.webtickets.utils.DateUtils;
import be.appreciate.webtickets.utils.ImageUtils;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<Event> events;
    private OnItemClickListener listener;

    private static final int TYPE_EVENT = 1;
    private static final int TYPE_EMPTY = 2;

    public EventAdapter()
    {
    }

    public void setEvents(List<Event> events)
    {
        this.events = events;
        this.notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position)
    {
        return this.getEventCount() > 0 ? TYPE_EVENT : TYPE_EMPTY;
    }

    @Override
    public int getItemCount()
    {
        int eventCount = this.getEventCount();
        int emptyCount = eventCount == 0 ? 1 : 0;
        return eventCount + emptyCount;
    }

    private int getEventCount()
    {
        return this.events != null ? this.events.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case TYPE_EVENT:
                View viewEvent = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_event, parent, false);
                return new EventViewHolder(viewEvent);

            case TYPE_EMPTY:
                View viewEmpty = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_event_empty, parent, false);
                return new EmptyViewHolder(viewEmpty);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof EventViewHolder)
        {
            Event event = this.events.get(position);
            ((EventViewHolder) holder).bind(event);
        }
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView textViewTitle;
        private TextView textViewDate;
        private ImageView imageViewEvent;
        private Event event;

        public EventViewHolder(View itemView)
        {
            super(itemView);

            this.textViewTitle = (TextView) itemView.findViewById(R.id.textView_name);
            this.textViewDate = (TextView) itemView.findViewById(R.id.textView_date);
            this.imageViewEvent = (ImageView) itemView.findViewById(R.id.imageView_event);

            itemView.setOnClickListener(this);
        }

        public void bind(Event event)
        {
            this.event = event;

            this.textViewTitle.setText(event.getName());
            this.textViewDate.setText(DateUtils.formatEventDate(event.getDate()));
            ImageUtils.loadImage(this.imageViewEvent, event.getImage(), R.drawable.placeholder);
        }

        @Override
        public void onClick(View v)
        {
            if(EventAdapter.this.listener != null)
            {
                EventAdapter.this.listener.onEventClick(v, this.event);
            }
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder
    {
        public EmptyViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    public interface OnItemClickListener
    {
        void onEventClick(View v, Event event);
    }
}
