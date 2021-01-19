package com.example.android.vozmail;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.android.vozmail.api.service.AuthClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final Integer RecordAudioRequestCode = 1;
    /* access modifiers changed from: private */

    /* renamed from: a */
    public static int f335a = 0;

    /* renamed from: AC */
    TextView f336AC;

    /* renamed from: AT */
    TextView f337AT;
    TextView Dresponse;
    TextView UserID;
    /* access modifiers changed from: private */
    public TextView activtyTitle;
    String apikey;
    AuthClient authClient;
    private FloatingActionButton composeButton;
    private DrawerLayout drawerLayout;
    int a=0;



    /* renamed from: g */
    Globals g = Globals.getInstance();
    /* access modifiers changed from: private */
    public GoogleSignInClient mGoogleSignInClient;
    /* access modifiers changed from: private */
    public FloatingActionButton micButton;
    String myResponse;
    String name;
    /* access modifiers changed from: private */
    public SpeechRecognizer speechRecognizer;
    /* access modifiers changed from: private */
    public SpeechRecognizer speechRecognizer2;

    public SpeechRecognizer speechRecognizer3;

    private Timer speechTimeout = null;
    String token;
    /* access modifiers changed from: private */
    public TextToSpeech tts;
    public TextToSpeech tts2;
    String userId;

    String positions[]={"1","2","3","4","5","6","7","8","9","10"};
    int position = 0;

    String fragmentTAG = "Inbox";
    EditText searchView;
    String searchParam="edi";
    String searchingFor="";

     Intent speechRecognizerIntent;
     Intent speechRecognizerIntent2;
     Intent speechRecognizerIntent3;








    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        final NavigationView navigationView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.token = "Bearer " + this.g.getAccessToken();
        this.apikey = getResources().getString(R.string.api_key);
        this.userId = this.g.getUserId();
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.compose);
        this.composeButton = floatingActionButton;
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), ComposeActivity.class));
                finish();
            }
        });

        micButton = findViewById(R.id.mic);

        MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));
        MainActivity.this.composeButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));



        final AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(5, true);
        this.activtyTitle = (TextView) findViewById(R.id.activity_title);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        this.drawerLayout = (DrawerLayout) findViewById(R.id.frag_drawer_layout);
        NavigationView navigationView2 = (NavigationView) findViewById(R.id.frag_nav_view);
        View headerView = navigationView2.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.userID);
        navUsername.setText(this.g.getUserId());
        navigationView2.setNavigationItemSelectedListener(this);
        TextView textView = navUsername;
        DrawerLayout drawerLayout2 = this.drawerLayout;
        View view = headerView;
        NavigationView navigationView3 = navigationView2;
        Toolbar toolbar2 = toolbar;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout2, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator((int) R.drawable.hamburg);

        this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        this.speechRecognizer2 = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer3 = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizerIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        speechRecognizerIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        speechRecognizerIntent.putExtra("android.speech.extra.LANGUAGE", Locale.getDefault());
        speechRecognizerIntent.putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 10000);
        speechRecognizerIntent.putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);
        speechRecognizerIntent.putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);
         speechRecognizerIntent2 = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        speechRecognizerIntent2.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        speechRecognizerIntent2.putExtra("android.speech.extra.LANGUAGE", Locale.getDefault());
        speechRecognizerIntent2.putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 10000);
        speechRecognizerIntent2.putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);
        speechRecognizerIntent2.putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);

        speechRecognizerIntent3 = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        speechRecognizerIntent3 = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        speechRecognizerIntent3.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        speechRecognizerIntent3.putExtra("android.speech.extra.LANGUAGE", Locale.getDefault());
        speechRecognizerIntent3.putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 10000);
        speechRecognizerIntent3.putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);
        speechRecognizerIntent3.putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 10000);






       if (savedInstanceState == null && g.getCurrentFragment().isEmpty()) {
            navigationView = navigationView3;
            navigationView.setCheckedItem(R.id.nav_inbox);
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new InboxFragment()).commit();




       } else {
            navigationView = navigationView3;
            if(g.getCurrentFragment().equals("Inbox")){
                navigationView.setCheckedItem(R.id.nav_inbox);
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new InboxFragment()).commit();


            }

                else if(g.getCurrentFragment().equals("Sent")){
                navigationView.setCheckedItem(R.id.nav_sent);

                getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new SentFragment()).commit();
                   this.activtyTitle.setText("SENT");


            }
               else if(g.getCurrentFragment().equals("Star")){
                   navigationView.setCheckedItem(R.id.nav_star);
                   getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new StarFragment()).commit();
                   this.activtyTitle.setText("STAR");

            }
               else if(g.getCurrentFragment().equals("Spam")){
                   navigationView.setCheckedItem(R.id.nav_spam);
                   getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new SpamFragment()).commit();
                   this.activtyTitle.setText("SPAM");

            }
               else if(g.getCurrentFragment().equals("Trash")){
                   navigationView.setCheckedItem(R.id.nav_trash);
                   getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new TrashFragment()).commit();
                   this.activtyTitle.setText("TRASH");

            }

               else {

            }

           }

        searchView = findViewById(R.id.searchMail);

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                if (actionId != EditorInfo.IME_ACTION_SEARCH) {

                    searchParam = searchView.getText().toString();
                    searchingFor="keyword";
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new SearchFragment()).commit();
                    activtyTitle.setText("RESULTS IN MAIL");

                    // return true;
                }
                return false;
            }

        });




        if (ActivityCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != 0) {
            checkPermission();
        }


          this.tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status != -1) {
                    MainActivity.this.tts.setLanguage(Locale.US);
                }
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if(utteranceId.equals("hotphrase detected") )
                        {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Log.d("UI thread", "I am the UI thread");
                                    speechRecognizer2.startListening(speechRecognizerIntent2);
                                }
                            });

                    }
                        if(utteranceId.equals("reading")){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Log.d("UI thread", "I am the UI thread");
                                    speechRecognizer3.startListening(speechRecognizerIntent3);
                                }
                            });

                        }
                        if(utteranceId.equals("listen again")){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Log.d("UI thread", "I am the UI thread");
                                    speechRecognizer.startListening(speechRecognizerIntent);
                                }
                            });
                        }


                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
            }
        });






        this.speechRecognizer2.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle params) {
            }

            public void onBeginningOfSpeech() {
            }

            public void onRmsChanged(float rmsdB) {
            }

            public void onBufferReceived(byte[] buffer) {
            }

            public void onEndOfSpeech() {
            }

            public void onError(int error) {
                if (error == 6 || error == 7 || error == 1) {
                    MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);
                    MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                    MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));
                }
            }

            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList("results_recognition");
                if (data.get(0).contains("spam")) {
                    navigationView.setCheckedItem(R.id.nav_spam);
                    MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new SpamFragment()).commit();
                    MainActivity.this.activtyTitle.setText("SPAM");
                    MainActivity.this.tts.speak("Loading spam mails", 0, (Bundle) null, "listen again");
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        public void run() {
                            amanager.setStreamMute(5, true);
                            MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);
                        }
                    }, 1200);
                    int unused = MainActivity.f335a = 0;
                    MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                    MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));

                }
                 else if(data.get(0).contains("read")) {


                    MainActivity.this.tts.speak(g.getEntry(a), 0, (Bundle) null, "reading");

                    MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                    MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));

                }
                  else if(data.get(0).contains("Compose") || data.get(0).contains("compose")) {


                    MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), ComposeActivity.class));
                    finish();
                }

                            else if (data.get(0).contains("trash")) {
                            navigationView.setCheckedItem((int) R.id.nav_trash);
                            MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new TrashFragment()).commit();
                            MainActivity.this.activtyTitle.setText("TRASH");
                            MainActivity.this.tts.speak("Loading trash mails", 0, (Bundle) null, "listen again");
                     /**       new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                public void run() {
                                    amanager.setStreamMute(5, true);
                                    MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);
                                }
                            }, 1200);  **/
                            int unused2 = MainActivity.f335a = 0;
                            MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                            MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));

                        }

                                else if (data.get(0).contains("inbox")) {
                                    navigationView.setCheckedItem((int) R.id.nav_inbox);
                                    MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new InboxFragment()).commit();
                                    MainActivity.this.activtyTitle.setText("INBOX");
                                    MainActivity.this.tts.speak("Loading inbox mails", 0, (Bundle) null, "listen again");
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        public void run() {
                                            amanager.adjustStreamVolume(5, -100, 0);
                                            MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);
                                        }
                                    }, 1500);
                                    int unused3 = MainActivity.f335a = 0;
                                    MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                                    MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));

                                }

                                else if (data.get(0).contains("Star") || data.get(0).contains("star")) {
                                    navigationView.setCheckedItem(R.id.nav_star);
                                    MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new StarFragment()).commit();
                                    MainActivity.this.activtyTitle.setText("STAR");
                                    MainActivity.this.tts.speak("Loading star mails", 0, (Bundle) null, "listen again");
                                  /**  new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        public void run() {
                                            amanager.adjustStreamVolume(5, -100, 0);
                                            MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);
                                        }
                                    }, 1500); **/
                                    int unused4 = MainActivity.f335a = 0;
                                    MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                                    MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));

                                }
                                else if(data.get(0).contains("sent")) {
                    navigationView.setCheckedItem(R.id.nav_sent);
                    MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new SentFragment()).commit();
                    MainActivity.this.activtyTitle.setText("SENT");
                    MainActivity.this.tts.speak("Loading sent mails", 0, (Bundle) null, "listen again");
                 /**   new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        public void run() {
                            amanager.setStreamMute(5, true);
                            MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);
                        }
                    }, 1200); **/
                    int unused6 = MainActivity.f335a = 0;
                    MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                    MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));
                }
                                else if(data.get(0).contains("drafts")) {

                    navigationView.setCheckedItem((int) R.id.nav_drafts);
                    MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new DraftsFragment()).commit();
                    MainActivity.this.activtyTitle.setText("DRAFTS");
                    MainActivity.this.tts.speak("Loading draft mails", 0, (Bundle) null, "listen again");
                  /**  new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        public void run() {
                            amanager.setStreamMute(5, true);
                            MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);
                        }
                    }, 1200);  **/
                    int unused7 = MainActivity.f335a = 0;
                    MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                    MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));

                }
                                 else if(data.get(0).contains("Create label") || data.get(0).contains("create label")) {

                  //  navigationView.setCheckedItem((int) R.id.nav_drafts);
                  //  MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new DraftsFragment()).commit();
                  //  MainActivity.this.activtyTitle.setText("DRAFTS");
                  //  MainActivity.this.tts.speak("Loading draft mails", 0, (Bundle) null, "listen again");
                  /**  new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        public void run() {
                            amanager.setStreamMute(5, true);
                            MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);
                        }
                    }, 1200);  **/
                   // int unused7 = MainActivity.f335a = 0;
                    String result[] = data.get(0).split(" ");
                    creatLabel(result[2]);

                    MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                    MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));

                }
                                  else if(data.get(0).contains("sign out") || data.get(0).contains("Sign out")) {

                                      GoogleSignInClient mGoogleSignInClient = g.getGoogleSign();
                                     mGoogleSignInClient.signOut();
                                    Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(i);
                                    finish();



                }


                                else if(data.get(0).contains("open")){
                    Log.w("Open mail",data.get(0));



                                    String currentFrag = getFragmentTag();

                                    String positionStr[] = data.get(0).split(" ");
                                    position = Integer.parseInt(positionStr[4]);
                                    openMailViaVoice(position-1,currentFrag);


                }
                                  else if (data.get(0).contains("Search for") || data.get(0).contains("search for")) {

                            //        navigationView.setCheckedItem(R.id.nav_star);
                                      String paramter[] = data.get(0).split(" ");
                                      String p="";
                                      if(data.get(0).contains("label")){
                                          for (int i = 3; i < paramter.length; i++) {
                                              p = p  + paramter[i];
                                          }
                                          searchParam = p;
                                          searchingFor="label";


                                      }
                                      else {
                                          for (int i = 2; i < paramter.length; i++) {
                                              p = p + " " + paramter[i];
                                          }
                                          searchParam = p;
                                          searchingFor="keyword";
                                      }
                                        searchView.setText(p);
                                    MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new SearchFragment()).commit();
                                    MainActivity.this.activtyTitle.setText("RESULTS IN MAIL");
                                    MainActivity.this.tts.speak("Loading search mails", 0, (Bundle) null, "listen again");
                           /**         new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        public void run() {
                                            amanager.adjustStreamVolume(5, -100, 0);
                                            MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);
                                        }
                                    }, 1500);  **/
                                    int unused4 = MainActivity.f335a = 0;
                                    MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                                    MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));

                                }


                                else {
                                    MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                                    MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.dark)));
                                    MainActivity.this.tts.speak("There are no such mails", 0, (Bundle) null, "listen again");
                                 /**   new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        public void run() {
                                            amanager.adjustStreamVolume(5, -100, 0);
                                            MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);
                                        }
                                    }, 1200); **/
                                    int unused5 = MainActivity.f335a = 0;
                                    speechRecognizer.startListening(speechRecognizerIntent);

                                }




                    }




            public void onPartialResults(Bundle partialResults) {
            }

            public void onEvent(int eventType, Bundle params) {
            }
        });
        this.speechRecognizer.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle params) {
                Log.w("Speech Recogniser 1","Ready for speech");
            }

            public void onBeginningOfSpeech() {
            }

            public void onRmsChanged(float rmsdB) {
            }

            public void onBufferReceived(byte[] buffer) {
            }

            public void onEndOfSpeech() {
            }

            public void onError(int error) {
                Log.w("Speech Recogniser 1","Error "+Integer.toString(error));


                    MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);

            }

            public void onResults(Bundle results) {

                MainActivity.this.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                ArrayList<String> data = results.getStringArrayList("results_recognition");
                Log.w("Speech Recogniser 1",data.get(0));

                if(data.get(0).contains("hello") || data.get(0).contains("hello mail") || data.get(0).contains("mail") || data.get(0).contains("hello mil")){
                    speechRecognizer.stopListening();
                    MainActivity.this.tts.speak("Hello there", 0, (Bundle) null, "hotphrase detected");
                    MainActivity.this.micButton.setImageResource(R.drawable.mic_on);
                    MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.colorAccent)));

                   // speechRecognizer2.startListening(speechRecognizerIntent2);

                }
                else {
                    speechRecognizer.startListening(speechRecognizerIntent);
                }

         /**       if (((!data.get(0).contains("hello") ||  !data.get(0).contains("mail")) )) {
                    int unused = MainActivity.f335a = 0;
                    amanager.adjustStreamVolume(5, -100, 0);
                /**    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        public void run() {
                            MainActivity.this.speechRecognizer.startListening(speechRecognizerIntent);
                        }
                    }, 150);  **/
            /**        return;
                }
                MainActivity.this.tts.speak("Hello there", 0, (Bundle) null, "hotphrase detected");
                int unused2 = MainActivity.f335a = 1;
                MainActivity.this.micButton.setImageResource(R.drawable.mic_on);
                MainActivity.this.micButton.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getResources().getColor(R.color.colorAccent)));
                amanager.adjustStreamVolume(5, -100, 0);
                MainActivity.this.speechRecognizer.stopListening();
                **/
             /**   new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    public void run() {
                        MainActivity.this.speechRecognizer2.startListening(speechRecognizerIntent2);
                    }
                }, 150);  **/
            }

            public void onPartialResults(Bundle partialResults) {
            }

            public void onEvent(int eventType, Bundle params) {
            }
        });
        speechRecognizer3.setRecognitionListener(new RecognitionListener() {


            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.w("Speech Recogniser 3  ","READY FOR SPEECH");

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
                speechRecognizer3.startListening(speechRecognizerIntent3);
                Log.w("Speech Recogniser 3 ERROR ",Integer.toString(error));

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList("results_recognition");
                Log.w("Speech Recogniser 3 RESULTS ",data.get(0));
                if(data.get(0).contains("next")){
                    a++;
                    tts.speak(g.getEntry(a),0,null,"reading");
                }
                else if(data.get(0).contains("open")){
                    Log.w("Speech Recogniser 3  ","OPEN DETECTED");

                    openMailViaVoice(a,getFragmentTag());
                }
                else if(data.get(0).contains("stop")){
                    Log.w("Speech Recogniser 3  ","STOP DETECTED");

                    // do nothing
                    speechRecognizer.startListening(speechRecognizerIntent);
                    a=0;
                }
                else {
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





        this.micButton = (FloatingActionButton) findViewById(R.id.mic);
        speechRecognizer.startListening(speechRecognizerIntent);
    }




    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_drafts /*2131230974*/:
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new DraftsFragment(),"Drafts").commit();
                this.activtyTitle.setText("DRAFTS");
                break;
            case R.id.nav_inbox /*2131230975*/:
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new InboxFragment(),"Inbox").commit();
                this.activtyTitle.setText("INBOX");
                break;
            case R.id.nav_logout /*2131230976*/:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage((CharSequence) "Are you sure you want to log out of your account?");
                dialog.setTitle((CharSequence) "Log Out?");
                dialog.setPositiveButton((CharSequence) "YES", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       mGoogleSignInClient.signOut();
                       startActivity(new Intent(MainActivity.this, LoginActivity.class));
                       finish();
                    }
                });
                dialog.setNegativeButton((CharSequence) "NO", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.create().show();
                break;
            case R.id.nav_sent /*2131230977*/:
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new SentFragment()).commit();
                this.activtyTitle.setText("SENT");
                break;
            case R.id.nav_share /*2131230978*/:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.putExtra("android.intent.extra.SUBJECT", "");
                intent.putExtra("android.intent.extra.TEXT", " ");
                intent.setType("text/plain");
                startActivity(intent);
                break;
            case R.id.nav_spam /*2131230979*/:
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new SpamFragment()).commit();
                this.activtyTitle.setText("SPAM");
                break;
            case R.id.nav_star /*2131230980*/:
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new StarFragment()).commit();
                this.activtyTitle.setText("STAR");
                break;
            case R.id.nav_trash /*2131230981*/:
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_fragment_container, new TrashFragment()).commit();
                this.activtyTitle.setText("TRASH");
                break;
        }
        this.drawerLayout.closeDrawer((int) GravityCompat.START);
        return true;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.RECORD_AUDIO"}, RecordAudioRequestCode.intValue());
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode.intValue() && grantResults.length > 0 && grantResults[0] == 0) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
        speechRecognizer2.destroy();
    }



    void openMailViaVoice(int position, String tag){


        if(tag.equals("Inbox")){
            InboxFragment inboxFragment = (InboxFragment) getSupportFragmentManager().getFragments().get(0);
            inboxFragment.openMail(position);
        }
        else if(tag.equals("Sent")){
            SentFragment sentFragment = (SentFragment) getSupportFragmentManager().getFragments().get(0);
            sentFragment.openMail(position);
        }
        else if(tag.equals("Trash")){
            TrashFragment trashFragment = (TrashFragment) getSupportFragmentManager().getFragments().get(0);
            trashFragment.openMail(position);
        }
        else if(tag.equals("Spam")){
            SpamFragment spamFragment = (SpamFragment) getSupportFragmentManager().getFragments().get(0);
            spamFragment.openMail(position);
        }
        else if(tag.equals("Star")){
            StarFragment starFragment = (StarFragment) getSupportFragmentManager().getFragments().get(0);
            starFragment.openMail(position);
        }
        else{

        }

    }

    void creatLabel(String label_name){

        OkHttpClient client = new OkHttpClient();
      //  String url = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages/" + messageId + "/modify/?key=" + getResources().getString(R.string.api_key);
       String url =  "https://gmail.googleapis.com/gmail/v1/users/"+g.getUserId()+"/labels/?alt=json&key=" + getResources().getString(R.string.api_key);



        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject actualData = new JSONObject();
        try {
            actualData.put("labelListVisibility", "labelShow");
            actualData.put("messageListVisibility", "show");
            actualData.put("name",label_name);
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
                    myResponse = response.body().string();
                    try {
                        JSONObject json = new JSONObject(myResponse);
                        String label_id = json.getString("id");
                   //     tokenManager.storeLabel(label_name,label_id);
                       g.setLabelId(label_name,label_id);
                        tts.speak(label_name+ " label created",0,null,"listen again");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }

    void readMails(){






    }

    public String getFragmentTag(){
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frag_fragment_container);
        if(currentFragment instanceof InboxFragment)
        {    fragmentTAG="Inbox";}
        else if(currentFragment instanceof SentFragment){
            fragmentTAG = "Sent";
        }
        else if(currentFragment instanceof SpamFragment){
            fragmentTAG = "Spam";
        }
        else if(currentFragment instanceof TrashFragment){
            fragmentTAG = "Trash";
        }
        else if(currentFragment instanceof StarFragment){
            fragmentTAG = "Star";
        }
        else{
            fragmentTAG="Inbox";
        }

        return fragmentTAG;

    }

   

}