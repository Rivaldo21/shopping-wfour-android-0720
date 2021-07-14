package com.wfour.onlinestoreapp.view.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.wfour.onlinestoreapp.R;
import com.wfour.onlinestoreapp.base.ApiResponse;
import com.wfour.onlinestoreapp.datastore.DataStoreManager;
import com.wfour.onlinestoreapp.globals.Constants;
import com.wfour.onlinestoreapp.network.modelmanager.ModelManager;
import com.wfour.onlinestoreapp.network.modelmanager.ModelManagerListener;
import com.wfour.onlinestoreapp.network1.NetworkUtility;
import com.wfour.onlinestoreapp.objects.SettingsObj;
import com.wfour.onlinestoreapp.utils.AppUtil;
import com.wfour.onlinestoreapp.view.fragments.WebViewFragment;

import org.json.JSONObject;

public class ParentWebview extends BaseActivity {
    private Toolbar toolbar;
    private TextView tv_title;
    public static FragmentTransaction fragmentTransaction;
    private WebViewFragment mWebViewFragment;

    @Override
    void inflateLayout() {
        setContentView(R.layout.activity_parent_layout);
    }

    @Override
    void initUI() {
        toolbar = findViewById(R.id.toolbar);
        tv_title = toolbar.findViewById(R.id.tv_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v->{
            onBackPressed();
        });
        String page = getIntent().getStringExtra("page");
        if(page.equals("faq")){
            toolbar.setTitle(getString(R.string.faq));
        }else if(page.equals("aboutUs")){
            toolbar.setTitle(getString(R.string.about_us));
        }else{
            toolbar.setTitle(getString(R.string.help));
        }
        showScreen(page);
    }

    @Override
    void initControl() {

    }

    private void showScreen(String page) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        SettingsObj setting = DataStoreManager.getSettingUtility();
        if (setting == null) {
            if (NetworkUtility.getInstance(this).isNetworkAvailable()) {
                ModelManager.getSettingUtility(this, new ModelManagerListener() {
                    @Override
                    public void onSuccess(Object object) {
                        JSONObject jsonObject = (JSONObject) object;
                        ApiResponse apiResponse = new ApiResponse(jsonObject);
                        if (!apiResponse.isError()) {
                            DataStoreManager.saveSettingUtility(jsonObject.toString());
                            SettingsObj utitlityObj = apiResponse.getDataObject(SettingsObj.class);
                            String keyUrl;
                            if(page.equals("faq")){
                                keyUrl=utitlityObj.getFaq();
                            }else if(page.equals("aboutUs")){
                                keyUrl=utitlityObj.getAbout();
                            }else{
                                keyUrl=utitlityObj.getHelp();
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.KEY_URL, keyUrl);
                            mWebViewFragment = WebViewFragment.newInstance(bundle);

                            fragmentTransaction.replace(R.id.frl_main, mWebViewFragment).commit();

                        }

                    }

                    @Override
                    public void onError() {
                    }
                });
            } else {
                AppUtil.showToast(getApplicationContext(), R.string.msg_network_not_available);
            }
        }else{
            String keyUrl;
            if(page.equals("faq")){
                keyUrl=DataStoreManager.getSettingUtility().getFaq();
            }else if(page.equals("aboutUs")){
                keyUrl=DataStoreManager.getSettingUtility().getAbout();
            }else{
                keyUrl=DataStoreManager.getSettingUtility().getHelp();
            }
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_URL, keyUrl);
            mWebViewFragment = WebViewFragment.newInstance(bundle);

            fragmentTransaction.replace(R.id.frl_main, mWebViewFragment).commit();

        }
    }
}
