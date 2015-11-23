package resto.yaqin.id.restoapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

import helper.AppController;
import helper.SessionManager;


public class MainActivity extends ActionBarActivity {

    SessionManager session;
    Button btnLogout;
    TextView tvUsername;
    TextView tvNama;
    TextView tvOrderSaya;
    TextView tvOrderToday;

    String id_belum_dikonfirm="0";
    String id;
    Button btnPesanbaru;
    Button btnPesanan;
    Button btnMenu;

    private String TAG = LoginActivity.class.getSimpleName();
    private String tag_string_req = "main_req";
    String ip="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        session = new SessionManager(getApplicationContext());
        //final RequestQueue queue = Volley.newRequestQueue(this);

        session.checkLogin();
        btnPesanbaru = (Button) findViewById(R.id.btnPesananBaru);
        btnPesanan = (Button) findViewById(R.id.btnPesanan);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnMenu= (Button) findViewById(R.id.btnMenu);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvNama = (TextView) findViewById(R.id.tvNama);
        tvOrderSaya = (TextView) findViewById(R.id.tvOrderSaya);
        tvOrderToday = (TextView) findViewById(R.id.tvSemuaOrderToday);


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                session.logoutUser();
            }
        });




        HashMap<String, String> user = session.getUserDetails();
        String uname = user.get(SessionManager.KEY_USERNAME);
        String name = user.get(SessionManager.KEY_NAME);
        ip = user.get(SessionManager.KEY_IP);
        id = user.get(SessionManager.KEY_ID);
        //Toast.makeText(MainActivity.this, id, Toast.LENGTH_SHORT).show();
        tvUsername.setText(uname);
        tvNama.setText(name);



        setisi();



        btnPesanbaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url_belum_dikonfirm = "http://"+ip+"/resto/index.php/servicecontroller/belum_dikonfirm";
                Toast.makeText(MainActivity.this, url_belum_dikonfirm, Toast.LENGTH_SHORT).show();
                StringRequest stringRequest3 = new StringRequest(Request.Method.GET, url_belum_dikonfirm,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                id_belum_dikonfirm = response.trim().toString();
                                Toast.makeText(MainActivity.this, "id_belum_dikonfirm : "+id_belum_dikonfirm, Toast.LENGTH_SHORT).show();
                                if(!id_belum_dikonfirm.matches("0"))
                                {
                                    Intent i = new Intent(getApplicationContext(), DetailPesananActivity.class);
                                    i.putExtra("id_order", id_belum_dikonfirm);
                                    i.putExtra("is_konfirmed", "0");
                                    startActivity(i);
                                }
                                else
                                {
                                    Intent i = new Intent(getApplicationContext(), PesanBaruActivity.class);
                                    i.putExtra("id", id);
                                    startActivity(i);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "error",Toast.LENGTH_SHORT).show();
                    }
                });
                // Add the request to the RequestQueue.
                //queue.add(stringRequest);
                AppController.getInstance().addToRequestQueue(stringRequest3, tag_string_req);



            }
        });



        btnPesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PesananActivity.class);
                i.putExtra("id", id);
                startActivity(i);
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MenuActivity.class);
                i.putExtra("id", id);
                i.putExtra("tipe", "1");
                startActivity(i);
            }
        });

    }

    public void setisi()
    {
        String url_order_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/orderHariIni";
        //Toast.makeText(MainActivity.this, url_order_hari_ini, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_order_hari_ini,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        tvOrderToday.setText(response.trim());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this, "error",Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        //queue.add(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);


        String url_order_saya_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/orderHariIni?id_user_fk="+id;
        //Toast.makeText(MainActivity.this, url_order_hari_ini, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url_order_saya_hari_ini,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        tvOrderSaya.setText(response.trim());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this, "error",Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        //queue.add(stringRequest2);
        AppController.getInstance().addToRequestQueue(stringRequest2, tag_string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first

        setisi();
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
