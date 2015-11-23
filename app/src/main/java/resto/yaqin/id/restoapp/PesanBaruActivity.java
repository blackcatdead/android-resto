package resto.yaqin.id.restoapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

import helper.AppController;
import helper.SessionManager;


public class PesanBaruActivity extends ActionBarActivity {

    SessionManager session;
    String ip="";
    private String TAG = PesanBaruActivity.class.getSimpleName();

    private String tag_string_req = "pesan_baru_req";

    EditText etMeja;
    EditText etAtasNama;
    String id_user="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesan_baru);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        ip = user.get(SessionManager.KEY_IP);


        etMeja = (EditText) findViewById(R.id.etNoMeja);
        etAtasNama = (EditText) findViewById(R.id.etAtasNama);

        Bundle b = getIntent().getExtras();
        id_user = b.getString("id");
        //Toast.makeText(PesanBaruActivity.this, id_user, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pesan_baru, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mulai_pesan) {
            //return true;
           // Toast.makeText(getApplicationContext(),"it works",Toast.LENGTH_SHORT).show();
            if((!etAtasNama.getText().toString().matches("")) && (!etMeja.getText().toString().matches("")))
            {
                String url_order_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/tambahOrder?id_user_fk="+id_user+"&nama_pemesan="+etAtasNama.getText().toString().replace(" ","%20")+"&no_meja="+etMeja.getText().toString()+"&status=2";
                //Toast.makeText(PesanBaruActivity.this, url_order_hari_ini, Toast.LENGTH_SHORT).show();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url_order_hari_ini,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                //tvOrderToday.setText(response.trim());
                                Toast.makeText(PesanBaruActivity.this, response.trim(), Toast.LENGTH_SHORT).show();
                                if((response.trim()!="0") || (response.trim()!=""))
                                {
                                    finish();
                                    Intent i= new Intent(getApplicationContext(),DetailPesananActivity.class);
                                    i.putExtra("id_order", response.trim());
                                    i.putExtra("is_konfirmed", "0");
                                    startActivity(i);

                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PesanBaruActivity.this, "gagal membuat pesanan",Toast.LENGTH_SHORT).show();
                    }
                });
                // Add the request to the RequestQueue.
                //queue.add(stringRequest);
                AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
            }else
            {
                Toast.makeText(PesanBaruActivity.this, "Pastikan semua field terisi dengan benar.",Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
