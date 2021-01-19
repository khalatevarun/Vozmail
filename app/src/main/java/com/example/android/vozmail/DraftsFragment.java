package com.example.android.vozmail;

import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class DraftsFragment extends Fragment {
    public static final Integer RecordAudioRequestCode = 1;
    private FloatingActionButton micButton;
    private ArrayList<String> sample;
    private SpeechRecognizer speechRecognizer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drafts, container, false);
    }
}