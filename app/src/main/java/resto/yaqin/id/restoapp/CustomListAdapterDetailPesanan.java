package resto.yaqin.id.restoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Color;
import android.os.Bundle;
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

/**
 * Created by Ikhsan on 9/11/2015.
 */
public class CustomListAdapterDetailPesanan extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private JSONArray jarr;
    private String ip;
    private String TAG = CustomListAdapterDetailPesanan.class.getSimpleName();
    private String tag_string_req = "hapus_detail_order_req";


    public CustomListAdapterDetailPesanan(Activity activity, JSONArray jarr, String ip) {
        this.activity = activity;
        this.jarr = jarr;
        this.ip = ip;
        //Toast.makeText(activity, jarr.toString(), Toast.LENGTH_SHORT).show();
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
            convertView = inflater.inflate(R.layout.item_detail_pesanan, null);


        TextView no = (TextView) convertView.findViewById(R.id.tv_detail_pesanan_nomor);
        TextView menu = (TextView) convertView.findViewById(R.id.tv_detailpesanan_menu);
        TextView catatan = (TextView) convertView.findViewById(R.id.tv_detailpesanan_catatan);
        TextView quantity = (TextView) convertView.findViewById(R.id.tv_detailpesanan_quantity);

        // getting movie data for the row
        String id_detail_order="";
        JSONObject m = null;
        String menu_txt = null;
        String id_menu = "";
        String status_s ="0";
        String catatan_txt = null;
        String qty_txt = null;
        final String[] harga = {"0"};
        try {

            m = jarr.getJSONObject(position);
            id_menu=m.getString("id_menu_fk").toString();
            status_s=m.getString("status").toString();
            menu_txt=m.getString("nama").toString();
            catatan_txt = m.getString("catatan").toString();
            qty_txt = m.getString("quantity").toString();
            id_detail_order = m.getString("id_detail_order").toString();
            no.setText((position+1)+"");
            menu.setText(menu_txt);
            quantity.setText("Quantity: "+qty_txt);
            catatan.setText("catatan: "+catatan_txt);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(status_s.matches("1"))
        {
            convertView.setBackgroundColor(Color.parseColor("#D1FFA3"));
        }
        final String finalId_detail_order = id_detail_order;
        final String finalMenu_txt = menu_txt;
        final String finalCatatan_txt = catatan_txt;
        final String finalQty_txt = qty_txt;
        final String finalId_menu = id_menu;
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, "long click", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder
                        .setItems(R.array.dialog_menu_detailorder, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                if(which == 0)
                                {

                                    String url_order_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/menu?id_menu="+ finalId_menu;
                                    //Toast.makeText(activity, url_order_hari_ini, Toast.LENGTH_SHORT).show();
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url_order_hari_ini,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    // Display the first 500 characters of the response string.
                                                    //Toast.makeText(activity, "response : "+response.trim(),Toast.LENGTH_SHORT).show();
                                                    try {
                                                        JSONArray jarr = new JSONArray(response);
                                                        JSONObject jo = jarr.getJSONObject(0);
                                                        harga[0] = jo.getString("harga").toString();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(activity, "error",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);



                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    LayoutInflater inflater = activity.getLayoutInflater();
                                    builder.setTitle("Edit Menu Pesanan");

                                    View vDialog = inflater.inflate(R.layout.dialog_tambah_detail_pesanan, null);
                                    TextView namaMenu = (TextView) vDialog.findViewById(R.id.tvNamaMenu);
                                    final EditText etQuantity = (EditText) vDialog.findViewById(R.id.etQuantity);
                                    final EditText etCatatan = (EditText) vDialog.findViewById(R.id.etCatatan);
                                    Button btnTambahDetailOrder = (Button) vDialog.findViewById(R.id.btnTambahDetailPesanan);
                                    namaMenu.setText("Menu: "+ finalMenu_txt);
                                    etQuantity.setText(finalQty_txt);
                                    etCatatan.setText(finalCatatan_txt);
                                    btnTambahDetailOrder.setText("Simpan");
                                    btnTambahDetailOrder.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // finishWithResult();
                                            int int_harga = Integer.parseInt(harga[0]);
                                            int int_qty = Integer.parseInt(etQuantity.getText().toString());
                                            int subtotal= int_harga*int_qty;
                                            editDetailOrder(finalId_detail_order, finalId_menu, etQuantity.getText().toString(), etCatatan.getText().toString(), subtotal+"", "0");
                                        }
                                    });
                                    builder.setView(vDialog);
                                    builder.show();


                                }else if(which == 1)
                                {
                                    //hapus


                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    builder.setMessage("Yakin akan menghapus detail pesanan?")
                                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // FIRE ZE MISSILES!
                                                    hapusDetailOrder(finalId_detail_order);
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

    public void editDetailOrder(String id_detail_order, String id_menu_fk, String quantity, String catatan, String subtotal, String status)
    {
//        http://localhost/ws_resto/index.php/servicecontroller/tambahDetailOrder?id_order_fk=5&id_menu_fk=1&quantity=3&catatan=tidak%20pedas&subtotal=30000&status=0
        String url_order_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/ubahDetailOrder?id_detail_order="+id_detail_order+"&id_menu_fk="+id_menu_fk+"&quantity="+quantity+"&catatan="+catatan.replace(" ","%20")+"&subtotal="+subtotal+"&status="+status;
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
                            //activity.finish();
                            //activity.startActivity(activity.getIntent());

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

    public void hapusDetailOrder(String id_detail_order)
    {
        String url_order_hari_ini = "http://"+ip+"/resto/index.php/servicecontroller/hapusDetailOrder?id_detail_order="+id_detail_order;
        //Toast.makeText(activity, url_order_hari_ini, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_order_hari_ini,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Toast.makeText(activity, "response : "+response.trim(),Toast.LENGTH_SHORT).show();
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




}
