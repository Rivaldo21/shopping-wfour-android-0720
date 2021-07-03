package com.wfour.onlinestoreapp.view.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.wfour.onlinestoreapp.R;
import com.wfour.onlinestoreapp.datastore.DataStoreManager;
import com.wfour.onlinestoreapp.globals.Args;
import com.wfour.onlinestoreapp.globals.GlobalFunctions;
import com.wfour.onlinestoreapp.interfaces.IMyOnClick;
import com.wfour.onlinestoreapp.network.ApiResponse;
import com.wfour.onlinestoreapp.network.modelmanager.ModelManager;
import com.wfour.onlinestoreapp.network.modelmanager.ModelManagerListener;
import com.wfour.onlinestoreapp.network1.NetworkUtility;
import com.wfour.onlinestoreapp.objects.CartObj;
import com.wfour.onlinestoreapp.objects.ColorProduct;
import com.wfour.onlinestoreapp.objects.HomeObj;
import com.wfour.onlinestoreapp.objects.ProductObj;
import com.wfour.onlinestoreapp.objects.RecomendedObj;
import com.wfour.onlinestoreapp.objects.SizeProduct;
import com.wfour.onlinestoreapp.retrofit.ApiUtils;
import com.wfour.onlinestoreapp.retrofit.respone.RecommendedProductResponse;
import com.wfour.onlinestoreapp.view.adapters.SingleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends BaseActivity {

    private Toolbar toolbar;
    private String name, mFilter;
    private RecyclerView mRclDeal;

    private SingleAdapter mAdapter;
    private ArrayList<ProductObj> mDatas;
    private ArrayList<ProductObj> List = new ArrayList<>();
    private int count;
    private ArrayList<CartObj> cartList;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int page = 1;
    private int number_per_page = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        toolbar = findViewById(R.id.toolbar);
        mRclDeal = findViewById(R.id.rcv_data);

        getExtraData();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(name);

        if (mDatas == null) {
            mDatas = new ArrayList<>();
        } else {
            mDatas.clear();
        }

        mAdapter = new SingleAdapter(self, List, new IMyOnClick() {
            @Override
            public void MyOnClick(int position, ProductObj productObj) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Args.KEY_PRODUCT_OBJECT, productObj);
                GlobalFunctions.startActivityWithoutAnimation(self, DealDetailActivity.class, bundle);
            }
        });
        mRclDeal.setAdapter(mAdapter);
        final GridLayoutManager layoutManager = new GridLayoutManager(self, 2);
        mRclDeal.setLayoutManager(layoutManager);
        mAdapter.addList(List);
        mRclDeal.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount(); // so muc hien thi
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition(); // muc hien thi truoc day


                    if (loading) {

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.e("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            page = (totalItemCount / number_per_page) + 1;
                            getData(mFilter, page, number_per_page);

                        }
                    }
                }
            }
        });

    }

    public void getExtraData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            name = bundle.getString(Args.TYPE_OF_PRODUCT_NAME);
            mFilter = bundle.getString(Args.FILTER);
        }
        Log.d("getExtraData ", ">>"+name);
        if (name.equals("REKOMENDA")) {
            setRecomendedRecyclerview();
        } else if (name.equals("PRODUTU POPULAR")) {
            setPopularRecyclerview();
        } else {
            getData(mFilter, page, number_per_page);
        }

    }

    private void getData(final String mFilter, int page, int number_per_page) {
        if (NetworkUtility.getInstance(self).isNetworkAvailable()) {
            ModelManager.getProductFillter(self, mFilter, page, number_per_page, new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    JSONObject obj = (JSONObject) object;
                    ApiResponse response = new ApiResponse(obj);

                    mDatas = new ArrayList<>();
                    if (!response.isError()) {
                        mDatas.addAll(response.getDataList(ProductObj.class));
                        List.addAll(mDatas);
                        mAdapter.addList(List);
                        loading = true;

                    }
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cart, menu);
        MenuItem menuItem = menu.findItem(R.id.action_cart);
        menuItem.setIcon(GlobalFunctions.buildCounterDrawable(self, count, R.drawable.ic_cart));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            GlobalFunctions.startActivityWithoutAnimation(self, CartActivity.class);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onResume() {
        super.onResume();
        GlobalFunctions.getCountCart(cartList, count);
        count = DataStoreManager.getCountCart();
        invalidateOptionsMenu();
    }

    @Override
    void inflateLayout() {

    }

    @Override
    void initUI() {

    }

    @Override
    void initControl() {

    }

    private void setRecomendedRecyclerview() {
        ArrayList<ProductObj> recomendedjList = new ArrayList<>();

        ApiUtils.getAPIService().getRecommendedProducts().enqueue(new Callback<RecommendedProductResponse>() {
            @Override
            public void onResponse(Call<RecommendedProductResponse> call, Response<RecommendedProductResponse> response) {
                if (response.body() != null) {
                    Log.e("TAG", "recomenda" + new Gson().toJson(response.body()));
                    if (response.body().getData() != null) {
                        RecommendedProductResponse m = response.body();
                        RecomendedObj obj;
                        int i = 0;
                        ProductObj p;
                        while (i < m.getData().size()) {
                            p = new ProductObj();
                            ArrayList image_files = new ArrayList();
                            p.setApplication_id(String.valueOf(m.getData().get(i).getApplication_id()));
                            p.setBanner(m.getData().get(i).getBanner());
                            p.setBarcode_image(m.getData().get(i).getBarcode_image());
                            p.setBrand(m.getData().get(i).getBrand());
                            p.setCategory_id(m.getData().get(i).getCategory_id());
                            p.setCode(m.getData().get(i).getCode());
                            p.setContent(m.getData().get(i).getContent());
                            p.setCost(m.getData().get(i).getCost());
                            p.setCount_comments(String.valueOf(m.getData().get(i).getCount_comments()));
                            p.setCount_likes(String.valueOf(m.getData().get(i).getCount_likes()));
                            p.setCount_purchase(String.valueOf(m.getData().get(i).getCount_purchase()));
                            p.setCount_rates(String.valueOf(m.getData().get(i).getCount_rates()));
                            p.setCount_views(String.valueOf(m.getData().get(i).getCount_views()));
                            p.setCreated_date(String.valueOf(m.getData().get(i).getCreated_date()));
                            p.setCreated_user(String.valueOf(m.getData().get(i).getCreated_user()));
                            p.setCurrency(String.valueOf(m.getData().get(i).getCurrency()));
                            p.setId(String.valueOf(m.getData().get(i).getId()));
                            p.setDiscount(String.valueOf(m.getData().get(i).getDiscount()));
                            p.setImage(String.valueOf(m.getData().get(i).getImage()));
                            p.setIs_active(m.getData().get(i).getIs_active());
                            p.setIs_favourite(m.getData().get(i).getIs_favourite());
                            p.setIs_hot(m.getData().get(i).getIs_hot());
                            p.setIs_prize(m.getData().get(i).getIs_prize());
                            p.setIs_promotion(m.getData().get(i).getIs_promotion());
                            p.setIs_tax_included(m.getData().get(i).getIs_tax_included());
                            p.setIs_top(m.getData().get(i).getIs_top());
                            p.setModified_date(m.getData().get(i).getModified_date());
                            p.setModified_user(m.getData().get(i).getModified_user());
                            p.setQuantity(m.getData().get(i).getQuantity());
                            p.setPromotion_id(m.getData().get(i).getPromotion_id());
                            p.setOverview(m.getData().get(i).getOverview());
                            p.setUnit(m.getData().get(i).getUnit());
                            p.setType(m.getData().get(i).getType());
                            p.setTitle(m.getData().get(i).getTitle());
                            p.setThumbnail(m.getData().get(i).getThumbnail());
                            p.setPrice(Double.parseDouble(m.getData().get(i).getPrice()));
                            p.setOld_price(Double.parseDouble(m.getData().get(i).getOld_price()));
                            image_files = new ArrayList();
                            for (int j = 0; j < m.getData().get(i).getImage_files().size(); j++) {
                                image_files.add(m.getData().get(i).getImage_files().get(j));
                            }
                            ArrayList<SizeProduct> size_list = new ArrayList<>();
                            ArrayList<ColorProduct> color_list = new ArrayList<>();
                            p.setSizes(size_list);
                            p.setColors(color_list);
                            p.setImage_files(image_files);
                            recomendedjList.add(p);

                            i++;
                        }
                        mDatas = new ArrayList<>();
                        mDatas.addAll(recomendedjList);
                        List.addAll(mDatas);
                        mAdapter.addList(List);
                        loading = false;


                    }
                }
            }

            @Override
            public void onFailure(Call<RecommendedProductResponse> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    private void setPopularRecyclerview() {
        ArrayList<ProductObj> polularList = new ArrayList<>();
        ApiUtils.getAPIService().getPopular().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    Log.e("TAG", "popular" + new Gson().toJson(response.body()));
                    if (response.isSuccessful()) {
                        ResponseBody m = response.body();
                        try {
                            JSONObject obj = new JSONObject(response.body().string());
                            if (obj.getString("status").equals("SUCCESS")) {

                                ProductObj p;
                                JSONArray array = obj.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject item_obj = (JSONObject) array.get(i);
                                    p = new ProductObj();
                                    ArrayList image_files = new ArrayList();
                                    p.setApplication_id(String.valueOf(item_obj.getString("application_id")));
                                    p.setBanner(item_obj.getString("banner"));
                                    p.setBarcode_image(item_obj.getString("barcode_image"));
                                    p.setBrand(item_obj.getString("brand"));
                                    p.setCategory_id(item_obj.getString("category_id"));
                                    p.setCode(item_obj.getString("code"));
                                    p.setContent(item_obj.getString("content"));
                                    p.setCost(item_obj.getString("cost"));
                                    p.setCount_comments(item_obj.getString("count_comments"));
                                    p.setCount_likes(item_obj.getString("count_likes"));
                                    p.setCount_purchase(item_obj.getString("count_purchase"));
                                    p.setCount_rates(item_obj.getString("count_rates"));
                                    p.setCount_views(item_obj.getString("count_views"));
                                    p.setCreated_date(item_obj.getString("created_date"));
                                    p.setCreated_user(item_obj.getString("created_user"));
                                    p.setCurrency(item_obj.getString("currency"));
                                    p.setId(item_obj.getString("id"));
                                    p.setDiscount(item_obj.getString("discount"));
                                    p.setImage(item_obj.getString("image"));
                                    p.setIs_active(Integer.parseInt(item_obj.getString("is_active")));
                                    p.setIs_favourite(Integer.parseInt(item_obj.getString("is_favourite")));
                                    p.setIs_hot(Integer.parseInt(item_obj.getString("is_hot")));
                                    p.setIs_prize(Integer.parseInt(item_obj.getString("is_prize")));
                                    p.setIs_promotion(item_obj.getString("is_promotion"));
                                    p.setIs_tax_included(item_obj.getString("is_tax_included"));
                                    p.setIs_top(Integer.parseInt(item_obj.getString("is_top")));
                                    p.setModified_date(item_obj.getString("modified_date"));
                                    p.setModified_user(item_obj.getString("modified_user"));
                                    p.setQuantity(item_obj.getString("quantity"));
                                    p.setPromotion_id(item_obj.getString("is_promotion"));
                                    p.setOverview(item_obj.getString("overview"));
                                    p.setUnit(item_obj.getString("unit"));
                                    p.setType(item_obj.getString("type"));
                                    p.setTitle(item_obj.getString("title"));
                                    p.setThumbnail(item_obj.getString("thumbnail"));
                                    p.setPrice(Double.parseDouble(item_obj.getString("price")));
                                    p.setOld_price(Double.parseDouble(item_obj.getString("old_price")));
                                    image_files = new ArrayList();
                                    JSONArray image_array = item_obj.getJSONArray("image_files");
                                    for (int j = 0; j < image_array.length(); j++) {
                                        image_files.add(image_array.get(j));
                                    }
                                    ArrayList<SizeProduct> size_list = new ArrayList<>();
                                    ArrayList<ColorProduct> color_list = new ArrayList<>();
                                    p.setSizes(size_list);
                                    p.setColors(color_list);
                                    p.setImage_files(image_files);
                                    polularList.add(p);
                                }
                                mDatas = new ArrayList<>();
                                mDatas.addAll(polularList);
                                List.addAll(mDatas);
                                mAdapter.addList(List);
                                loading = false;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}
