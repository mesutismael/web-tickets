package be.appreciate.webtickets.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import be.appreciate.webtickets.R;
import be.appreciate.webtickets.activities.MainActivity;
import be.appreciate.webtickets.api.ApiHelper;
import be.appreciate.webtickets.utils.DialogUtils;
import be.appreciate.webtickets.utils.Observer;
import be.appreciate.webtickets.utils.PreferencesHelper;

/**
 * Created by Inneke De Clippel on 24/03/2016.
 */
public class LoginFragment extends Fragment implements View.OnClickListener
{
    private EditText editTextUsername;
    private MaterialDialog dialogProgress;
    private String userCode;

    public static LoginFragment newInstance()
    {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view  = inflater.inflate(R.layout.fragment_login, container, false);

        this.editTextUsername = (EditText) view.findViewById(R.id.editText_username);
        Button buttonLogIn = (Button) view.findViewById(R.id.button_logIn);
        Button buttonRegister = (Button) view.findViewById(R.id.button_register);

        buttonLogIn.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v)
    {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        switch (v.getId())
        {
            case R.id.button_logIn:
                this.userCode = this.editTextUsername.getText().toString();

                if(TextUtils.isEmpty(this.userCode))
                {
                    DialogUtils.makeErrorDialog(v.getContext(), R.string.login_incomplete_error).show();
                }
                else
                {
                    this.dialogProgress = DialogUtils.makeProgressDialog(v.getContext(), R.string.login_progress);
                    this.dialogProgress.show();
                    ApiHelper.getEvents(v.getContext(), this.userCode).subscribe(this.loginObserver);
                }
                break;

            case R.id.button_register:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://webtickets.be"));
                this.startActivity(intent);
                break;
        }
    }

    private void startNextActivity()
    {
        if(this.getContext() != null)
        {
            Intent intent = MainActivity.getIntent(this.getContext());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        }
    }

    private Observer<Object> loginObserver = new Observer<Object>()
    {
        @Override
        public void onCompleted()
        {
            DialogUtils.dismissDialog(LoginFragment.this.dialogProgress);

            if(LoginFragment.this.getContext() != null)
            {
                PreferencesHelper.saveUserCode(LoginFragment.this.getContext(), LoginFragment.this.userCode);
            }

            LoginFragment.this.startNextActivity();
        }

        @Override
        public void onError(Throwable e)
        {
            DialogUtils.dismissDialog(LoginFragment.this.dialogProgress);

            if(LoginFragment.this.getContext() != null && DialogUtils.canShowDialog(LoginFragment.this))
            {
                DialogUtils.makeErrorDialog(LoginFragment.this.getContext(), R.string.login_login_error).show();
            }
        }
    };
}
