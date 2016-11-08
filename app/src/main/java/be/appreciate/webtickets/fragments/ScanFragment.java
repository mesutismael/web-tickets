package be.appreciate.webtickets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.webtickets.R;
import be.appreciate.webtickets.api.ApiHelper;
import be.appreciate.webtickets.model.Validation;
import be.appreciate.webtickets.utils.AudioPlayer;
import be.appreciate.webtickets.utils.ConnectivityManager;
import be.appreciate.webtickets.utils.ImageUtils;
import be.appreciate.webtickets.utils.Observer;
import be.appreciate.webtickets.utils.PreferencesHelper;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class ScanFragment extends Fragment implements ZXingScannerView.ResultHandler, View.OnClickListener,
        ConnectivityManager.ConnectivityListener
{
    private Button buttonScan;
    private LinearLayout layoutSuccess;
    private TextView textViewSuccessName;
    private TextView textViewSuccessType;
    private LinearLayout layoutFailure;
    private TextView textViewFailure;
    private TextView textViewOffline;
    private ZXingScannerView scannerView;
    private boolean scanning;
    private int eventId;
    private Handler handler;
    private Runnable callbackDismiss;
    private long scanSuccessDuration;

    private static final long VIBRATE_DURATION_SUCCESS = 200;
    private static final long VIBRATE_DURATION_FAIURE = 1000;
    private static final String KEY_EVENT_ID = "event_id";

    public static ScanFragment newInstance(int eventId)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_EVENT_ID, eventId);

        ScanFragment fragment = new ScanFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        this.buttonScan = (Button) view.findViewById(R.id.button_scan);
        FrameLayout frameLayoutCamera = (FrameLayout) view.findViewById(R.id.frameLayout_camera);
        this.layoutSuccess = (LinearLayout) view.findViewById(R.id.layout_success);
        this.textViewSuccessName = (TextView) view.findViewById(R.id.textView_name);
        this.textViewSuccessType = (TextView) view.findViewById(R.id.textView_type);
        this.layoutFailure = (LinearLayout) view.findViewById(R.id.layout_failure);
        this.textViewFailure = (TextView) view.findViewById(R.id.textView_error);
        this.textViewOffline = (TextView) view.findViewById(R.id.textView_offline);
        TextView textViewSuccessTitle = (TextView) view.findViewById(R.id.textView_successTitle);
        TextView textViewFailureTitle = (TextView) view.findViewById(R.id.textView_failureTitle);

        ImageUtils.setDrawableLeft(textViewSuccessTitle, R.drawable.ic_success, view.getContext());
        ImageUtils.setDrawableLeft(textViewFailureTitle, R.drawable.ic_failure, view.getContext());
        ImageUtils.setDrawableLeft(this.textViewOffline, R.drawable.ic_warning, view.getContext());

        ImageUtils.tintDrawables(textViewSuccessTitle, R.color.scan_feedback_text);
        ImageUtils.tintDrawables(textViewFailureTitle, R.color.scan_feedback_text);
        ImageUtils.tintDrawables(this.textViewOffline, R.color.general_text_error);

        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);
        formats.add(BarcodeFormat.CODE_128);
        this.scannerView = new ZXingScannerView(view.getContext());
        this.scannerView.setFormats(formats);
        frameLayoutCamera.addView(this.scannerView);

        this.buttonScan.setOnClickListener(this);
        this.layoutSuccess.setOnClickListener(this);
        this.layoutFailure.setOnClickListener(this);

        this.eventId = this.getArguments() != null ? this.getArguments().getInt(KEY_EVENT_ID) : 0;

        AudioPlayer.initSoundPool(view.getContext());

        this.handler = new Handler();
        this.callbackDismiss = () -> this.hideSuccess();
        this.scanSuccessDuration = PreferencesHelper.getScanSuccessDuration(view.getContext()) * 1000;

        return view;
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
        this.hideSuccess();

        super.onStop();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        this.scanning = true;
        this.buttonScan.setVisibility(View.INVISIBLE);
        this.scannerView.setResultHandler(this);
        this.scannerView.startCamera();
    }

    @Override
    public void onPause()
    {
        this.scanning = false;
        this.buttonScan.setVisibility(View.VISIBLE);
        this.scannerView.stopCamera();
        super.onPause();
    }

    @Override
    public void handleResult(Result result)
    {
        this.scanning = false;
        this.buttonScan.setVisibility(View.VISIBLE);

        if(this.getContext() != null && this.eventId != 0 && result != null && !TextUtils.isEmpty(result.getText()))
        {
            String key = PreferencesHelper.getUserCode(this.getContext());
            String code = result.getText();
            ApiHelper.validate(this.getContext(), key, this.eventId, code).subscribe(this.validateObserver);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_scan:
                if(!this.scanning)
                {
                    this.scanning = true;
                    this.buttonScan.setVisibility(View.INVISIBLE);
                    this.scannerView.resumeCameraPreview(this);
                }
                this.hideSuccess();
                this.hideError();
                break;

            case R.id.layout_success:
                this.hideSuccess();
                break;

            case R.id.layout_failure:
                this.hideError();
                break;
        }
    }

    @Override
    public void onConnected()
    {
        this.textViewOffline.setVisibility(View.GONE);
    }

    @Override
    public void onDisconnected()
    {
        this.textViewOffline.setVisibility(View.VISIBLE);
    }

    private void showSuccess(String name, String type)
    {
        this.hideError();
        this.layoutSuccess.setVisibility(View.VISIBLE);
        this.textViewSuccessName.setText(name);
        this.textViewSuccessType.setText(type);

        this.handler.postDelayed(this.callbackDismiss, this.scanSuccessDuration);

        AudioPlayer.playSuccessSound();
        this.vibrate(VIBRATE_DURATION_SUCCESS);
    }

    private void showError(String error)
    {
        this.hideSuccess();
        this.layoutFailure.setVisibility(View.VISIBLE);
        this.textViewFailure.setText(error);

        AudioPlayer.playErrorSound();
        this.vibrate(VIBRATE_DURATION_FAIURE);
    }

    private void hideSuccess()
    {
        this.handler.removeCallbacks(this.callbackDismiss);
        this.layoutSuccess.setVisibility(View.GONE);
    }

    private void hideError()
    {
        this.layoutFailure.setVisibility(View.GONE);
    }

    private void vibrate(long duration)
    {
        if(this.getContext() != null)
        {
            Vibrator vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(duration);
        }
    }

    private Observer<Validation> validateObserver = new Observer<Validation>()
    {
        @Override
        public void onNext(Validation validation)
        {
            String name = validation != null ? validation.getName() : null;
            String type = validation != null ? validation.getType() : null;
            ScanFragment.this.showSuccess(name, type);
        }

        @Override
        public void onError(Throwable e)
        {
            ScanFragment.this.showError(e.getMessage());
        }
    };
}
