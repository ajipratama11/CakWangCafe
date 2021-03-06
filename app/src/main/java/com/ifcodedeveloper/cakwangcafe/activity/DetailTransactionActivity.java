package com.ifcodedeveloper.cakwangcafe.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ifcodedeveloper.cakwangcafe.R;
import com.ifcodedeveloper.cakwangcafe.adapter.AllTransAdapter;
import com.ifcodedeveloper.cakwangcafe.adapter.TransAdapter;
import com.ifcodedeveloper.cakwangcafe.model.orderProduct.GetOrderProduct;
import com.ifcodedeveloper.cakwangcafe.model.orderProduct.OrderProduct;
import com.ifcodedeveloper.cakwangcafe.model.transaction.GetTransaction;
import com.ifcodedeveloper.cakwangcafe.model.transaction.PostTransaction;
import com.ifcodedeveloper.cakwangcafe.model.transaction.Transaction;
import com.ifcodedeveloper.cakwangcafe.rest.ApiClient;
import com.ifcodedeveloper.cakwangcafe.rest.ApiInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTransactionActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_TRANS = "extra_trans";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ApiInterface mApiInterface;
    Context mContext;
    String nama_pelanggan, no_meja, id_transaksi;
    Integer total_harga;
    String pelanggan, meja, jam, tanggal, total,status = "2";
    Transaction transaction;
    ArrayList<OrderProduct> orderList = new ArrayList<>();
    TextView tv_total_harga, tv_pelanggan, tv_meja, tv_jam, tv_tanggal;
Button btn_selesai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);
        btn_selesai = findViewById(R.id.btn_selesai);
        btn_selesai.setOnClickListener(this);
        tv_total_harga = findViewById(R.id.tv_total_harga);
        tv_jam = findViewById(R.id.tv_jam);
        tv_pelanggan = findViewById(R.id.tv_pelanggan);
        tv_tanggal = findViewById(R.id.tv_tanggal);
        mRecyclerView = findViewById(R.id.rv_trans);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        transaction = getIntent().getParcelableExtra(EXTRA_TRANS);
        id_transaksi = transaction.getId_transaksi();
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Log.e("trans", "onCreate: "+id_transaksi );
        GetTrans();
        ShowCart();
    }

    void GetTrans() {
        Call<Transaction> TransCall = mApiInterface.getTrans(id_transaksi);
        TransCall.enqueue(new Callback<Transaction>() {

            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                pelanggan = response.body().getNama_pelanggan();
                meja = response.body().getNo_meja();
                jam = response.body().getJam();
                tanggal = response.body().getTanggal();
                total = response.body().getTotal_harga();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date testDate = null;
                try {
                    testDate = sdf.parse(tanggal);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
                String newFormat = formatter.format(testDate);
                tv_pelanggan.setText(pelanggan);
//                tv_meja.setText("Meja No." + meja);
                tv_jam.setText(jam);
                tv_tanggal.setText(newFormat);
                tv_total_harga.setText("Rp. " + total);
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {

            }
        });
    }

    public void ShowCart() {
        Call<GetOrderProduct> ItemCall = mApiInterface.getOder(id_transaksi);
        ItemCall.enqueue(new Callback<GetOrderProduct>() {
            @Override
            public void onResponse(Call<GetOrderProduct> call, Response<GetOrderProduct>
                    response) {
                orderList = response.body().getListDataOrder();

                Log.d("Retrofit Get", "Jumlah data Item: " + String.valueOf(orderList.size()));
                mAdapter = new TransAdapter(orderList, mContext);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<GetOrderProduct> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
            }
        });
    }

    public void StatusDone(){
        Call<GetTransaction> updateKontakCall = mApiInterface.updateTrans(id_transaksi);
        updateKontakCall.enqueue(new Callback<GetTransaction>() {
            @Override
            public void onResponse(Call<GetTransaction> call, Response<GetTransaction> response) {
                Log.e("s", "onResponse: " +status);

                Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<GetTransaction> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_selesai:
                    Intent checkout = new Intent(DetailTransactionActivity.this, ListTransactionActivity.class);
                    StatusDone();
                    startActivity(checkout);
                break;
        }
    }
}
