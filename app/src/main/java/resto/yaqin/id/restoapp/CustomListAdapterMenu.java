package resto.yaqin.id.restoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import helper.AppController;
import helper.Rupiah;

/**
 * Created by Ikhsan on 9/11/2015.
 */
public class CustomListAdapterMenu extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private JSONArray jarr;
    private String ip;
    private String tipe;
    private String id_order;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private String TAG = CustomListAdapterMenu.class.getSimpleName();
    private String tag_string_req = "tambah_detal_pesanan_req";


    public CustomListAdapterMenu(Activity activity, JSONArray jarr, String ip, String tipez, String id_orderz) {
        this.activity = activity;
        this.jarr = jarr;
        this.tipe = tipez;
        this.ip = ip;
        this.id_order = id_orderz;
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


        //Toast.makeText(activity, jarr.toString(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(activity, "tipe : "+tipe, Toast.LENGTH_SHORT).show();
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
            convertView = inflater.inflate(R.layout.item_menu, null);


        TextView id = (TextView) convertView.findViewById(R.id.tv_menu_id);
        final TextView nama = (TextView) convertView.findViewById(R.id.tv_menu_nama);
        final TextView harga_v = (TextView) convertView.findViewById(R.id.tv_menu_harga);
        //ImageView img = (ImageView) convertView.findViewById(R.id.iv_menu_img);
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.iv_menu_img);

        String id_menu="";
        String nama_menu="";
        String harga="0";
        JSONObject m = null;
        try {
            m = jarr.getJSONObject(position);
            harga= m.getString("harga").toString();
            int rup =Integer.parseInt(harga);

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);

            thumbNail.setImageUrl("http://"+ip+"/resto/gambar/"+m.getString("gambar").toString(),imageLoader);
            nama_menu=m.getString("nama").toString();
            nama.setText(nama_menu);
            String rp=new String();

            harga_v.setText(kursIndonesia.format(rup));
            id_menu = m.getString("id_menu").toString();
            id.setText(id.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(tipe.equals("2"))
        {
            final String finalNama_menu = nama_menu;
            final String finalId_menu = id_menu;
            final String finalHarga = harga;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    builder.setTitle("Tambah Menu Pesanan");
                    View vDialog = inflater.inflate(R.layout.dialog_tambah_detail_pesanan, null);
                    TextView namaMenu = (TextView) vDialog.findViewById(R.id.tvNamaMenu);
                    final EditText etQuantity = (EditText) vDialog.findViewById(R.id.etQuantity);
                    final EditText etCatatan = (EditText) vDialog.findViewById(R.id.etCatatan);
                    Button btnTambahDetailOrder = (Button) vDialog.findViewById(R.id.btnTambahDetailPesanan);
                    namaMenu.setText("Menu: "+finalNama_menu);
                    btnTambahDetailOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           // finishWithResult();
                            int int_harga = Integer.parseInt(finalHarga);
                            int int_qty = Integer.parseInt(etQuantity.getText().toString());
                            int subtotal= int_harga*int_qty;
                            tambahDetailOrder(id_order, finalId_menu,etQuantity.getText().toString(), etCatatan.getText().toString(), subtotal+"", "0");
                        }
                    });
                    builder.setView(vDialog);
                    builder.show();


                }
            });
        }





        return convertView;
    }

    private void finishWithResult()
    {
        Bundle conData = new Bundle();
        //conData.putString("results", "Thanks Thanks");
        Intent intent = new Intent();
        intent.putExtras(conData);
        activity.setResult(activity.RESULT_OK, intent);
        activity.finish();
    }

    public void tambahDetailOrder(String id_ordr_fk, String id_menu_fk, String quantity, String catatan, String subtotal, String status)
    {
//        http://localhost/ws_resto/index.php/servicecontroller/tambahDetailOrder?id_order_fk=5&id_menu_fk=1&quantity=3&catatan=tidak%20pedas&subtotal=30000&status=0
        String url_order_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/tambahDetailOrder?id_order_fk="+id_ordr_fk+"&id_menu_fk="+id_menu_fk+"&quantity="+quantity+"&catatan="+catatan.replace(" ","%20")+"&subtotal="+subtotal+"&status="+status;
        Toast.makeText(activity, url_order_hari_ini, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_order_hari_ini,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Toast.makeText(activity, "response : "+response.trim(),Toast.LENGTH_SHORT).show();
                        if(response.trim().matches("1"))
                        {
                            //activity.finish();
                            //activity.startActivity(activity.getIntent());

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(activity, "error",Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

        finishWithResult();

    }




}
