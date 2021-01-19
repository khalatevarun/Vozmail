package com.example.android.vozmail;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForwardActivity extends AppCompatActivity {
    String body;
    EditText body_view;
    String category;
    String from;
    EditText from_view;

    /* renamed from: g */
    Globals g = Globals.getInstance();
    String subject;
    EditText subject_view;
    int count=1;

    /* renamed from: to */
    String to;
    EditText to_view;
    String date_inRequest;
    String messageId_inRequest;
    String finalmsg_inRequest;

    TextToSpeech tts;
    String ttsMailid="Who would you like to send this mail to?";
    String ttsSubject = "Please enter the subject of mail";
    String ttsBody ="Please enter the body of the mail";
    String utteranceMailid = "mailId";
    String utteranceSubject ="subject";
    String utteranceBody = "body";
    String ttsReadMail="This mail is being sent to ";



    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_forward);
        from = g.getUserId();

        to_view =  findViewById(R.id.to);
        from_view =  findViewById(R.id.from);
        subject_view =  findViewById(R.id.subject);
        body_view =  findViewById(R.id.body);

        body = getIntent().getStringExtra("Body");
        if(!body.isEmpty()){
            body_view.setText(body);
        }
        ActionBar actionBar = getSupportActionBar();
        this.subject = getIntent().getStringExtra("Subject");
        String stringExtra = getIntent().getStringExtra("Category");
        this.category = stringExtra;
        if (stringExtra.equals("Fwd")) {
            actionBar.setTitle((CharSequence) "Forward");
            String str = "Fwd: " + this.subject;
            subject = str;
            subject_view.setText(str);
        } else {
            actionBar.setTitle((CharSequence) "Reply");
            to = getIntent().getStringExtra("To");
            String str2 = "Re: " + subject;
            subject = str2;
            subject_view.setText(str2);
            to_view.setText(to);
            count=2;
        }
        from_view.setText(from);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#202124")));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0.0f);

        final SpeechRecognizer speechRecognizer, speechRecognizer2,speechRecognizer3;


        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer2 = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer3 = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, "free_form");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 10000);
        speechRecognizerIntent.putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);
        speechRecognizerIntent.putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);

        final Intent speechRecognizerIntent2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent2.putExtra(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, "free_form");
        speechRecognizerIntent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent2.putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 10000);
        speechRecognizerIntent2.putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);
        speechRecognizerIntent2.putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);

        Log.d("TTS ","just before assigning");


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    if (tts.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                        tts.setLanguage(Locale.US);
                    tts.setSpeechRate(0.8f);
                    if(category.equals("Re")) {
                        tts.speak(ttsBody, 0, (Bundle) null, utteranceBody);

                    }
                    else{
                        tts.speak(ttsMailid,0,null,utteranceMailid);
                    }
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {

                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Log.d("TTS ","Done speaking");
                            if(utteranceId.equals(utteranceMailid) || utteranceId.equals(utteranceBody) || utteranceId.equals(utteranceSubject)) {

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("STT3 ","Activating");
                                        speechRecognizer3.startListening(speechRecognizerIntent);
                                    }
                                });

                            }
                            if(utteranceId.equals("ReadMail")){
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("STT3 ","Activating");
                                        speechRecognizer.startListening(speechRecognizerIntent);
                                    }
                                });
                            }
                            if(utteranceId.equals("MailSent")){
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("STT3 ","Activating");
                                        speechRecognizer.startListening(speechRecognizerIntent);
                                    }
                                });

                            }
                            if(utteranceId.equals("reply")){
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("STT3 ","Activating");
                                        speechRecognizer.startListening(speechRecognizerIntent);
                                    }
                                });
                            }
                            if (utteranceId.equals("hotphrase")){
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("STT3 ","Activating");
                                        speechRecognizer2.startListening(speechRecognizerIntent2);
                                    }
                                });

                            }


                        }

                        @Override
                        public void onError(String utteranceId) {

                        }
                    });
                } else if (status == TextToSpeech.ERROR) {
                    Toast.makeText(ForwardActivity.this, "Sorry! Text To Speech failed...",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.d("TTS ","just before speaking");

        //  tts.speak(ttsSubject,0,null,utteranceSubject);
        //  tts.speak(ttsBody,0,null,utteranceBody);



        speechRecognizer3.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList("results_recognition");
                Log.d("Speech recogniser 3 for mail id: ",data.get(0));
                if( count ==1) {
                    Log.d("TTS ", "Activating for body");
                    String recipient = data.get(0).toLowerCase().replace(" ", "");
                    if (recipient.contains("dot")) {
                        recipient.replace("dot", ".");
                    }
                    to_view.setText(recipient);
                    ttsReadMail = ttsReadMail + recipient;

                    // tts.speak(ttsSubject,0,null,utteranceSubject);
                    if(category.equals("Re")) {
                        tts.speak(ttsBody, 0, null, "reply");
                        count++;

                    }
                    else{
                        speechRecognizer.startListening(speechRecognizerIntent);
                    }

                }

                else if(count ==2){
                    String firstCapBody = data.get(0).substring(0,1).toUpperCase()+  data.get(0).substring(1).toLowerCase();
                    String finalBody = firstCapBody.replace(" new line","\n").replace("new paragraph","\n\n");
                    body_view.setText(finalBody);
                    ttsReadMail = ttsReadMail+"The mail reads as follows. "+finalBody;
                    g.setttsText(ttsReadMail);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                else{
                    speechRecognizer.startListening(speechRecognizerIntent);
                }


            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });



        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("Continous speech recognition:", "Ready for speech");

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                String errorText = Integer.toString(error);
                if (error == 6 || error == 7 || error == 1) {

                    speechRecognizer.startListening(speechRecognizerIntent);

                    Log.d("Continous speech recognition:", errorText);

                }
                if(error == 8) {
                    speechRecognizer.cancel();
                    speechRecognizer.startListening(speechRecognizerIntent);
                    Log.d("Continous speech recognition:", errorText);
                }

            }

            @Override
            public void onResults(Bundle results) {
                Log.d("Continous speech recognition:", "Results detected");



                ArrayList<String> data = results.getStringArrayList("results_recognition");

                Log.d("Continous speech recognition:", "Results detected as: "+data);

                if(data.get(0).contains("hello mail") || data.get(0).contains("hello male")) {
                    Log.d("Continous speech recognition:", "Hello mail detected");

                    speechRecognizer.stopListening();

                    tts.speak("Hello there", 0, (Bundle) null, "hotphrase");

                }
                else
                {
                   // Toast.makeText(ForwardActivity.this,"Could'nt recognise speech",Toast.LENGTH_SHORT);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        //  speechRecognizer.startListening(speechRecognizerIntent);

        speechRecognizer2.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                if(error==6 || error == 7 || error == 1){
                    speechRecognizer2.startListening(speechRecognizerIntent);
                    //  micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                    //  micButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark,getTheme())));
                }

            }

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(data.get(0).contains("read")){
                    tts.speak(g.getTtsText(), 0, (Bundle) null, "ReadMail");

                }

                else if(data.get(0).contains("send")) {
                    send();

                }
                else if(data.get(0).contains("back")){
                    Intent i = new Intent(getApplicationContext(),ReadMailActivity.class);
                    startActivity(i);
                    finish();
                }

                else{
                    speechRecognizer.startListening(speechRecognizerIntent);
                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });






    }



        






    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 16908332) {
            onBackPressed();
            return true;
        } else if (itemId != R.id.action_send) {
            return super.onOptionsItemSelected(item);
        } else {
            send();
            return true;
        }
    }

    public void send() {
        from = "From: "+g.getUserName()+" <" + g.getUserId() + ">";
        to = "To: <" + to_view.getText().toString() + ">";
        subject = "Subject: " + subject_view.getText().toString();
        date_inRequest = "Date: Fri, 21 Nov 1997 09:55:06 -0600";
        messageId_inRequest = "Message-ID: <1234@local.machine.example>";
        body = body_view.getText().toString();
        String str = from + "\n" + to + "\n" + subject + "\n" + date_inRequest + "\n" + messageId_inRequest + "\n\n" + body;

        finalmsg_inRequest = encodeString(str);
        OkHttpClient client = new OkHttpClient();
        String url = "https://gmail.googleapis.com/gmail/v1/users/" + g.getUserId() + "/messages/send?key=" + getResources().getString(R.string.api_key);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject actualData = new JSONObject();
        try {
            actualData.put("raw", finalmsg_inRequest);
        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON Exception");
            e.printStackTrace();
        }
        client.newCall(new Request.Builder().header("Authorization", "Bearer " + g.getAccessToken()).url(url).post(RequestBody.create(JSON, actualData.toString())).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            Toast.makeText(ForwardActivity.this, "Mail sent", Toast.LENGTH_LONG).show();
                            tts.speak("Mail sent",0,null,"MailSent");
                        }
                    });
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public String encodeString(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }
}