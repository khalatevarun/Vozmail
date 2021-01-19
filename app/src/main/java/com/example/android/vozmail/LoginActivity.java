package com.example.android.vozmail;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.gmail.GmailScopes;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static final List<String> SCOPES = Arrays.asList(new String[]{GmailScopes.GMAIL_LABELS});
    String LOG_TAG = "LoginActivity";
    String TAG = "LoginActivity";
    GoogleSignInAccount account;
    String authCode;
    private String client_id;
    private String client_secret;

    /* renamed from: g */
    Globals g = Globals.getInstance();
    GoogleSignInClient mGoogleSignInClient;
    Button signInButton;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(1024);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }
        this.client_id = getResources().getString(R.string.server_client_id);
        this.client_secret = getString(R.string.server_client_secret);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope(GmailScopes.GMAIL_READONLY), new Scope[0]).requestScopes(new Scope(GmailScopes.GMAIL_LABELS), new Scope[0]).requestScopes(new Scope(GmailScopes.GMAIL_MODIFY), new Scope[0]).requestServerAuthCode(this.client_id).requestEmail().build();
        Log.w(this.TAG, "GSO HUA!!!!!!!!");
        this.mGoogleSignInClient = GoogleSignIn.getClient((Activity) this, gso);
        Button button = (Button) findViewById(R.id.sign_in_button);
        this.signInButton = button;
        button.setVisibility(View.INVISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.signIn();
            }
        });
        if(isSignedIn()){

            signIn();
        }
        else{
            button.setVisibility(View.VISIBLE);

        }
    }

    /* access modifiers changed from: private */
    public void signIn() {
        Intent signInIntent = this.mGoogleSignInClient.getSignInIntent();
        g.setGoogleSign(this.mGoogleSignInClient);
        startActivityForResult(signInIntent, 1);
    }

    private void signOut() {
        this.mGoogleSignInClient.signOut().addOnCompleteListener((Activity) this, new OnCompleteListener<Void>() {
            public void onComplete(Task<Void> task) {
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            try {
                GoogleSignInAccount account2 = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                String serverAuthCode = account2.getServerAuthCode();
                this.authCode = serverAuthCode;
                this.g.setauthCode(serverAuthCode);
                this.g.setUserId(account2.getEmail());
                    g.setUserName(account2.getDisplayName());
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject actualData = new JSONObject();
                try {
                    actualData.put("grant_type", "authorization_code");
                    actualData.put("client_id", this.client_id);
                    actualData.put("client_secret", this.client_secret);
                    actualData.put("redirect_uri", "");
                    actualData.put("code", this.authCode);
                } catch (JSONException e) {
                    Log.d("OKHTTP3", "JSON Exception");
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(JSON, actualData.toString());
                try {
                    Log.w("OKHTTP3", "Request Done, got the response:");
                    try {

                        JSONObject Jobject = new JSONObject(client.newCall(new Request.Builder().url("https://www.googleapis.com/oauth2/v4/token").post(body).build()).execute().body().string());
                        String at = Jobject.getString("access_token");
                        this.g.setAcessToken(at);
                        JSONObject jSONObject = Jobject;
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        Log.w("OKHTTP3", at);
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                } catch (IOException e3) {
                    Log.w("OKHTTP3", "Exception while doing request");
                }
            } catch (ApiException e4) {
                e4.printStackTrace();
            }
        }
    }
    private boolean isSignedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){
            return false;
        }
        return true;
    }
}