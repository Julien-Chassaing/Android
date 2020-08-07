package com.losrobertoshermanos.ppe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkStateChecker extends BroadcastReceiver {
    /* access modifiers changed from: private */
    public Context context;
    /* access modifiers changed from: private */
    public DatabaseHelper db;
    private List<Controler> lesControles;

    public void onReceive(Context context2, Intent intent) {
        this.context = context2;
        this.db = new DatabaseHelper(context2);
        NetworkInfo activeNetwork = ((ConnectivityManager) context2.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetwork == null) {
            return;
        }
        if (activeNetwork.getType() == 1 || activeNetwork.getType() == 0) {
            List<Controler> readLesControles = this.db.readLesControles();
            this.lesControles = readLesControles;
            for (Controler c : readLesControles) {
                saveName(c);
            }
        }
    }

    private void saveName(final Controler unControle) {
        final Controler controler = unControle;
        VolleySingleton.getInstance(this.context).addToRequestQueue(new StringRequest(1, "https://testandroid.mtxserv.com/saveName.php", new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    System.out.println(response);
                    if (!new JSONObject(response).getBoolean("error")) {
                        NetworkStateChecker.this.db.deleteControle(unControle);
                        NetworkStateChecker.this.context.sendBroadcast(new Intent("net.simplifiedcoding.datasaved"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("numero_serie", controler.getN_serie());
                params.put("numero_intervention", controler.getId_intervention());
                params.put("temps_passer", controler.getTemps());
                params.put("commentaire", controler.getCommentaire());
                return params;
            }
        });
    }
}
