package be.appreciate.webtickets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import be.appreciate.webtickets.BuildConfig;
import be.appreciate.webtickets.R;
import be.appreciate.webtickets.fragments.LoginFragment;
import be.appreciate.webtickets.utils.PreferencesHelper;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Inneke De Clippel on 24/03/2016.
 */
public class LoginActivity extends AppCompatActivity
{
    public static Intent getIntent(Context context)
    {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(!BuildConfig.DEBUG)
        {
            Fabric.with(this, new Crashlytics());
        }
        this.setContentView(R.layout.activity_login);

        if(!TextUtils.isEmpty(PreferencesHelper.getUserCode(this)))
        {
            Intent intent = MainActivity.getIntent(this);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
        }
        else
        {
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout_content, LoginFragment.newInstance())
                    .commit();
        }
    }
}
