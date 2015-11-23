package resto.yaqin.id.restoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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


public class DetailPesananActivity extends ActionBarActivity {

    SessionManager session;
    private ListView listView;
    private CustomListAdapterDetailPesanan adapter;
    Activity ac;
    String id_order="";
    String is_konfirmed="";
    private String TAG = DetailPesananActivity.class.getSimpleName();
    private String tag_string_req = "detail_pesanan_req";
    String ip="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        ip = user.get(SessionManager.KEY_IP);

        Bundle b = getIntent().getExtras();
        id_order = b.getString("id_order");
        is_konfirmed = b.getString("is_konfirmed");
       // Toast.makeText(DetailPesananActivity.this, id_order, Toast.LENGTH_SHORT).show();

        listView = (ListView) findViewById(R.id.list_detail_pesanan);

        ac=this;

        String url_order_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/detailorder?id_order_fk="+id_order;
        //Toast.makeText(DetailPesananActivity.this, url_order_hari_ini, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_order_hari_ini,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONArray jarr= new JSONArray(response.toString());
                            adapter = new CustomListAdapterDetailPesanan(ac, jarr,ip);
                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(DetailPesananActivity.this, "error",Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        //queue.add(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_pesanan, menu);
        if(is_konfirmed.matches("1"))
        {
            menu.getItem(1).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.tambah_detail_pesanan) {
            Intent i = new Intent(getApplicationContext(),MenuActivity.class);
            i.putExtra("id", id_order);
            i.putExtra("tipe", "2");
            startActivityForResult(i, 90);

            return true;
        }else if(id == R.id.konfirmasi)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            LayoutInflater inflater = ac.getLayoutInflater();
            builder.setTitle("Konfirmasi pesanan");
            builder.setPositiveButton("Konfirmasi", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String url_order_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/konfirmasi?id_order="+id_order+"&status=0";
                    Toast.makeText(ac, url_order_hari_ini, Toast.LENGTH_SHORT).show();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url_order_hari_ini,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                    Toast.makeText(ac, "response : "+response.trim(),Toast.LENGTH_SHORT).show();
                                    if(response.trim().matches("true"))
                                    {
                                        finishWithResult();
                                        //activity.finish();
                                        //activity.startActivity(activity.getIntent());

                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ac, "error",Toast.LENGTH_SHORT).show();
                        }
                    });

                    AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
                }
            });
            View vDialog = inflater.inflate(R.layout.dialog_konfirmasi, null);
            ListView lvKonf = (ListView) vDialog.findViewById(R.id.lst_konfirmasi);
            lvKonf.setAdapter(adapter);
            builder.setView(vDialog);
            builder.show();
        }

        return super.onOptionsItemSelected(item);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 90:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    //String result = res.getString("results");
                    //Log.d("FIRST", "result:"+result);
                    Toast.makeText(DetailPesananActivity.this, "result:"+resultCode,Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
                break;
        }
    }
}
