package resto.yaqin.id.restoapp;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import helper.AppController;
import helper.SessionManager;


public class MenuActivity extends ActionBarActivity {

    TabHost tabhost;
    SessionManager session;
    private String TAG = MenuActivity.class.getSimpleName();
    Activity ac;
    private String tag_string_req = "menu_req";
    private CustomListAdapterMenu adapter;
    private CustomListAdapterMenu adapter2;
    String ip="";
    String id="";
    String tipe="";

    ListView lv_makanan;
    ListView lv_minuman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        ip = user.get(SessionManager.KEY_IP);


        Bundle b = getIntent().getExtras();
        id = b.getString("id");
        tipe = b.getString("tipe");
       // Toast.makeText(MenuActivity.this, "id dan tipe"+id+", "+tipe, Toast.LENGTH_SHORT).show();


        tabhost= (TabHost) findViewById(R.id.tabHost);

        tabhost.setup();

        TabHost.TabSpec tabspec = tabhost.newTabSpec("makanan");
        tabspec.setContent(R.id.tab1);
        tabspec.setIndicator("Makanan");
        tabhost.addTab(tabspec);

        tabspec = tabhost.newTabSpec("minuman");
        tabspec.setContent(R.id.tab2);
        tabspec.setIndicator("Minuman");
        tabhost.addTab(tabspec);

        lv_makanan = (ListView) tabhost.findViewById(R.id.list_makanan);
        lv_minuman = (ListView) tabhost.findViewById(R.id.list_minuman);

        ac=this;
        String url_makanan = "http://"+ip+"/resto/index.php/servicecontroller/makanan";
        //Toast.makeText(MenuActivity.this, url_makanan, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_makanan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MenuActivity.this, response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jarr= new JSONArray(response.toString());
                            adapter = new CustomListAdapterMenu(ac, jarr,ip,tipe,id);
                            lv_makanan.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MenuActivity.this, "error",Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        //queue.add(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

        String url_minuman = "http://"+ip+"/resto/index.php/servicecontroller/minuman";
        //Toast.makeText(MenuActivity.this, url_minuman, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url_minuman,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MenuActivity.this, response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jarr2= new JSONArray(response.toString());
                            adapter2 = new CustomListAdapterMenu(ac, jarr2,ip,tipe,id);
                            lv_minuman.setAdapter(adapter2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MenuActivity.this, "error",Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        //queue.add(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest2, tag_string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_menu, menu);
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
