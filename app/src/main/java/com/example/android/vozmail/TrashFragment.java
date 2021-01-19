package com.example.android.vozmail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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

public class TrashFragment extends Fragment {
    inboxAdapter TrashAdapter;
    String date_value;
    String from_value;

    /* renamed from: g */
    Globals g = Globals.getInstance();
    String[] ids = new String[10];
    ListView listView;
    String messageID_value;
    String myResponse;
    View rootView;
    String snippet;
    String subject_value;
    List<inboxMails> trashMailsList = new ArrayList();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_trash, container, false);
        g.setCurrentFragment("Trash");

        this.rootView = inflate;
        this.listView = (ListView) inflate.findViewById(R.id.trash_mails_list);
        new OkHttpClient().newCall(new Request.Builder().header("Authorization", "Bearer " + this.g.getAccessToken()).url("https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages?maxResults=10&labelIds=TRASH&key=" + getResources().getString(R.string.api_key)).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    TrashFragment.this.myResponse = response.body().string();
                    try {
                        JSONArray messagesArray = new JSONObject(TrashFragment.this.myResponse).getJSONArray("messages");
                        for (int i = 0; i < messagesArray.length(); i++) {
                            TrashFragment.this.ids[i] = messagesArray.getJSONObject(i).getString("id");
                        }
                        TrashFragment.this.getContent(TrashFragment.this.ids);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TrashFragment.this.TrashAdapter = new inboxAdapter(TrashFragment.this.getActivity().getApplicationContext(), R.layout.mail_item, TrashFragment.this.trashMailsList);
                    Log.w("interrupt", "I am b/w inbox adapter init and set");
                    TrashFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            TrashFragment.this.listView.setAdapter(TrashFragment.this.TrashAdapter);
                        //    ((MainActivity)getActivity()).speechRecognizer.startListening(((MainActivity) getActivity()).speechRecognizerIntent);

                         //   Log.w("interrupt", "I am after inbox adapter  set");
                        }
                    });
                }
            }
        });
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
              openMail(position);
            }
        });
        return this.rootView;
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
        for (int j = 0; j < strArr.length; j++) {
            OkHttpClient client2 = new OkHttpClient();
            String url2 = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages/" + strArr[j] + "?key=" + getResources().getString(R.string.api_key);
            try {
                Response response = client2.newCall(new Request.Builder().header("Authorization", "Bearer " + this.g.getAccessToken()).url(url2).build()).execute();
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
                    JSONArray partsArray = payload.getJSONArray("parts");
                    part1 = partsArray.getJSONObject(0);
                    body = part1.getJSONObject("body");
                    encodeText = body.getString("data");
                    StringBuilder sb = new StringBuilder();
                    JSONArray jSONArray = partsArray;
                    sb.append("Done for ");
                    sb.append(j);
                    Log.w("Getting mail content", sb.toString());
                    str = this.from_value;
                    str2 = this.subject_value;
                    OkHttpClient okHttpClient3 = client2;


                    JSONObject jSONObject = body;
                    JSONObject jSONObject2 = part1;
                    JSONArray jSONArray2 = headersArray;
                    String str7 = this.date_value;
                    JSONObject jSONObject3 = payload;
                    this.trashMailsList.add(new inboxMails(str, "", str2, str7, this.snippet, strArr[j], encodeText, labels));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void openMail(int position){
        inboxMails currentMail = (inboxMails) TrashFragment.this.TrashAdapter.getItem(position);
        String labelStar = "false";
        if(currentMail.getStar()){
            labelStar="true";
        }

        String readSubject = currentMail.getSubject();
        String readText = currentMail.getText();
        String readFrom = currentMail.getFrom();
        String readDate = currentMail.getDate();
        String readId = currentMail.getMessageID();
        Intent intent = new Intent(TrashFragment.this.getActivity(), ReadMailActivity.class);
        intent.putExtra("Subject", readSubject);
        intent.putExtra("Text", readText);
        intent.putExtra("From", readFrom);
        intent.putExtra("Time", readDate);
        intent.putExtra("MessageId", readId);
        intent.putExtra("FromFragment","Trash");
        intent.putExtra("Starlabel",labelStar);


        TrashFragment.this.startActivity(intent);
        getActivity().finish();

    }
}