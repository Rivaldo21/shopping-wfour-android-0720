package com.wfour.onlinestoreapp.view.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wfour.onlinestoreapp.R;
import com.wfour.onlinestoreapp.base.ApiResponse;
import com.wfour.onlinestoreapp.datastore.DataStoreManager;
import com.wfour.onlinestoreapp.network.BaseRequest;
import com.wfour.onlinestoreapp.network.modelmanager.RequestManger;
import com.wfour.onlinestoreapp.network.modelmanager.singletonmanager.CartManager;
import com.wfour.onlinestoreapp.objects.CartObj;
import com.wfour.onlinestoreapp.objects.DealReviewObj;
import com.wfour.onlinestoreapp.objects.ProductObj;
import com.wfour.onlinestoreapp.quickblox.conversation.utils.DialogUtil;
import com.wfour.onlinestoreapp.utils.ImageUtil;
import com.wfour.onlinestoreapp.utils.StringUtil;
import com.wfour.onlinestoreapp.view.Dialogs.Dialog_Cart;
import com.wfour.onlinestoreapp.view.adapters.AdapterViewPaggerImage;
import com.wfour.onlinestoreapp.view.adapters.DealReviewAdapter;
import com.wfour.onlinestoreapp.globals.Args;
import com.wfour.onlinestoreapp.globals.GlobalFunctions;
import com.wfour.onlinestoreapp.network.modelmanager.ModelManager;
import com.wfour.onlinestoreapp.network.modelmanager.ModelManagerListener;
import com.wfour.onlinestoreapp.network1.NetworkUtility;
import com.wfour.onlinestoreapp.objects.DealObj;
import com.wfour.onlinestoreapp.objects.SettingsObj;
import com.wfour.onlinestoreapp.utils.AppUtil;
import com.wfour.onlinestoreapp.view.chat.mainchat.LoginChatActivity;
import com.wfour.onlinestoreapp.view.fragments.CartListFragment;
import com.wfour.onlinestoreapp.widgets.recyclerview.EndlessRecyclerOnScrollListener;
import com.wfour.onlinestoreapp.widgets.textview.TextViewBold;
import com.wfour.onlinestoreapp.widgets.textview.TextViewRegular;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Suusoft on 11/24/2016.
 */

public class DealDetailActivity extends com.wfour.onlinestoreapp.base.BaseActivity implements View.OnClickListener, EndlessRecyclerOnScrollListener.OnLoadMoreListener {
    private static final String TAG = DealDetailActivity.class.getName();
    public static final String TITLE = "TITLE";
    public static final String PRICE = "PRICE";
    public static final String IMAGE = "IMAGE";
    private static final int RC_ACTIVATE_DEAL = 1;
    private static final int RQ_UPDATE_DEAL = 332;
    public static final int RC_UPDATE_DEAL = 344;
    public static int coun = 1;
    /*reskin*/
    private ImageView img, imgContent, imgFavorite, back_imageview, cart_imageview, share_imageview;
    private TextView tvAddress, tvFavoriteCount, tvPrice, tvOldPrice, lblSalePercent,
            tvName, tvAbout, tvFileName, lblRateQuantity, tvEndTime, tvDeal, btnBuy, tvContent, tvStatus, tvBrand, tvType, tvFavotite, image_number_tv, product_information_tv;
    private WebView webview;

    private RelativeLayout btnDeal;
    private LinearLayout llBtnDeal;
    private TextView btnUpdate, btnRenewDeal, btnDeactivate;

    private RatingBar ratingBar;
    private LinearLayout llParentTime;
    private RecyclerView rcvData;
    private DealReviewAdapter mAdapter;
    private ArrayList<DealReviewObj> mDatas;
    private EndlessRecyclerOnScrollListener onScrollListener;

    private int page = 1;
    //end reskin

    private MenuItem itemActivate;
    private MenuItem itemDeActivate;
    private Menu menu;
    private MenuInflater inflater;

    //private DealObj item;
    private ProductObj item, cart, productObj;
    private CartObj cartObj;
    private int percent;
    private Bundle args;
    //private ArrayList<CartObj> cartObjList;
    private ArrayList<ProductObj> productObjList;
    private int count = 0;

