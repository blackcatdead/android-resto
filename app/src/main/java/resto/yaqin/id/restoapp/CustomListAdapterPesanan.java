package resto.yaqin.id.restoapp;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import helper.AppController;
import helper.SessionManager;

/**
 * Created by Ikhsan on 9/11/2015.
 */
public class CustomListAdapterPesanan extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private JSONArray jarr;
    private String ip;
    private String TAG = CustomListAdapterPesanan.class.getSimpleName();
    private String tag_string_req = "hapus_order_req";


    public CustomListAdapterPesanan(Activity activity, JSONArray jarr, String ip) {
        this.activity = activity;
        this.jarr = jarr;
        this.ip = ip;
    }

    @Override
    public int getCount() {
        return jarr.length();
    }

    @Override
    public Object getItem(int location) {
        try {
            return jarr.get(location);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_pesanan, null);

        String id_order="";
        String nama_txt="";
        String noMeja_txt="";

        TextView nama = (TextView) convertView.findViewById(R.id.tv_pesanan_nama);
        TextView waktu = (TextView) convertView.findViewById(R.id.tv_pesanan_waktu);
        TextView meja = (TextView) convertView.findViewById(R.id.tv_pesanan_meja);

        // getting movie data for the row
        JSONObject m = null;
        try {
            m = jarr.getJSONObject(position);


            String dtStart = m.getString("datetime").toString();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(dtStart);
                Format formatter = new SimpleDateFormat("E, dd-MM-yyyy kk:mm ");
                dtStart = formatter.format(date);
                System.out.println(date);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            nama_txt=m.getString("nama_pemesan").toString();
            noMeja_txt=m.getString("no_meja").toString();
            nama.setText(nama_txt);
            waktu.setText(dtStart);
            meja.setText(noMeja_txt);
            id_order=m.getString("id_order").toString();



        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String finalId_order = id_order;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity,"it works. id_order : " + finalId_order, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(activity, DetailPesananActivity.class);
                i.putExtra("id_order", finalId_order);
                i.putExtra("is_konfirmed", "1");
                activity.startActivity(i);
            }
        });

        final String finalNoMeja_txt = noMeja_txt;
        final String finalNama_txt = nama_txt;
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder
                        .setItems(R.array.dialog_menu_detailorder, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                if (which == 0) {
                                    Dialog dia = new Dialog(activity);
                                    dia.setContentView(R.layout.dialog_edit_pesanan);
                                    dia.setTitle("Edit Pesanan");
                                    //dialog.

                                    // set the custom dialog components - text, image and button
                                    // TextView text = (TextView) dialog.findViewById(R.id.text);
                                    // text.setText("Android custom dialog example!");
                                    final EditText etNoMeja = (EditText) dia.findViewById(R.id.dialog_editPesanan_noMeja);
                                    final EditText etAtasNama = (EditText) dia.findViewById(R.id.dialog_editPesanan_atasNama);
                                    Button btnSimpan = (Button) dia.findViewById(R.id.btn_editPesanan_simpan);

                                    etNoMeja.setText(finalNoMeja_txt);
                                    etAtasNama.setText(finalNama_txt);
                                    btnSimpan.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //Toast.makeText(getApplicationContext(), ipa, Toast.LENGTH_SHORT).show();
                                            ubahOrder(finalId_order,etAtasNama.getText().toString(),etNoMeja.getText().toString());
                                        }
                                    });
                                    dia.show();

                                } else if (which == 1) {
                                    //hapus


                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    builder.setMessage("Yakin akan menghapus pesanan?")
                                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // FIRE ZE MISSILES!
                                                    hapusOrder(finalId_order);
                                                }
                                            })
                                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // User cancelled the dialog

                                                }
                                            });
                                    // Create the AlertDialog object and return it
                                    builder.show();
                                }

                            }
                        });
                builder.show();
                return false;
            }
        });

        return convertView;
    }

    public void hapusOrder(String id_order)
    {
        String url_order_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/hapusOrder?id_order="+id_order;
        Toast.makeText(activity, url_order_hari_ini, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_order_hari_ini,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Toast.makeText(activity, "response : "+response.trim(),Toast.LENGTH_SHORT).show();
                        if(response.trim().matches("1"))
                        {
                            activity.finish();
                            activity.startActivity(activity.getIntent());

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "error",Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);



    }

    public void ubahOrder(String id_order, String atasNama, String nomorMeja)
    {
        String url_order_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/ubahOrder?id_order="+id_order+"&nama_pemesan="+atasNama+"&no_meja="+nomorMeja;
        Toast.makeText(activity, url_order_hari_ini, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_order_hari_ini,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Toast.makeText(activity, "response : "+response.trim(),Toast.LENGTH_SHORT).show();
                        if(response.trim().matches("1"))
                        {
                            Toast.makeText(activity, "Sukses merubah pesanan", Toast.LENGTH_SHORT).show();
                            activity.finish();
                            activity.startActivity(activity.getIntent());

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "error",Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);



    }


}
