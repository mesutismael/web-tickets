package be.appreciate.webtickets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import be.appreciate.webtickets.R;
import be.appreciate.webtickets.fragments.EventsFragment;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class MainActivity extends AppCompatActivity
{
    public static Intent getIntent(Context context)
    {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_content, EventsFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                this.startActivity(SettingsActivity.getIntent(this));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
