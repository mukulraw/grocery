package com.mrtecks.grocery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mrtecks.grocery.checkoutPOJO.checkoutBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Checkout extends AppCompatActivity {

    Toolbar toolbar;
    EditText name , address , area , city , pin;
    Button proceed;
    ProgressBar progress;
    String amm;
    Spinner slot;
    String tslot;
    String paymode;
    RadioGroup group;
    String oid;
    TextView date;

    String dd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        amm = getIntent().getStringExtra("amount");

        toolbar = findViewById(R.id.toolbar4);
        name = findViewById(R.id.editText2);
        address = findViewById(R.id.editText3);
        proceed = findViewById(R.id.button6);
        progress = findViewById(R.id.progressBar4);
        slot = findViewById(R.id.spinner);
        group = findViewById(R.id.radioButton);
        area = findViewById(R.id.editText5);
        city = findViewById(R.id.editText6);
        pin = findViewById(R.id.editText7);
        date = findViewById(R.id.textView48);


        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Checkout");


        final List<String> ts = new ArrayList<>();

        ts.add("9:30 - 12:30");
        ts.add("12:30 - 3:30");
        ts.add("4:00 - 7:00");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,ts);

        slot.setAdapter(adapter);


        slot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                tslot = ts.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(Checkout.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.date_dialog);
                dialog.show();


                final DatePicker picker = dialog.findViewById(R.id.date);
                Button ok = dialog.findViewById(R.id.ok);

                long now = System.currentTimeMillis() - 1000;
                picker.setMaxDate(now);





                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int year = picker.getYear();
                        int month = picker.getMonth();
                        int day = picker.getDayOfMonth();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String strDate = format.format(calendar.getTime());

                        dialog.dismiss();

                        date.setText("Date - " + strDate + " (click to change)");

                        dd = strDate;

                    }
                });

            }
        });


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String n = name.getText().toString();
                String a = address.getText().toString();

                if (n.length() > 0)
                {

                    if (a.length() > 0)
                    {

                        int iidd = group.getCheckedRadioButtonId();

                        if (iidd > -1)
                        {

                            RadioButton cb = group.findViewById(iidd);

                            paymode = cb.getText().toString();



                            oid = String.valueOf(System.currentTimeMillis());

                            if (paymode.equals("Cash on Delivery"))
                            {
                                progress.setVisibility(View.VISIBLE);

                                Bean b = (Bean) getApplicationContext();

                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(b.baseurl)
                                        .addConverterFactory(ScalarsConverterFactory.create())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

                                Call<checkoutBean> call = cr.buyVouchers(
                                        SharePreferenceUtils.getInstance().getString("userId"),
                                        amm,
                                        oid,
                                        n,
                                        a,
                                        paymode,
                                        tslot
                                );
                                call.enqueue(new Callback<checkoutBean>() {
                                    @Override
                                    public void onResponse(Call<checkoutBean> call, Response<checkoutBean> response) {

                                        Toast.makeText(Checkout.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                        progress.setVisibility(View.GONE);

                                        finish();

                                    }

                                    @Override
                                    public void onFailure(Call<checkoutBean> call, Throwable t) {
                                        progress.setVisibility(View.GONE);
                                    }
                                });
                            }
                            else
                            {


                                Intent intent = new Intent(Checkout.this, WebViewActivity.class);
                                intent.putExtra(AvenuesParams.ACCESS_CODE, "AVOL70EE77BF91LOFB");
                                intent.putExtra(AvenuesParams.MERCHANT_ID, "133862");
                                intent.putExtra(AvenuesParams.ORDER_ID, oid);
                                intent.putExtra(AvenuesParams.CURRENCY, "INR");
                                intent.putExtra(AvenuesParams.AMOUNT, String.valueOf(amm));
                                //intent.putExtra(AvenuesParams.AMOUNT, "1");
                                intent.putExtra("pid", SharePreferenceUtils.getInstance().getString("userid"));

                                intent.putExtra(AvenuesParams.REDIRECT_URL, "https://mrtecks.com/grocery/api/pay/ccavResponseHandler.php");
                                intent.putExtra(AvenuesParams.CANCEL_URL, "https://mrtecks.com/grocery/api/pay/ccavResponseHandler.php");
                                intent.putExtra(AvenuesParams.RSA_KEY_URL, "https://mrtecks.com/grocery/api/pay/GetRSA.php");

                                startActivityForResult(intent , 12);


                            }



                        }
                        else
                        {
                            Toast.makeText(Checkout.this, "Please select a Payment Mode", Toast.LENGTH_SHORT).show();
                        }




                    }
                    else
                    {
                        Toast.makeText(Checkout.this, "Please enter a valid address", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(Checkout.this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {

            progress.setVisibility(View.VISIBLE);

            Bean b = (Bean) getApplicationContext();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(b.baseurl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

            String n = name.getText().toString();
            String a = address.getText().toString();

            Call<checkoutBean> call = cr.buyVouchers(
                    SharePreferenceUtils.getInstance().getString("userId"),
                    amm,
                    oid,
                    n,
                    a,
                    "online",
                    tslot
            );
            call.enqueue(new Callback<checkoutBean>() {
                @Override
                public void onResponse(Call<checkoutBean> call, Response<checkoutBean> response) {

                    Toast.makeText(Checkout.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    progress.setVisibility(View.GONE);

                    finish();

                }

                @Override
                public void onFailure(Call<checkoutBean> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                }
            });

        }

    }
}
