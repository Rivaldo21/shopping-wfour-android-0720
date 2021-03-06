package com.wfour.onlinestoreapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.wfour.onlinestoreapp.AppController;
import com.wfour.onlinestoreapp.R;
import com.wfour.onlinestoreapp.base.ApiResponse;
import com.wfour.onlinestoreapp.base.BaseActivity;
import com.wfour.onlinestoreapp.datastore.DataStoreManager;
import com.wfour.onlinestoreapp.globals.GlobalFunctions;
import com.wfour.onlinestoreapp.network.modelmanager.ModelManager;
import com.wfour.onlinestoreapp.network.modelmanager.ModelManagerListener;
import com.wfour.onlinestoreapp.objects.UserObj;
import com.wfour.onlinestoreapp.view.fragments.AllDealsFragment;
import com.wfour.onlinestoreapp.view.fragments.BillManagementFragment;
import com.wfour.onlinestoreapp.view.fragments.ConfirmInformationFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class BillTakingActivity extends BaseActivity implements View.OnClickListener {

    private TextView btnGoBy, btnBill, tvDelivery, tvBillCode;

    private Toolbar toolbar;
    private AllDealsFragment mAllDealsFragment;
    private BillManagementFragment mBillManagementFragment;

    public static final String BILL = "bill";
    private String delivery;
    private String billCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(this.getString(R.string.Bill_Information));

    }

    @Override
    protected ToolbarType getToolbarType() {
        return ToolbarType.NONE;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_billtaking;
    }

    @Override
    protected void getExtraData(Intent intent) {

    }

    @Override
    protected void initilize() {

    }

    @Override
    protected void initView() {

        tvDelivery = findViewById(R.id.tvDelivery);
        tvBillCode = findViewById(R.id.tvBillCode);
        btnBill = findViewById(R.id.btnBill);
        btnGoBy = findViewById(R.id.btnGoBy);

        btnGoBy.setOnClickListener(this);
        btnBill.setOnClickListener(this);

//        ImageView gif_done= findViewById(R.id.gif_done);
//        DrawableImageViewTarget target = new DrawableImageViewTarget(gif_done);
//
//        Glide.with(self).load(R.drawable.logo_cash)
//                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
//                .into(target);

        getData();
        getProfile();
    }

    @Override
    protected void onViewCreated() {

    }

    private void getProfile() {
        if (DataStoreManager.getUser() != null) {
            String id = DataStoreManager.getUser().getId();
            final String passWord = DataStoreManager.getUser().getPassWord();

            ModelManager.getProfile(self, id, new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    try {
                        JSONObject jsonObject = new JSONObject(object.toString());
                        ApiResponse response = new ApiResponse(jsonObject);

                        if (!response.isError()) {
                            UserObj userObj = response.getDataObject(UserObj.class);
                            userObj.setPassWord(passWord);
                            userObj.setRememberMe(DataStoreManager.getUser().isRememberMe());
                            DataStoreManager.saveUser(userObj);
                            AppController.getInstance().setUserUpdated(true);
                        } else {
                            Toast.makeText(self, response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError() {

                }
            });

        }
    }


    @Override
    public void onClick(View view) {
        if(view == btnGoBy){
            GlobalFunctions.startActivityCart(self, MainActivity.class);
            finish();
        }else if(view == btnBill){
            Intent intent = new Intent(self, MainActivity.class);
            intent.putExtra(BILL, "bill");
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            GlobalFunctions.startActivityCart(self, MainActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(){Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            delivery = bundle.getString(ConfirmInformationFragment.DELIVERY);
            billCode = bundle.getString("billCode");
            tvDelivery.setText(delivery);
            tvBillCode.setText(billCode);
        }
    }
}
