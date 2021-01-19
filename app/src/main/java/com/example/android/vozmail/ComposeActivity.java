package com.example.android.vozmail;

import  android.content.Intent;
import android.content.res.ColorStateList;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import org.json.JSONException;
import org.json.JSONObject;

public class ComposeActivity extends AppCompatActivity {
    String body;
    EditText bodyView;
    String completemssg;
    String date;
    String finalmsg;
    String from;
    EditText fromView;

    /* renamed from: g */
    Globals g = Globals.getInstance();
    String messageId;
    String subject;
    EditText subjectView;

    /* renamed from: to */
    String to;
    EditText toView;

    TextToSpeech tts;
    String ttsMailid="Who would you like to send this mail to?";
    String ttsSubject = "Please enter the subject of mail";
    String ttsBody ="Please enter the body of the mail";
    String utteranceMailid = "mailId";
    String utteranceSubject ="subject";
    String utteranceBody = "body";
    int count=1;
    String ttsReadMail="This mail is being sent to ";
    String finalSubject;
    String recipient;
    FloatingActionButton mic;


    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        this.toView = (EditText) findViewById(R.id.to_compose);
        this.fromView = (EditText) findViewById(R.id.from_compose);
        this.subjectView = (EditText) findViewById(R.id.subject_compose);
        this.bodyView = (EditText) findViewById(R.id.body_compose);
        this.fromView.setText(g.getUserId());

        mic = findViewById(R.id.mic);
        mic.setBackgroundTintList(ColorStateList.valueOf(ComposeActivity.this.getResources().getColor(R.color.dark)));


        final SpeechRecognizer speechRecognizer, speechRecognizer2,speechRecognizer3;


        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#202124")));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Compose");
        getSupportActionBar().setElevation(0.0f);

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
                    tts.speak(ttsMailid,0,(Bundle) null,utteranceMailid);
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



                        }

                        @Override
                        public void onError(String utteranceId) {

                        }
                    });
                } else if (status == TextToSpeech.ERROR) {
                    Toast.makeText(ComposeActivity.this, "Sorry! Text To Speech failed...",
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
                if( count ==1){
                    Log.d("TTS ","Activating for subject");
                     recipient = data.get(0).toLowerCase().replace(" ","");
                    if(recipient.contains("dot")){
                        recipient.replace("dot",".");
                    }
                    toView.setText(recipient);
                    ttsReadMail = ttsReadMail+recipient;

                    tts.speak(ttsSubject,0,null,utteranceSubject);

                    count++;
                }
               else if(count ==2){
                    Log.d("TTS ","Activating for body");
                    String finalSubject = data.get(0).substring(0,1).toUpperCase()+  data.get(0).substring(1).toLowerCase();
                    subjectView.setText(finalSubject);
                    ttsReadMail = ttsReadMail+" with subject "+finalSubject+". ";

                    tts.speak(ttsBody,0,null,utteranceBody);
                    count++;
                }
               else{
                    String firstCapBody = data.get(0).substring(0,1).toUpperCase()+  data.get(0).substring(1).toLowerCase();
                    String finalBody = firstCapBody.replace(" new line","\n").replace("new paragraph","\n\n");
                    bodyView.setText(finalBody);
                    ttsReadMail = ttsReadMail+"The mail reads as follows. "+finalBody;
                    g.setttsText(ttsReadMail);
                    count--;
                    mic.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark)));
                    mic.setImageResource(R.drawable.ic_baseline_mic_24);


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

                    mic.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    mic.setImageResource(R.drawable.mic_on);


                    tts.speak("Hello there", 0, (Bundle) null, (String) null);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        public void run() {
                            speechRecognizer2.startListening(speechRecognizerIntent2);
                        }
                    }, 500);
                }
                else
                {
                    Toast.makeText(ComposeActivity.this,"Could'nt recognise speech",Toast.LENGTH_SHORT);
                    speechRecognizer2.startListening(speechRecognizerIntent2);
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
                    String text = "This mail is being send to "+ toView.getText().toString()+". With Subject "+ subjectView.getText().toString()+". It reads as follows "+bodyView.getText().toString();

                    tts.speak(text,0,null,"ReadMail");
                }

                else if(data.get(0).contains("send")) {
                    send();

                }
                else if(data.get(0).contains("back")){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
            onBackPressed();

            return true;
        } else if (itemId != R.id.action_send) {
            return super.onOptionsItemSelected(item);
        } else {
            send();
            return true;
        }
    }



    /* access modifiers changed from: package-private */
    public void send() {
        from = "From: <" + g.getUserId() + ">";
        to = "To: <" + toView.getText().toString() + ">";
        subject = "Subject: " + subjectView.getText().toString();
        date = "Date: Fri, 21 Nov 1997 09:55:06 -0600";
        messageId = "Message-ID: <1234@local.machine.example>";
        body = bodyView.getText().toString();
        String str = from + "\n" + to + "\n" + subject + "\n" + date + "\n" + messageId + "\n\n" + body;
        this.completemssg = str;
        this.finalmsg = encodeString(str);
        OkHttpClient client = new OkHttpClient();
        String url = "https://gmail.googleapis.com/gmail/v1/users/" + g.getUserId() + "/messages/send?key=" + getResources().getString(R.string.api_key);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject actualData = new JSONObject();
        try {
            actualData.put("raw", this.finalmsg);
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
                            Toast.makeText(ComposeActivity.this, "Mail sent", Toast.LENGTH_LONG).show();
                            tts.speak("Mail sent",0,null,"MailSent");
                        }
                    });
                }
                else{
                    tts.speak("There was a problem while sending the mail. Please try again",0,null,"MailSent");
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public String encodeString(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
    }
}