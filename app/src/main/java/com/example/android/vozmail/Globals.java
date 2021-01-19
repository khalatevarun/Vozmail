
package com.example.android.vozmail;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Globals {
    private static Globals instance;
    private String acessToken = null;
    private String authCode = null;
    private GoogleSignInClient mSignIn;
    private String userId = null;
    private String userName = null;
    private String ttsText="";
    private ArrayList<String> listofMails = new ArrayList<String>();
    private String CurrentFragment="";
    private HashMap<String, String> labels= new HashMap<String, String>() ;

    private Globals() {
    }

    public static synchronized Globals getInstance() {
        Globals globals;
        synchronized (Globals.class) {
            if (instance == null) {
                instance = new Globals();
            }
            globals = instance;
        }
        return globals;
    }

    public void setAcessToken(String at) {
        this.acessToken = at;
    }

    public String getAccessToken() {
        return this.acessToken;
    }

    public void setauthCode(String ui) {
        this.authCode = ui;
    }

    public String getauthCode() {
        return this.authCode;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String ui) {
        this.userId = ui;
    }

    public GoogleSignInClient getGoogleSign() {
        return this.mSignIn;
    }

    public void setGoogleSign(GoogleSignInClient SignIn) {
        this.mSignIn = SignIn;
    }

    public void setttsText(String mttsText){ this.ttsText=mttsText;}
    public String getTtsText(){ return  this.ttsText;
    }

    public void setUserName(String muserName){
        userName = muserName;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setCurrentFragment(String currentFragment){
        CurrentFragment = currentFragment;
    }

    public String getCurrentFragment(){
        return  CurrentFragment;
    }

    public void setLabelId(String label_name, String label_id){
        labels.put(label_name,label_id);

    }
    public String getLabelId(String label_name){
        return labels.get(label_name);
    }

    public String getEntry(int i){
        return listofMails.get(i);
    }
    public void setEntry(String str){
         listofMails.add(str);
    }
    public void clearEntry(){
        listofMails.clear();
    }
}