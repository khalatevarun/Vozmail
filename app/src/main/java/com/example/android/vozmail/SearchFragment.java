package com.example.android.vozmail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.fragment.app.Fragment;

import com.example.android.vozmail.adapters.commonAdapter;
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



public class SearchFragment extends Fragment {

    String date_value;
    String from_value;
    String to_value;

    /* renamed from: g */
    Globals g = Globals.getInstance();
    String[] ids = new String[10];
    List<inboxMails> inboxMailsList = new ArrayList();
    ListView listView;
    String messageID_value;
    String myResponse;
    View rootView;
    String snippet;
    String subject_value;
    commonAdapter CommonAdapter;
    String parameter;
    String searchingFor;
    SharedPreferences sh ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_search, container, false);
        this.rootView = inflate;
        this.listView = (ListView) inflate.findViewById(R.id.star_mails_list);
       parameter = ((MainActivity)getActivity()).searchParam;
        sh = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        
       
        
       
       
       searchingFor=((MainActivity)getActivity()).searchingFor;
        Log.w("Control till: ", "Got paramter "+parameter);
      //  tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));




        getList();
        return this.rootView;
    }

    /* access modifiers changed from: package-private */
    public void getList() {
        String url;
        OkHttpClient client = new OkHttpClient();
        ids = new String[10];

        if(searchingFor.equals("keyword")) {
            url = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages?q=" + parameter + "&maxResults=10&key=" + getResources().getString(R.string.api_key);
        }
        else{
            String label_id = g.getLabelId(parameter);
            url = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages?maxResults=10&labelIds="+label_id+"&key=" + getResources().getString(R.string.api_key);

        }
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
                        Log.w("Control till: ", "Before calling getContent method");

                        getContent(ids);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CommonAdapter = new commonAdapter(getActivity().getApplicationContext(), R.layout.common_mail_item, inboxMailsList);
                    Log.w("interrupt", "I am b/w inbox adapter init and set");
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            listView.setVisibility(View.VISIBLE);
                          listView.setAdapter(CommonAdapter);
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
            String url2 = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages/" + strArr[j] + "?key=AIzaSyCpfC0dtmTERPkSQXHAvrOlblWN6fflckI";
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
                            String checkFrom = from_value.substring(from_value.indexOf("<")+1,from_value.length()-1);
                            if (checkFrom.equals(g.getUserId())){
                                from_value = "Me";
                            }
                        }
                        if(name.equals("To")){
                            to_value = currentHeader.getString("value");

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
                    str = this.to_value;


                    String str10 = this.date_value;
                    String str9 = subject_value;
                    this.inboxMailsList.add(new inboxMails(from_value, str, str9, str10, this.snippet, strArr[j], encodeText, labels));
                }
            } catch (IOException | JSONException e) {

                e.printStackTrace();
            }
        }
    }
    void openMail(int position){
        inboxMails currentMail = (inboxMails) CommonAdapter.getItem(position);
        String labelStar = "false";
        if(currentMail.getStar()){
            labelStar="true";
        }

        String readSubject = currentMail.getSubject();
        String readText = currentMail.getText();
        String readFrom = currentMail.getFrom();
        String readDate = currentMail.getDate();
        String readTo = currentMail.getTo();
        Intent intent = new Intent(getActivity(), ReadMailActivity.class);
        intent.putExtra("Subject", readSubject);
        intent.putExtra("Text", readText);
        intent.putExtra("From", readFrom);
        intent.putExtra("To",readTo);
        intent.putExtra("Time", readDate);
        intent.putExtra("FromFragment","Inbox");
        intent.putExtra("Starlabel",labelStar);


        startActivity(intent);
        getActivity().finish();

    }

}