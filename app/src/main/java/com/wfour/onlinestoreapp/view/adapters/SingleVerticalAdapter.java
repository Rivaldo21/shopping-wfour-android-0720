package com.wfour.onlinestoreapp.view.adapters;

import android.app.Activity;
import android.graphics.Paint;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wfour.onlinestoreapp.R;
import com.wfour.onlinestoreapp.datastore.DataStoreManager;
import com.wfour.onlinestoreapp.interfaces.IMyOnClick;
import com.wfour.onlinestoreapp.network.BaseRequest;
import com.wfour.onlinestoreapp.network.modelmanager.RequestManger;
import com.wfour.onlinestoreapp.objects.HomeObj;
import com.wfour.onlinestoreapp.objects.ProductObj;
import com.wfour.onlinestoreapp.utils.AppUtil;
import com.wfour.onlinestoreapp.utils.ImageUtil;
import com.wfour.onlinestoreapp.utils.StringUtil;

import java.util.ArrayList;

public class SingleVerticalAdapter extends RecyclerView.Adapter<SingleVerticalAdapter.MyViewHolder> {
    ArrayList<ProductObj> productObjList;
    ArrayList<ProductObj> productObjListFull;
    private Activity context;
    private IMyOnClick onClick;
    private HomeObj homeObj;
    private ArrayList<HomeObj> homeObjList;
    private int w, h;

    public void initParam(Activity activity) {
        w = AppUtil.getScreenWidth(activity);
        h = AppUtil.getScreenHeight(activity);
    }

    public SingleVerticalAdapter(Activity context, ArrayList<ProductObj> productObjList, IMyOnClick onClick) {
        this.context = context;
        this.productObjList = productObjList;
        this.onClick = onClick;
        initParam(context);
    }

    public void addList(ArrayList<ProductObj> dealObjList) {
        this.productObjList = dealObjList;
        this.notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_single_vertical, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ProductObj productObj = productObjList.get(position);
        holder.tvTitle.setText(productObj.getTitle());
        holder.tvPrice.setText(" $"+ StringUtil.convertNumberToString(productObj.getPrice(),2));
        if(productObj.getOld_price()==0){
            holder.tvOldPrice.setVisibility(View.GONE);
        }
        holder.tvOldPrice.setText(" $"+ StringUtil.convertNumberToString(productObj.getOld_price(),2));
        ImageUtil.setImage(context, holder.imgAvatar, productObj.getImage());
        holder.ivFavorite.setImageResource(productObj.getIs_favourite() == 1 ? R.drawable.ic_heart_favorite : R.drawable.ic_heart_unfavorite);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null) {
                    onClick.MyOnClick(position, productObj);
                }
            }
        });
        holder.ivFavorite.setOnClickListener(v -> {
            if (DataStoreManager.getUser() != null) {
                Log.e("user_id ", DataStoreManager.getUser().getId());
                Log.e("item_id ", productObj.getId());
                Log.e("object_type", "deal");
                RequestManger.addFavorite(DataStoreManager.getUser().getId(), productObj.getId(), "deal", new BaseRequest.CompleteListener() {
                    @Override
                    public void onSuccess(com.wfour.onlinestoreapp.network.ApiResponse response) {
                        if (!response.isError()) {
                            AppUtil.showToast(context, response.getMessage() + "");
                            holder.ivFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart_favorite));
                        } else {
                            AppUtil.showToast(context, response.getMessage() + "");
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("", "onError: " + message);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return (productObjList.size() >= 10) ? 10: productObjList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvDescription, tvPrice,tvOldPrice;
        private CardView cardView;
        private ImageView imgAvatar,ivFavorite;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTitle = itemView.findViewById(R.id.tvTitle);
//            cardView = itemView.findViewById(R.id.cardView);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvPrice = itemView.findViewById(R.id.lbl_price);
            tvOldPrice = itemView.findViewById(R.id.lbl_price_old);
            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
