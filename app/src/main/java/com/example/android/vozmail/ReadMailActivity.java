package com.example.android.vozmail;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.vozmail.adapters.inboxAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
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
import org.w3c.dom.Text;

public class ReadMailActivity extends AppCompatActivity {
    TextView dateView;
    Button delete;
    Button forward;
    TextView fromView;
    FloatingActionButton micButton;

    /* renamed from: g */
    Globals g = Globals.getInstance();
    Button reply;
    TextView subjectView;
    TextView textView;
    TextToSpeech tts;
     String subject;
    String from,to;
    String fromFragment;

    ImageView star;
    String label;
    String text;
    int listening=1;



     SpeechRecognizer speechRecognizer, speechRecognizer2;
    Intent speechRecognizerIntent,speechRecognizerIntent2;


    /* access modifiers changed from: protected */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);



        star = findViewById(R.id.star);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#202124")));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        getSupportActionBar().setElevation(0.0f);
        label = getIntent().getStringExtra("Starlabel");
        if(label.equals("true")){
            star.setImageResource(R.drawable.filled_star);
        }
        subject = getIntent().getStringExtra("Subject");
         text = getIntent().getStringExtra("Text");
         from = getIntent().getStringExtra("From");
         to = getIntent().getStringExtra("To");
         fromFragment = getIntent().getStringExtra("FromFragment");
        String date = getIntent().getStringExtra("Time");
        final String messageId = getIntent().getStringExtra("MessageId");
        this.subjectView = (TextView) findViewById(R.id.subject);
        this.textView = (TextView) findViewById(R.id.text);
        this.fromView = (TextView) findViewById(R.id.from);
        this.dateView = (TextView) findViewById(R.id.date);
        this.subjectView.setText(subject);
        this.textView.setText(text);
        String[] from_v = from.split("<");
        String[] date_v = date.split(" ");
        micButton = findViewById(R.id.mic);
        if(from_v[0].equals("Me")){
            fromView.setText(to);
        }
       fromView.setText(from_v[0] );
        String ttsText = "This mail is from "+from_v[0]+"... sent at date "+date_v[0] + " " + date_v[1]+" "+date_v[2]+"... with subject "+subject+"... The mail reads as follows. "+text;

        if(from_v[0].isEmpty()){
            String tostr  = "To: "+to.substring(to.indexOf("<")+1,to.indexOf(">"));
           fromView.setText(tostr);
           ttsText = "This mail was sent to "+to+". At date "+date_v[0] + " " + date_v[1]+" "+date_v[2]+". With subject "+subject+". The mail reads as follows. "+text;
       }
     //   fromView.setText(from);
      //  dateView.setText(date);
        dateView.setText((date_v[0] + " " + date_v[1]+" "+date_v[2]));
        this.reply = (Button) findViewById(R.id.reply_button);
        this.forward = (Button) findViewById(R.id.forward_button);
        this.delete = (Button) findViewById(R.id.delete_button);

        g.setttsText(ttsText);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer2 = SpeechRecognizer.createSpeechRecognizer(this);
          speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, "free_form");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 10000);
        speechRecognizerIntent.putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);
        speechRecognizerIntent.putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);

        speechRecognizerIntent2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent2.putExtra(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, "free_form");
        speechRecognizerIntent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent2.putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 10000);
        speechRecognizerIntent2.putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);
        speechRecognizerIntent2.putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                if (tts.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                    tts.setLanguage(Locale.US);
                    tts.setSpeechRate(0.85f);

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (utteranceId.equals("reading")) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Log.d("UI thread", "I am the UI thread");
                                    speechRecognizer.startListening(speechRecognizerIntent);
                                }
                            });
                        }
                        if(utteranceId.equals("hotphrase")){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Log.d("UI thread", "I am the UI thread");
                                    speechRecognizer.stopListening();
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
                Toast.makeText(ReadMailActivity.this, "Sorry! Text To Speech failed...",
                        Toast.LENGTH_LONG).show();
            }
        }
        });
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                tts.speak(g.getTtsText(), 0, (Bundle) null, (String) null);


            }
        });

        this.reply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reply();

            }
        });
        this.forward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                forward();

            }
        });
        this.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ReadMailActivity.this.movetoTrash(messageId);
            }
        });

        speechRecognizer.startListening(speechRecognizerIntent);





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
                speechRecognizer.stopListening();

                Log.d("Continous speech recognition:", "Results detected");



                ArrayList<String> data = results.getStringArrayList("results_recognition");

                Log.d("Continous speech recognition:", "Results detected as: "+data);

                if(data.get(0).contains("hello mail") || data.get(0).contains("hello male") || data.get(0).contains("hello") || data.get(0).contains("mail") || data.get(0).contains("male")) {
                    Log.d("Continous speech recognition:", "Hello mail detected");

                    speechRecognizer.stopListening();

                    tts.speak("Hello there", 0, (Bundle) null, null);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // yourMethod();
                            speechRecognizer2.startListening(speechRecognizerIntent2);

                        }
                    }, 150);   //5 seconds

                }
                else
                {
                    Toast.makeText(ReadMailActivity.this,"Could'nt recognise speech",Toast.LENGTH_SHORT);
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
                    speechRecognizer2.startListening(speechRecognizerIntent2);
                    micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                    micButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark,getTheme())));
                }

            }

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(data.get(0).contains("read")){
                    tts.speak(g.getTtsText(), 0, (Bundle) null, "reading");

                }

                else if(data.get(0).contains("reply")){
                    tts.speak("You can now reply to this mail", 0, (Bundle) null, (String) null);
                    reply();
                }

                else if(data.get(0).contains("forward") || data.get(0).contains("Forward")){
                    tts.speak("You can now forward this mail", 0, (Bundle) null, (String) null);
                    forward();
                }

                else if(data.get(0).contains("delete")){
                    movetoTrash(messageId);
                }
                else if(data.get(0).contains("back")){
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                    finish();
                }

                else if(data.get(0).contains("star") || data.get(0).contains("STAR")){

                    if(data.get(0).contains("add")){
                        addStarLabel(messageId);
                    }
                    if(data.get(0).contains("remove")){
                        removeStarLabel(messageId);
                    }

                }
                else if(data.get(0).contains("label")){
                    String customLabel[] =  data.get(0).split(" ");
                    addCustomLabel(customLabel[2],messageId);
                }

                else{
                    tts.speak("Could not recognize your speech",0,(Bundle)null,(String)null);
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

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    /* access modifiers changed from: package-private */
    public void movetoTrash(String messageId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages/" + messageId + "/modify/?key=" + getResources().getString(R.string.api_key);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject actualData = new JSONObject();
        try {
            actualData.put("addLabelIds", "TRASH");
            actualData.put("removeLabelIds", "INBOX");
        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON Exception");
            e.printStackTrace();
        }
        client.newCall(new Request.Builder().header("Authorization", "Bearer " + this.g.getAccessToken()).url(url).post(RequestBody.create(JSON, actualData.toString())).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            Toast.makeText(ReadMailActivity.this, "Mail deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    void forward(){
        Intent intent = new Intent(ReadMailActivity.this, ForwardActivity.class);
        intent.putExtra("From", ReadMailActivity.this.g.getUserId());
        intent.putExtra("To", "Null");
        intent.putExtra("Subject", subject);
        intent.putExtra("Category", "Fwd");
        intent.putExtra("Body",text);
        startActivity(intent);

    }

    void reply(){
        int s = from.indexOf(60);
        String from_mail = from.substring(s + 1, from.indexOf(62));
        Intent intent = new Intent(ReadMailActivity.this, ForwardActivity.class);
        intent.putExtra("From", ReadMailActivity.this.g.getUserId());
        intent.putExtra("To", from_mail);
        intent.putExtra("Subject", subject);
        intent.putExtra("Category", "Re");
        intent.putExtra("Body","");

        startActivity(intent);

    }

    void addCustomLabel(String label, String messageId){
        String label_id = g.getLabelId(label);
        OkHttpClient client = new OkHttpClient();
        String url = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages/" + messageId + "/modify/?key=" + getResources().getString(R.string.api_key);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject actualData = new JSONObject();
        try {
            actualData.put("addLabelIds", label_id);
        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON Exception");
            e.printStackTrace();
        }
        client.newCall(new Request.Builder().header("Authorization", "Bearer " + this.g.getAccessToken()).url(url).post(RequestBody.create(JSON, actualData.toString())).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            tts.speak("Mail labelled as "+label,0,null,label);
                            Toast.makeText(ReadMailActivity.this, "Mail labelled as "+label, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void addStarLabel(String messageId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages/" + messageId + "/modify/?key=" + getResources().getString(R.string.api_key);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject actualData = new JSONObject();
        try {
            actualData.put("addLabelIds", "STARRED");
        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON Exception");
            e.printStackTrace();
        }
        client.newCall(new Request.Builder().header("Authorization", "Bearer " + this.g.getAccessToken()).url(url).post(RequestBody.create(JSON, actualData.toString())).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            tts.speak("Mail labelled as star",0,null,"label");
                            Toast.makeText(getApplicationContext(), "Mail labelled as star", Toast.LENGTH_SHORT).show();
                            star.setImageResource(R.drawable.filled_star);

                        }
                    });
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void removeStarLabel(String messageId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages/" + messageId + "/modify/?key=" + getResources().getString(R.string.api_key);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject actualData = new JSONObject();
        try {
            actualData.put("removeLabelIds", "STARRED");
        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON Exception");
            e.printStackTrace();
        }
        client.newCall(new Request.Builder().header("Authorization", "Bearer " + this.g.getAccessToken()).url(url).post(RequestBody.create(JSON, actualData.toString())).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Mail labelled as star",Toast.LENGTH_SHORT).show();
                            tts.speak("Star label removed",0,null,"label");
                            star.setImageResource(R.drawable.star);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        speechRecognizer.stopListening();
        listening=0;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(listening==0) {
            speechRecognizer.startListening(speechRecognizerIntent);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReadMailActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}