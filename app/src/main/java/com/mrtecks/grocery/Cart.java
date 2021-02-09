package com.mrtecks.grocery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mrtecks.grocery.cartPOJO.Datum;
import com.mrtecks.grocery.cartPOJO.cartBean;
import com.mrtecks.grocery.homePOJO.Best;
import com.mrtecks.grocery.homePOJO.homeBean;
import com.mrtecks.grocery.seingleProductPOJO.singleProductBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.steppertouch.StepperTouch;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Cart extends Fragment {

    private Toolbar toolbar;
    ProgressBar bar;
    String base;
    TextView btotal, bproceed, clear;

    float amm = 0;

    View bottom;

    CartAdapter adapter;

    GridLayoutManager manager;

    RecyclerView grid;

    List<Datum> list;

    String client, txn;

    List<Best> list2;
    TextView sp, grand;

    TextView fifty, ninetynine, twentyfive, delivery;
    EditText tip;

    float tt = 0;

    MainActivity2 mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);
        mainActivity = (MainActivity2) getActivity();
        list = new ArrayList<>();
        list2 = new ArrayList<>();

        fifty = view.findViewById(R.id.textView85);
        ninetynine = view.findViewById(R.id.textView86);
        twentyfive = view.findViewById(R.id.textView87);
        tip = view.findViewById(R.id.editTextNumber);
        sp = view.findViewById(R.id.textView76);
        grand = view.findViewById(R.id.textView80);
        toolbar = view.findViewById(R.id.toolbar3);
        bar = view.findViewById(R.id.progressBar3);
        bottom = view.findViewById(R.id.cart_bottom);
        btotal = view.findViewById(R.id.textView9);
        bproceed = view.findViewById(R.id.textView10);
        grid = view.findViewById(R.id.grid);
        clear = view.findViewById(R.id.textView12);
        delivery = view.findViewById(R.id.textView78);


        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Cart");

        fifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tip.setText("50");

            }
        });

        ninetynine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tip.setText("99");

            }
        });


        twentyfive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tip.setText("25");

            }
        });

        tip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    tt = Float.parseFloat(charSequence.toString());
                } else {
                    tt = 0;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        LinearLayoutManager manager1 = new LinearLayoutManager(mainActivity, RecyclerView.HORIZONTAL, false);

        adapter = new CartAdapter(list, mainActivity);

        manager = new GridLayoutManager(mainActivity, 1);

        grid.setAdapter(adapter);
        grid.setLayoutManager(manager);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bar.setVisibility(View.VISIBLE);

                Bean b = (Bean) mainActivity.getApplicationContext();

                base = b.baseurl;

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(b.baseurl)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

                Call<singleProductBean> call = cr.clearCart(SharePreferenceUtils.getInstance().getString("userId"));

                call.enqueue(new Callback<singleProductBean>() {
                    @Override
                    public void onResponse(Call<singleProductBean> call, Response<singleProductBean> response) {

                        if (response.body().getStatus().equals("1")) {
                            mainActivity.navigation.setSelectedItemId(R.id.action_home);
                        }

                        Toast.makeText(mainActivity, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        bar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onFailure(Call<singleProductBean> call, Throwable t) {
                        bar.setVisibility(View.GONE);
                    }
                });


            }
        });

        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (amm > 0) {

                    Intent intent = new Intent(mainActivity, Checkout.class);
                    if (amm < 1500) {
                        intent.putExtra("amount", String.valueOf(amm));
                        intent.putExtra("del_charges", 19);
                    } else {
                        intent.putExtra("amount", String.valueOf(amm));
                        intent.putExtra("del_charges", 0);
                    }
                    startActivity(intent);


                } else {
                    Toast.makeText(mainActivity, "Invalid amount", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadCart();

    }

    void loadCart() {
        bar.setVisibility(View.VISIBLE);

        Bean b = (Bean) mainActivity.getApplicationContext();

        base = b.baseurl;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(b.baseurl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

        Log.d("userid", SharePreferenceUtils.getInstance().getString("userId"));


        Call<cartBean> call = cr.getCart(SharePreferenceUtils.getInstance().getString("userId"));
        call.enqueue(new Callback<cartBean>() {
            @Override
            public void onResponse(Call<cartBean> call, Response<cartBean> response) {

                if (response.body().getData().size() > 0) {


                    adapter.setgrid(response.body().getData());

                    amm = Float.parseFloat(response.body().getTotal());

                    if (amm < 1500)
                    {
                        delivery.setText("\u20B9" + 19);
                        btotal.setText("Total: \u20B9" + (amm + 19));
                        grand.setText("\u20B9" + (amm + 19));
                    }
                    else
                    {
                        delivery.setText("\u20B9" + 0);
                        btotal.setText("Total: \u20B9" + (amm));
                        grand.setText("\u20B9" + (amm));
                    }


                    sp.setText("\u20B9" + response.body().getTotal());


                    bottom.setVisibility(View.VISIBLE);
                } else {
                    adapter.setgrid(response.body().getData());
                    bottom.setVisibility(View.GONE);
                    Toast.makeText(mainActivity, "Cart is empty", Toast.LENGTH_SHORT).show();
                    mainActivity.navigation.setSelectedItemId(R.id.action_home);
                }

                bar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<cartBean> call, Throwable t) {
                bar.setVisibility(View.GONE);
            }
        });

    }

    class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

        List<Datum> list = new ArrayList<>();
        Context context;
        LayoutInflater inflater;

        CartAdapter(List<Datum> list, Context context) {
            this.context = context;
            this.list = list;
        }

        void setgrid(List<Datum> list) {

            this.list = list;
            notifyDataSetChanged();

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.prod_list_model4, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i1) {

            final Datum item = list.get(i1);

            //viewHolder.setIsRecyclable(false);


            viewHolder.title.setText(item.getName());
            viewHolder.brand.setText(item.getBrand());


            viewHolder.quantity.setText(item.getQuantity());

            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int q = Integer.parseInt(item.getQuantity());

                    if (q < 99) {

                        q++;

                        viewHolder.quantity.setText(String.valueOf(q));

                        bar.setVisibility(View.VISIBLE);

                        Bean b = (Bean) context.getApplicationContext();

                        base = b.baseurl;

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(b.baseurl)
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

                        Call<singleProductBean> call = cr.updateCart(item.getPid(), String.valueOf(q), item.getPrice());

                        call.enqueue(new Callback<singleProductBean>() {
                            @Override
                            public void onResponse(Call<singleProductBean> call, Response<singleProductBean> response) {

                                if (response.body().getStatus().equals("1")) {
                                    loadCart();
                                }

                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                bar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onFailure(Call<singleProductBean> call, Throwable t) {
                                bar.setVisibility(View.GONE);
                            }
                        });

                    }


                }
            });


            viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int q = Integer.parseInt(item.getQuantity());

                    if (q > 1) {

                        q--;

                        viewHolder.quantity.setText(String.valueOf(q));

                        bar.setVisibility(View.VISIBLE);

                        Bean b = (Bean) context.getApplicationContext();

                        base = b.baseurl;

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(b.baseurl)
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

                        Call<singleProductBean> call = cr.updateCart(item.getPid(), String.valueOf(q), item.getPrice());

                        call.enqueue(new Callback<singleProductBean>() {
                            @Override
                            public void onResponse(Call<singleProductBean> call, Response<singleProductBean> response) {

                                if (response.body().getStatus().equals("1")) {
                                    loadCart();
                                }

                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                bar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onFailure(Call<singleProductBean> call, Throwable t) {
                                bar.setVisibility(View.GONE);
                            }
                        });

                    }


                }
            });


            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    bar.setVisibility(View.VISIBLE);

                    Bean b = (Bean) context.getApplicationContext();

                    base = b.baseurl;

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(b.baseurl)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

                    Call<singleProductBean> call = cr.deleteCart(item.getPid());

                    call.enqueue(new Callback<singleProductBean>() {
                        @Override
                        public void onResponse(Call<singleProductBean> call, Response<singleProductBean> response) {

                            if (response.body().getStatus().equals("1")) {
                                loadCart();
                            }

                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            bar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onFailure(Call<singleProductBean> call, Throwable t) {
                            bar.setVisibility(View.GONE);
                            Log.d("error", t.toString());
                        }
                    });

                }
            });


            viewHolder.price.setText("\u20B9 " + item.getPrice());


            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(false).build();
            ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(item.getImage(), viewHolder.imageView, options);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            ImageButton delete;

            Button add, remove;
            TextView quantity, title, brand, price;


            ViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.image);
                //buy = itemView.findViewById(R.id.play);


                delete = itemView.findViewById(R.id.delete);

                add = itemView.findViewById(R.id.increment);
                remove = itemView.findViewById(R.id.decrement);
                quantity = itemView.findViewById(R.id.display);
                title = itemView.findViewById(R.id.textView20);
                brand = itemView.findViewById(R.id.textView21);
                price = itemView.findViewById(R.id.textView22);

                //buy.setSideTapEnabled(true);

                //name = itemView.findViewById(R.id.name);


            }
        }
    }



}
