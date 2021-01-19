package com.example.android.vozmail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.EditorInfo;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.android.vozmail.adapters.inboxAdapter;
import com.example.android.vozmail.api.model.inboxMails;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InboxFragment extends Fragment {
    inboxAdapter InboxAdapter;
    String date_value;
    String from_value;

    /* renamed from: g */
    Globals g = Globals.getInstance();
    String[] ids = new String[10];
    List<inboxMails> inboxMailsList = new ArrayList();
    ListView listView;
    String messageID_value;
    String myResponse;
    View rootView;
    String searchParam = "";
    EditText searchView;
    String snippet;
    String subject_value;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        g.setCurrentFragment("Inbox");
        listView = rootView.findViewById(R.id.inbox_mails_list);

        getList();
        return rootView;
    }

    /* access modifiers changed from: package-private */
    public void getList() {
        String url;
        OkHttpClient client = new OkHttpClient();
        ids = new String[10];
            url = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages?maxResults=10&labelIds=INBOX&key=" + getResources().getString(R.string.api_key);

        client.newCall(new Request.Builder().header("Authorization", "Bearer " + this.g.getAccessToken()).url(url).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    myResponse = response.body().string();
                    try {
                        JSONArray messagesArray = new JSONObject(myResponse).getJSONArray("messages");
                        for (int i = 0; i < messagesArray.length(); i++) {
                            ids[i] = messagesArray.getJSONObject(i).getString("id");
                        }
                        getContent(InboxFragment.this.ids);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    InboxFragment.this.InboxAdapter = new inboxAdapter(InboxFragment.this.getActivity().getApplicationContext(), R.layout.mail_item, InboxFragment.this.inboxMailsList);
                    Log.w("interrupt", "I am b/w inbox adapter init and set");
                   InboxFragment.this.getActivity().runOnUiThread(new Runnable() {
                       public void run() {
                            InboxFragment.this.listView.setVisibility(View.VISIBLE);
                            InboxFragment.this.listView.setAdapter(InboxFragment.this.InboxAdapter);

                           // ((MainActivity)getActivity()).speechRecognizer.startListening(((MainActivity) getActivity()).speechRecognizerIntent);
                           // Log.w("interrupt", "I am after inbox adapter  set");
                        }
                   } );

                }
            }
        });
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    openMail(position);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void getContent(String[] ids2) {
        ArrayList<String> labels;
        JSONObject payload;
        JSONArray headersArray;
        JSONObject part1;
        JSONObject body;
        String encodeText;
        String str;
        String str2;
        String[] strArr = ids2;
        this.inboxMailsList.clear();
        for (int j = 0; j < strArr.length; j++) {
            OkHttpClient client2 = new OkHttpClient();
            String url2 = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages/" + strArr[j] + "?key=" + getResources().getString(R.string.api_key);
            try {
            Response response = client2.newCall(new Request.Builder().
                                                header("Authorization", "Bearer " + this.g.getAccessToken())
                                                .url(url2)
                                                .build())
                                                .execute();
            if (response.isSuccessful()) {

                this.myResponse = response.body().string();


                JSONObject json = new JSONObject(this.myResponse);
                JSONArray labelIds = json.getJSONArray("labelIds");
                labels = new ArrayList<>();


                for (int i = 0; i < labelIds.length(); i++) {
                    labels.add(labelIds.getString(i));
                }


                this.snippet = json.getString("snippet");
                payload = json.getJSONObject("payload");
                headersArray = payload.getJSONArray("headers");
                int i2 = 0;
                while (i2 < headersArray.length()) {
                    JSONObject currentHeader = headersArray.getJSONObject(i2);
                    String name = currentHeader.getString("name");
                    JSONObject json2 = json;
                    if (name.equals("From")) {
                        this.from_value = currentHeader.getString("value");
                    }
                    if (name.equals("Date")) {
                        this.date_value = currentHeader.getString("value");
                    }
                    if (name.equals("Subject")) {
                        this.subject_value = currentHeader.getString("value");
                    }
                    i2++;
                    json = json2;
                }
                StringBuilder sb = new StringBuilder();

                if(payload.has("parts")) {
                    JSONArray partsArray = payload.getJSONArray("parts");
                    part1 = partsArray.getJSONObject(0);
                    body = part1.getJSONObject("body");
                    encodeText = body.getString("data");

                    JSONArray jSONArray = partsArray;
                }

                else {

                    JSONObject body2 = payload.getJSONObject("body");
                    encodeText = body2.getString("data");
                }



                sb.append("Done for ");
                sb.append(j);
                Log.w("Getting mail content", sb.toString());
               // str = this.to_value;


                String str10 = this.date_value;
                String str9 = subject_value;
                this.inboxMailsList.add(new inboxMails(from_value, "", subject_value, date_value, this.snippet, strArr[j], encodeText, labels));
                 }
                } catch (IOException | JSONException e) {

                    e.printStackTrace();
                }
            }
        }

         void openMail(int position){
            inboxMails currentMail = (inboxMails) InboxFragment.this.InboxAdapter.getItem(position);
            String labelStar = "false";
            if(currentMail.getStar()){
                labelStar="true";
            }

            String readSubject = currentMail.getSubject();
            String readText = currentMail.getText();
            String readFrom = currentMail.getFrom();
            String readDate = currentMail.getDate();
            String readId = currentMail.getMessageID();
            Intent intent = new Intent(InboxFragment.this.getActivity(), ReadMailActivity.class);
            intent.putExtra("Subject", readSubject);
            intent.putExtra("Text", readText);
            intent.putExtra("From", readFrom);
            intent.putExtra("Time", readDate);
            intent.putExtra("MessageId", readId);
            intent.putExtra("FromFragment","Inbox");
            intent.putExtra("Starlabel",labelStar);
            InboxFragment.this.startActivity(intent);
            getActivity().finish();
        }
    }
