package resto.yaqin.id.restoapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import helper.AppController;
import helper.SessionManager;


public class LoginActivity extends ActionBarActivity {

    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    TextView tvSetIP;
    SessionManager session;
    final Context context = this;
    private String TAG = LoginActivity.class.getSimpleName();

    private String tag_string_req = "login_req";
    String ip="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());


        //Toast.makeText(getApplicationContext(), "is: "+ip, Toast.LENGTH_SHORT).show();

        //final RequestQueue queue = Volley.newRequestQueue(this);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvSetIP = (TextView) findViewById(R.id.tvSetIP);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String url="http://192.168.1.2/ws-pkm/index.php/webservice/tambahUsaha?usaha="+etUsaha.getText().toString()+"&deskripsi="+etDeskripsi.getText().toString();
                HashMap<String, String> user = session.getUserDetails();
                ip = user.get(SessionManager.KEY_IP);
                String url_login = "http://"+ip+"/resto/index.php/servicecontroller/login?username="+etUsername.getText().toString()+"&password="+etPassword.getText().toString()+"&status=1";
                //Toast.makeText(LoginActivity.this, url_login,Toast.LENGTH_SHORT).show();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url_login,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                               // btnLogin.setText(response);

                                if (response != null)
                                {
                                    try {
                                        JSONArray ja= new JSONArray(response.toString());
                                        JSONObject jo = ja.getJSONObject(0);
                                        String id= jo.getString("id_user");
                                        String uname= jo.getString("username");
                                        String pass= jo.getString("password");
                                        String nama= jo.getString("nama");

                                        session.createLoginSession(id, nama,uname);


                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                        finish();
                                    }catch (Exception ex)
                                    {
                                        Toast.makeText(LoginActivity.this, "gagal login",Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this, "gagal login",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "gagal login",Toast.LENGTH_SHORT).show();
                    }
                });
                // Add the request to the RequestQueue.
                //queue.add(stringRequest);
                AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
            }
        });

        tvSetIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_set_ip);
                dialog.setTitle("Set IP");
                //dialog.

                // set the custom dialog components - text, image and button
               // TextView text = (TextView) dialog.findViewById(R.id.text);
               // text.setText("Android custom dialog example!");
                final EditText etSetIP = (EditText) dialog.findViewById(R.id.etSetIP);
                Button btnSetIP = (Button) dialog.findViewById(R.id.btnSetIPSimpan);
                HashMap<String, String> user = session.getUserDetails();
                ip = user.get(SessionManager.KEY_IP);
                etSetIP.setText(ip);
                btnSetIP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String ipa= etSetIP.getText().toString();
                        session.setIP(ipa);
                        //Toast.makeText(getApplicationContext(), ipa, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "IP telah diganti", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_login, menu);
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
