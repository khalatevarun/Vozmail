package com.example.android.vozmail.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.vozmail.R;
import com.example.android.vozmail.Globals;
import com.example.android.vozmail.api.model.inboxMails;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class commonAdapter extends ArrayAdapter<inboxMails> {

    /* renamed from: g */
    Globals g = Globals.getInstance();
    String to_or_from;
    int a = 1;
    String entry;

    public commonAdapter(Context context, int resource, List<inboxMails> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.common_mail_item, parent, false);
        } else {
            ViewGroup viewGroup = parent;
        }
        final inboxMails currentMail = (inboxMails) getItem(position);
      /**  if(currentMail.getFrom().isEmpty()){
            to_or_from = currentMail.getTo();
            if (to_or_from.contains("<")) {
                if (to_or_from.indexOf("<") == 0) {
                    to_or_from = to_or_from.substring(1, to_or_from.indexOf(">") - 1);
                } else {
                    to_or_from = to_or_from.substring(0, to_or_from.indexOf("<"));
                }
            }
            to_or_from = "To: "+to_or_from;
        } else {
            to_or_from = currentMail.getFrom().split("<")[0];
        }
       **/


        ((TextView) listItemView.findViewById(R.id.subject)).setText(currentMail.getSubject());
        ((TextView) listItemView.findViewById(R.id.snippet)).setText(currentMail.getSnippet());
        String[] date_v = currentMail.getDate().split(" ");
        StringBuilder sb = new StringBuilder();
        sb.append(date_v[0]);
        sb.append(" ");
        sb.append(date_v[1]);
        sb.append(" ");
        sb.append(date_v[2]);
        ((TextView) listItemView.findViewById(R.id.date)).setText(sb.toString().replace(",", ""));
        final ImageView star = (ImageView) listItemView.findViewById(R.id.star);

        if(currentMail.getFrom().equals("Me")){
            to_or_from = "To: "+currentMail.getTo();
            entry ="This mail was sent to "+currentMail.getTo()+". At date "+ sb.toString().replace(",", "") + ". With subject "+currentMail.getSubject()+". Would you like to open this mail or move to the next mail or stop?";


        }
        else {
            to_or_from = currentMail.getFrom();
            entry="This is mail is from "+to_or_from+". Received at date "+sb.toString().replace(",", "")+ ". With subject "+currentMail.getSubject()+". Would you like to open this mail or move to the next mail or stop?";

        }

        TextView toORfrom = (TextView) listItemView.findViewById(R.id.toORfrom);
        toORfrom.setText(to_or_from);

        g.setEntry(entry);
        a++;





        if (currentMail.getStar()) {
            star.setImageResource(R.drawable.filled_star);
        }
        star.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!currentMail.getStar()) {
                    star.setImageResource(R.drawable.filled_star);
                    currentMail.setStar(true);
                    commonAdapter.this.addStarLabel(currentMail.getMessageID());
                    return;
                }
                star.setImageResource(R.drawable.star);
                currentMail.setStar(false);
                commonAdapter.this.removeStarLabel(currentMail.getMessageID());
            }
        });
        return listItemView;
    }

    /* access modifiers changed from: package-private */
    public void addStarLabel(String messageId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages/" + messageId + "/modify/?key=" + getContext().getResources().getString(R.string.api_key);
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
                            Toast.makeText(commonAdapter.this.getContext(), "Mail labelled as star", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void removeStarLabel(String messageId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://gmail.googleapis.com/gmail/v1/users/" + this.g.getUserId() + "/messages/" + messageId + "/modify/?key=" + getContext().getResources().getString(R.string.api_key);
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
                            Toast.makeText(commonAdapter.this.getContext(), "Mail labelled as star",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
