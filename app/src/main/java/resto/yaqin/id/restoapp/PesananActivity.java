package resto.yaqin.id.restoapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

import helper.AppController;
import helper.SessionManager;


public class PesananActivity extends ActionBarActivity {


    SessionManager session;
    private ListView listView;
    private CustomListAdapterPesanan adapter;
    Activity ac;
    String id_user="";

    private String TAG = PesananActivity.class.getSimpleName();
    private String tag_string_req = "pesanan_req";
    String ip="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        ip = user.get(SessionManager.KEY_IP);

        Bundle b = getIntent().getExtras();
        id_user = b.getString("id");
        //Toast.makeText(PesananActivity.this, id_user, Toast.LENGTH_SHORT).show();

        listView = (ListView) findViewById(R.id.list_pesanan);

        ac=this;

        String url_order_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/order?id_user_fk="+id_user+"&status=0";
        //Toast.makeText(PesananActivity.this, url_order_hari_ini, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_order_hari_ini,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONArray jarr= new JSONArray(response.toString());
                            adapter = new CustomListAdapterPesanan(ac, jarr,ip);
                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PesananActivity.this, "error",Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        //queue.add(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);



    }

    public void onBackPressed() {
        finishWithResult();
    }

    private void finishWithResult()
    {
        Bundle conData = new Bundle();
        //conData.putString("results", "Thanks Thanks");
        Intent intent = new Intent();
        intent.putExtras(conData);
        ac.setResult(ac.RESULT_OK, intent);
        ac.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_pesanan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