    private String cartString;
    private String idDeal;
    private ViewPager mViewPager;
    private CircleIndicator mIndicator;
    private AdapterViewPaggerImage adapterIndicator;
    private View screenFavorite;
    private CountObj countObj;
    private Bundle bundle;
    private ArrayList<CartObj> cartList;
    private ImageView circleImageViewMessage;

    RelativeLayout add_to_cart_relative;
    SharedPreferences prefs;
    public static final String MyPREFERENCES = "last_seen_list";
    SharedPreferences sharedPreferences;
    static ArrayList<ProductObj> last_seen_List = new ArrayList<ProductObj>();

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public static FragmentTransaction fragmentTransaction;
    public static CartListFragment mCartFragment;

    String action = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ToolbarType getToolbarType() {
        return ToolbarType.NAVI;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_deal_detail;
    }

    //add_last_seen
    @Override
    protected void getExtraData(Intent intent) {
        args = intent.getExtras();
        if (args != null) {
            prefs = this.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            try {
                Gson gson = new Gson();
                String json = prefs.getString("codeList", null);
                if (json != null) {
                    Type type = new TypeToken<ArrayList<ProductObj>>() {
                    }.getType();
                    last_seen_List = gson.fromJson(json, type);
//                    Log.e("lastseensize_in_deal", last_seen_List.size() + "");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                cartString = args.getString("cart");
                item = args.getParcelable(Args.KEY_PRODUCT_OBJECT);

//                if (last_seen_List.size() == 4) {
//                    last_seen_List.remove(0);
//                }
                if (!check_last_Seen_Exists(item.getId())) {
                    last_seen_List.add(item);
                    String jsonText = new Gson().toJson(last_seen_List);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("codeList", jsonText);
                    editor.apply();
                    editor.commit();
                    Toast.makeText(DealDetailActivity.this,"Saved",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }
    }

    boolean check_last_Seen_Exists(String id) {
        boolean b = false;

        int i = 0;
        while (i < last_seen_List.size()) {

            ProductObj obj = last_seen_List.get(i);
            if (obj.getId().equals(id)) {
                b = true;
                return b;
            }
            i++;
        }
        return b;
    }

    private void initViewPager() {
        screenFavorite = findViewById(R.id.screen_favorite);
        mViewPager = findViewById(R.id.view_pager);
        mIndicator = findViewById(R.id.circle_indicator);

        adapterIndicator = new AdapterViewPaggerImage(self, item.getImage_files());
        screenFavorite.setNestedScrollingEnabled(false);
        setPageAdapter();
    }

    private void setPageAdapter() {
        mViewPager.setAdapter(adapterIndicator);
        mIndicator.setViewPager(mViewPager);
        adapterIndicator.registerDataSetObserver(mIndicator.getDataSetObserver());
        image_number_tv.setText("1" + "/" + adapterIndicator.getCount());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {

//                Log.e("positionnnn", "Total " + adapterIndicator.getCount() + " selected position =" + position);
                image_number_tv.setText(position + "/" + adapterIndicator.getCount());
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle args = intent.getExtras();
        if (args.containsKey(Args.NOTIFICATION_TYPE)) {

            processNotificationNewDeal(args);
        } else if (args.containsKey(Args.KEY_DEAL_OBJECT)) {
            DealObj item = args.getParcelable(Args.KEY_DEAL_OBJECT);
            getDetailDeal(item.getId());

        }
    }

    @Override
    protected void initilize() {
        if (item != null) {
            setToolbarTitle(item.getTitle());
        }
    }

    @Override
    protected void initView() {
        init();
    }

    private void init() {
        webview = findViewById(R.id.web_view);
        tvDeal = findViewById(R.id.tv_btn);
        btnDeal = findViewById(R.id.btn_functions);
        tvName = findViewById(R.id.tvName);
        imgFavorite = findViewById(R.id.img_favorite);
        tvFavotite = findViewById(R.id.tv_favorite);
        tvPrice = findViewById(R.id.tvPrice);
        tvOldPrice = findViewById(R.id.lbl_price_old);
        tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        img = findViewById(R.id.image);
        imgContent = findViewById(R.id.imgContent);
//        stock_tv = findViewById(R.id.stock_tv);
//        brand_tv = findViewById(R.id.brand_tv);
        image_number_tv = findViewById(R.id.image_number_tv);
//        dikirim_tv = findViewById(R.id.dikirim_tv);
        product_information_tv = findViewById(R.id.product_information_tv);
        btnBuy = findViewById(R.id.btnBuy);
        add_to_cart_relative = findViewById(R.id.add_to_cart_relative);
        circleImageViewMessage = findViewById(R.id.img_message);
        if (DataStoreManager.getUser() == null) {
            circleImageViewMessage.setVisibility(View.VISIBLE);
        } else if (DataStoreManager.getUser().getRole() == 30) {
//            circleImageViewMessage.setVisibility(View.GONE);
            circleImageViewMessage.setVisibility(View.VISIBLE);
        } else {
            circleImageViewMessage.setVisibility(View.VISIBLE);
        }

        back_imageview = findViewById(R.id.back_imageview);
        cart_imageview = findViewById(R.id.cart_imageview);
        share_imageview = findViewById(R.id.share_imageview);
        back_imageview.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DealDetailActivity.super.onBackPressed();
                    }
                }
        );
        cart_imageview.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DealDetailActivity.this, CartActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );
        add_to_cart_relative.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        showDialog();
                        setOrder();
                    }
                }
        );
        share_imageview.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareText("Product", item.getTitle() + " , Price $" + item.getPrice());
                    }
                }
        );
        getToolbar().setVisibility(View.GONE);
        circleImageViewMessage.setOnClickListener(this);
