package be.appreciate.webtickets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import be.appreciate.webtickets.R;
import be.appreciate.webtickets.fragments.SettingsFragment;

/**
 * Created by Inneke De Clippel on 31/03/2016.
 */
public class SettingsActivity extends AppCompatActivity
{
    public static Intent getIntent(Context context)
    {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_content, SettingsFragment.newInstance())
                .commit();
    }
}
