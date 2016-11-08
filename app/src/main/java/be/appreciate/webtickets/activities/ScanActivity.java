package be.appreciate.webtickets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import be.appreciate.webtickets.R;
import be.appreciate.webtickets.fragments.ScanFragment;

/**
 * Created by Inneke De Clippel on 30/03/2016.
 */
public class ScanActivity extends AppCompatActivity
{
    private static final String KEY_EVENT_ID = "event_id";

    public static Intent getIntent(Context context, int eventId)
    {
        Intent intent = new Intent(context, ScanActivity.class);
        intent.putExtra(KEY_EVENT_ID, eventId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_scan);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int eventId = this.getIntent().getIntExtra(KEY_EVENT_ID, 0);
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_content, ScanFragment.newInstance(eventId))
                .commit();
    }
}