//        tvType = findViewById(R.id.tvType);
//        tvBrand = findViewById(R.id.tvBrand);
//        tvStatus = findViewById(R.id.tvStatus);
        btnBuy.setOnClickListener(this);
        imgFavorite.setOnClickListener(this);
        tvFavotite.setOnClickListener(this);

        //ImageUtil.setImage(self, img, item.getImage());
        tvName.setText(item.getTitle());
        if (item.getIs_prize() == 1) {
            tvPrice.setText(" P." + StringUtil.convertNumberToString(item.getPrice(), 2));
        } else {
            tvPrice.setText(" $" + StringUtil.convertNumberToString(item.getPrice(), 2));
        }

        if (item.getOld_price() == 0) {
            tvOldPrice.setVisibility(View.GONE);
        }
        if (item.getIs_prize() == 1) {
            tvOldPrice.setText(" P." + StringUtil.convertNumberToString(item.getOld_price(), 2));
        } else {
            tvOldPrice.setText(" $" + StringUtil.convertNumberToString(item.getOld_price(), 2));
        }
//        tvContent.setText(item.getContent());
        ImageUtil.setImage(self, imgContent, item.getImage());
//        tvType.setText(item.getType());
//        tvBrand.setText(item.getBrand());
//        tvStatus.setText(item.getStatus());
        webview.loadData(item.getContent(), "text/html", "UTF-8");

        bundle = getIntent().getExtras();
        idDeal = bundle.getParcelable(Args.KEY_ID_DEAL);
        Log.e(TAG, "init: " + item.getId());

        getDetailDeal(item.getId());

        //getFavorite();
        initViewPager();
    }

    public void shareText(String subject, String body) {
        Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
        txtIntent.setType("text/plain");
        txtIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        txtIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(txtIntent, "Share"));
    }

    @Override
    public void onLoadMore(int page) {

    }

    @Override
    public void onClick(View v) {
        if (v == btnBuy) {
            showDialog();
        } else if (v == circleImageViewMessage) {
            if (DataStoreManager.getUser() == null) {
                AppUtil.showToast(self, "You are not logged in");
            } else if (DataStoreManager.getUser() != null) {
                Intent messager = new Intent(getApplicationContext(), LoginChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Args.KEY_DEAL_OBJECT, DealDetailActivity.this.item);
                messager.putExtras(bundle);
                startActivity(messager);
            }
        } else if (v == imgFavorite) {
            if (DataStoreManager.getUser() != null) {
                addFavorite();
            }
        } else if (v == tvFavotite) {
            addFavorite();
        }
    }

    @Override
    protected void onResume() {
        GlobalFunctions.getCountCart(cartList, count);
        count = DataStoreManager.getCountCart();
        invalidateOptionsMenu();

        productObj = new ProductObj();
        productObj = item;
        super.onResume();
    }

    private void showDialog() {
        Dialog_Cart dialogCart = new Dialog_Cart();
        dialogCart.show(getSupportFragmentManager(), "DialogCart");

        Bundle bundle = new Bundle();
        bundle.putParcelable(Args.KEY_PRODUCT_OBJECT, productObj);
        dialogCart.setArguments(bundle);
    }

    @Override
    protected void onViewCreated() {

        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setResult(RC_UPDATE_DEAL);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Args.KEY_DEAL_OBJECT, item);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RC_UPDATE_DEAL, intent);
                Log.e(TAG, "setResult(RC_UPDATE_DEAL, intent)");
                finish();
            }
        });
    }

    public int percentPriceOldAndPriceNew(double priceOld, double priceNew) {
        int i = (int) (((priceOld - priceNew) / priceOld) * 100);
        return i;
    }

    public int percentPriceOldAndPriceSale(double priceOld, double priceSale) {
        int i = (int) ((priceSale / priceOld) * 100);
        return i;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        MenuItem menuItem = menu.findItem(R.id.action_cart);
        menuItem.setIcon(GlobalFunctions.buildCounterDrawable(self, count, R.drawable.ic_cart));
        return super.onCreateOptionsMenu(menu);
    }

    private void initMenu(Menu menu) {
        if (itemActivate == null && itemDeActivate == null) {
            getMenuInflater().inflate(R.menu.menu_detail_deal, menu);
            itemActivate = menu.findItem(R.id.action_activate);
            itemDeActivate = menu.findItem(R.id.action_deactivate);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem items) {
        int id = items.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_cart:
                GlobalFunctions.startActivityWithoutAnimation(self, CartActivity.class);
                break;
            case R.id.action_update:
                Intent intent = new Intent(getApplicationContext(), UpdateDealActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Args.KEY_DEAL_OBJECT, DealDetailActivity.this.item);
                intent.putExtras(bundle);
                startActivityForResult(intent, RQ_UPDATE_DEAL);
                break;
            case R.id.action_activate:
                if (NetworkUtility.getInstance(getApplicationContext()).isNetworkAvailable()) {
                    // activateDeal(DealDetailActivity.this.item.getId(), Constants.ON);
                } else {
                    AppUtil.showToast(getApplicationContext(), R.string.msg_network_not_available);
                }

                break;
            case R.id.action_deactivate:
                if (NetworkUtility.getInstance(getApplicationContext()).isNetworkAvailable()) {
                    //activateDeal(DealDetailActivity.this.item.getId(), Constants.OFF);
                } else {
                    AppUtil.showToast(getApplicationContext(), R.string.msg_network_not_available);
                }

                break;
            case R.id.action_renew_deal:
                showDurationBuyingDialog(R.string.msg_enter_deal_duration_to_be_available);
                break;

//            case R.id.action_message:
//                Intent messager = new Intent(getApplicationContext(), LoginChatActivity.class);
//                Bundle bundle1 = new Bundle();
//                bundle1.putParcelable(Args.KEY_DEAL_OBJECT, DealDetailActivity.this.item);
//                messager.putExtras(bundle1);
//                startActivity(messager);
//                break;
        }
        return super.onOptionsItemSelected(items);
    }

    private void addFavorite() {
        if (DataStoreManager.getUser() != null) {
            Log.e("user_id ", DataStoreManager.getUser().getId());
            Log.e("item_id ", item.getId());
            Log.e("object_type", "deal");
            Log.d("request data", ">>>");
            RequestManger.addFavorite(DataStoreManager.getUser().getId(), item.getId(), "deal", new BaseRequest.CompleteListener() {
                @Override
                public void onSuccess(com.wfour.onlinestoreapp.network.ApiResponse response) {
                    Log.d("request data", ">>>"+response);
                    if (!response.isError()) {
                        AppUtil.showToast(self, response.getMessage() + "");
                        imgFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_favorite));
                    } else {
                        AppUtil.showToast(self, response.getMessage() + "");
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "onError: " + message);
                }
            });
        }
    }

    private void getFavorite() {
        if (DataStoreManager.getUser() != null) {
            RequestManger.getFavorite(DataStoreManager.getUser().getToken(), item.getId(), new BaseRequest.CompleteListener() {
                @Override
                public void onSuccess(com.wfour.onlinestoreapp.network.ApiResponse response) {
                    if (!response.isError()) {
                        ProductObj productObj = response.getDataObject(ProductObj.class);
                        AppUtil.showToast(self, response.getMessage() + "");
                        if (productObj.getIs_favourite() == 1) {
                            imgFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_favorite));
                        }

                    } else {
                        AppUtil.showToast(self, response.getMessage() + "");
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "onError: " + message);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_UPDATE_DEAL) {
            Log.e(TAG, "requestCode == RQ_UPDATE_DEAL");
            if (resultCode == Args.RC_UPDATE_DEAL) {

                Log.e(TAG, "resultCode == Args.RC_UPDATE_DEAL");
                Bundle args = data.getExtras();
                if (args != null) {
                    if (args.containsKey(Args.KEY_DEAL_OBJECT)) {
                        item = args.getParcelable(Args.KEY_DEAL_OBJECT);
                        setToolbarTitle(item.getTitle());

                    }
                }
//                finish();
            }

        } else
            //Reskin
            if (requestCode == RC_ACTIVATE_DEAL) {
                if (resultCode == Activity.RESULT_OK) {
                    // Update deal status(active or inactive) after coming back from Chat screen
                    if (item != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            if (bundle.containsKey(Args.IS_ACTIVATED_DEAL)) {
                                //item.setIs_online(bundle.getBoolean(Args.IS_ACTIVATED_DEAL) ? DealObj.DEAL_ACTIVE : DealObj.DEAL_INACTIVE);

                                // Refresh deal object in parent activity
                                //getItem().setIs_online(bundle.getBoolean(Args.IS_ACTIVATED_DEAL) ? DealObj.DEAL_ACTIVE : DealObj.DEAL_INACTIVE);

                            }
                        }
                    }
                }
            }
        //end reskin
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public ProductObj getItem() {
        return item;
    }


    private void showDurationBuyingDialog(int msgId) {
        final Dialog dialog = DialogUtil.setDialogCustomView(self, R.layout.dialog_buying_duration, true);

        final EditText txtDuration = (EditText) dialog.findViewById(R.id.txt_duration);
        TextViewRegular lblMsg = (TextViewRegular) dialog.findViewById(R.id.lbl_msg);
        final TextViewRegular lblFee = (TextViewRegular) dialog.findViewById(R.id.lbl_msg_fee);
        TextViewBold lblBuyCredits = (TextViewBold) dialog.findViewById(R.id.lbl_buy_credits);
        TextViewBold lblAvailable = (TextViewBold) dialog.findViewById(R.id.lbl_available);

        lblMsg.setText(msgId);
        lblFee.setText(String.format(getString(R.string.msg_subtract_credits), "0"));

        SettingsObj settingsObj = DataStoreManager.getSettingUtility();


        txtDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String duration = editable.toString().trim().isEmpty() ? "0" : editable.toString().trim();

                // lblFee.setText(String.format(getString(R.string.msg_subtract_credits), String.valueOf((Integer.parseInt(duration) * feePerHour))));
            }
        });

        lblBuyCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                GlobalFunctions.startActivityWithoutAnimation(self, BuyCreditsActivity.class);
            }
        });

        lblAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int duration = 0;
                if (txtDuration.getText().toString().trim().length() > 0) {
                    duration = Integer.parseInt(txtDuration.getText().toString().trim());
                }

                if (duration == 0 || duration > 24) {
                    Toast.makeText(self, R.string.msg_duration_must_gt_zero, Toast.LENGTH_LONG).show();
                } else {
                    if (DataStoreManager.getUser().getBalance() < duration) {
                        Toast.makeText(self, String.format(getString(R.string.msg_balance_is_not_enough),
                                String.valueOf(duration)), Toast.LENGTH_LONG).show();
                    } else {
                        dialog.dismiss();

                        if (item != null) {
                            // updateDurationOfDeal(item.getId(), duration, (duration * feePerHour));
                        }
                    }
                }
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void processNotificationNewDeal(Bundle args) {
        if (args != null) {
            if (args.containsKey(Args.KEY_ID_DEAL)) {
                String id = args.getString(Args.KEY_ID_DEAL);
                if (id != null && !id.equalsIgnoreCase(""))
                    if (NetworkUtility.getInstance(this).isNetworkAvailable()) {
                        getDetailDeal(id);
                    } else {
                        AppUtil.showToast(this, R.string.msg_network_not_available);
                    }
            }
        }
    }

    private void getDetailDeal(final String id) {
        if (DataStoreManager.getUser() != null) {
            ModelManager.getDetailDeal(this, id, DataStoreManager.getToken(), new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    org.json.JSONObject jsonObject = (JSONObject) object;
                    ApiResponse response = new ApiResponse(jsonObject);
                    if (!response.isError()) {
                        item = response.getDataObject(ProductObj.class);
                        imgFavorite.setImageResource(item.getIs_favourite() == 1 ? R.drawable.ic_heart_favorite : R.drawable.ic_heart_unfavorite);
                        if (item != null) {
//                            brand_tv.setText(item.getBrand());
//                            stock_tv.setText(item.getQuantity());
//                            dikirim_tv.setText(item.get);
                            Log.e("description", item.getContent());
                            setToolbarTitle(item.getTitle());
                            if (itemActivate == null && itemDeActivate == null) {
                                initMenu(menu);
                            }
                        } else {
                            finish();
                        }
                    }
                }

                @Override
                public void onError() {
                    Log.e(TAG, "ERROR: GET DETAIL DEAL");
                }
            });
        } else {
            Log.e(TAG, "getDetailDeal: ");
        }
    }

    private ArrayList<CartObj> cartObjList;
    private CartObj cart1;
    private String color, size;

    private void setOrder() {
        cartObjList = CartManager.getInstance().getArray();
        if (cartObjList.size() != 0) {
            boolean isExist = false;
            String id = item.getId();

            for (int i = 0; i < cartObjList.size(); i++) {
                cart1 = cartObjList.get(i);
                Log.d("detail detail ",""+id+" "+cart1.getId());
                if (color != null && size != null) {
                    if (cart.getId().equals(id)) {
                        isExist = true;
                        break;
                    }
                }
//                if (color != null && size != null) {
//                    if (cart.getId().equals(id) && cart.getColor().equals(color) && cart.getSize().equals(size)) {
//                        isExist = true;
//                        break;
//                    }
//                } else if (color != null && size == null) {
//                    if (cart.getId().equals(id) && cart.getColor().equals(color)) {
//                        isExist = true;
//                        break;
//                    }
//                } else if (color == null && size != null) {
//                    if (cart.getId().equals(id) && cart.getSize().equals(size)) {
//                        isExist = true;
//                        break;
//                    }
//                } else {
//                    if (cart != null && cart.getId().equals(id)) {
//                        isExist = true;
//                        break;
//                    }
//                }
            }
            if (isExist) {
                int number = cart.getNumber();
                number += 1;
                cart.setNumber(number);

                double money = cart.getTotalMoney();
                money = number * cart.getPrice();
                cart.setTotalMoney(money);
            } else {
                CartManager.getInstance().addItem(new CartObj(item.getId(), item.getTitle(), item.getPrice(), item.getImage(), 1, item.getPrice(), color, size, item.getOld_price(), item.getIs_prize()));

            }
        } else {
            CartManager.getInstance().addItem(new CartObj(item.getId(), item.getTitle(), item.getPrice(), item.getImage(), 1, item.getPrice(), color, size, item.getOld_price(), item.getIs_prize()));
        }

        doIncrease();
    }

    private void doIncrease() {
        count = DataStoreManager.getCountCart();
        count++;
        //getContext().invalidateOptionsMenu();
        DataStoreManager.saveCountCart(count);
        Toast.makeText(DealDetailActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
    }
}