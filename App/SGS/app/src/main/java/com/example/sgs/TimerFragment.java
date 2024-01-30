package com.example.sgs;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.Timer;

public class TimerFragment extends Fragment {
    EditText edtHrs,edtMin,edtSec;
    Button btnStart,btnStop,btnSilent;
    TextView txtCountDown;
    CountDownTimer timer;
    long countTime;
    MediaPlayer alarm;
    CardView cardTimer;

    public TimerFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_timer,container,false);
        edtHrs = view.findViewById(R.id.edtHrs);
        edtMin = view.findViewById(R.id.edtMin);
        edtSec = view.findViewById(R.id.edtSec);
        btnStart = view.findViewById(R.id.btnStart);
        btnStop = view.findViewById(R.id.btnStop);
        btnSilent = view.findViewById(R.id.btnSilent);
        txtCountDown = view.findViewById(R.id.txtCountDown);
        cardTimer = view.findViewById(R.id.cardTimer);
        alarm = MediaPlayer.create(getContext(), Settings.System.DEFAULT_ALARM_ALERT_URI);
        alarm.setLooping(true);

        ///////////////////////////////////////////////////////////////////////////

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hrsText = edtHrs.getText().toString().isEmpty() ? "0" : edtHrs.getText().toString();
                String minText = edtMin.getText().toString().isEmpty() ? "0" : edtMin.getText().toString();
                String secText = edtSec.getText().toString().isEmpty() ? "0" : edtSec.getText().toString();

                try {
                    countTime = (Integer.parseInt(hrsText) * 3600 * 1000L) +
                            (Integer.parseInt(minText) * 60 * 1000L) +
                            (Integer.parseInt(secText) * 1000L);
                    startTime();
                } catch (NumberFormatException e) {
                    // Handle the exception, e.g., show a toast indicating invalid input
                    Toast.makeText(getContext(), "Invalid input. Please enter valid numbers.", Toast.LENGTH_SHORT).show();
                }

                edtMin.setInputType(InputType.TYPE_NULL);
                edtSec.setInputType(InputType.TYPE_NULL);
                edtHrs.setInputType(InputType.TYPE_NULL);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        btnSilent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.stop();
                btnSilent.setVisibility(View.INVISIBLE);
                cardTimer.setVisibility(View.INVISIBLE);
            }
        });


        //////////////////////////////////////////////////////////////////////////
        return view;
    }

    private void stopTimer() {
        txtCountDown.setText("00:00:00");
        cardTimer.setVisibility(View.INVISIBLE);
        alarm.stop();

        timer.cancel();
        edtHrs.setText("");
        edtMin.setText("");
        edtSec.setText("");

        edtMin.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtSec.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtHrs.setInputType(InputType.TYPE_CLASS_NUMBER);

        edtHrs.setFocusable(true);
        edtMin.setFocusable(true);
        edtSec.setFocusable(true);
    }

    private void startTime() {
        cardTimer.setVisibility(View.VISIBLE);
        timer = new CountDownTimer(countTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / 1000;
                long sec = totalSeconds % 60;
                long min = (totalSeconds / 60) % 60;
                long hrs = totalSeconds / 3600;

                String timeFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d",hrs,min,sec);
                txtCountDown.setText(timeFormatted);
                edtHrs.setText("");
                edtMin.setText("");
                edtSec.setText("");
            }

            @Override
            public void onFinish() {

                btnSilent.setVisibility(View.VISIBLE);
                txtCountDown.setText("00:00:00");
                Toast.makeText(getContext(), "Time's up", Toast.LENGTH_SHORT).show();
                alarm.start();
                edtHrs.setText("");
                edtMin.setText("");
                edtSec.setText("");

                edtMin.setInputType(InputType.TYPE_CLASS_NUMBER);
                edtSec.setInputType(InputType.TYPE_CLASS_NUMBER);
                edtHrs.setInputType(InputType.TYPE_CLASS_NUMBER);

            }
        }.start();
    }
}