package com.wfour.onlinestoreapp.view.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wfour.onlinestoreapp.R;
import com.wfour.onlinestoreapp.datastore.DataStoreManager;
import com.wfour.onlinestoreapp.interfaces.IMyOnClick;
import com.wfour.onlinestoreapp.network.modelmanager.singletonmanager.CartManager;
import com.wfour.onlinestoreapp.objects.CartObj;
import com.wfour.onlinestoreapp.objects.ProductObj;
import com.wfour.onlinestoreapp.utils.AppUtil;
import com.wfour.onlinestoreapp.utils.ImageUtil;
import com.wfour.onlinestoreapp.view.activities.DealDetailActivity;

import java.util.ArrayList;

public class AdapterWhistList extends RecyclerView.Adapter<AdapterWhistList.MyViewHolder> {
    ArrayList<ProductObj> productObjList;
    private Activity context;
    private IMyOnClick onClick;
    private int w, h;
    protected Context self;
    private ArrayList<ProductObj> mDatas;
    private ArrayList<ProductObj> List = new ArrayList<>();
    private OnclickRemoveItemWhistList listener;
    private ArrayList<CartObj> cartObjList;
    private CartObj cart1;
    private String color, size;
    private int count = 0;


    public void initParam(Activity activity) {
        h = AppUtil.getScreenHeight(activity);
    }

    public AdapterWhistList(Activity context, ArrayList<ProductObj> productObjList, IMyOnClick onClick) {
        this.context = context;
        self = context;
        this.productObjList = productObjList;
        this.onClick = onClick;
        initParam(context);
    }

    public void addList(ArrayList<ProductObj> dealObjList) {
        this.productObjList = dealObjList;
        this.notifyDataSetChanged();
    }

    public void setListener(OnclickRemoveItemWhistList listener) {
        this.listener = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_whistlist, parent, false);

        return new MyViewHolder(view);

    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ProductObj productObj = productObjList.get(position);
        holder.tvTitle.setText(productObj.getTitle());
        holder.tvPrice.setText("$" + (productObj.getPrice()));

        holder.tvOldPrice.setText("$" + (productObj.getOld_price()));
        ImageUtil.setImage(context, holder.imgAvatar, productObj.getImage());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null) {
                    onClick.MyOnClick(position, productObj);
                }
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.OnItemClicked(view, position);
                }
            }
        });
        holder.btnAddToCart.setOnClickListener(V->{
            setOrder(productObj);
        });

    }


    @Override
    public int getItemCount() {
        return (productObjList == null) ? 0 : productObjList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvDescription, tvPrice, tvOldPrice;
        private CardView cardView;
        private ImageView imgAvatar, remove;
        private AppCompatButton btnAddToCart;


        public MyViewHolder(final View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            cardView = itemView.findViewById(R.id.cardView);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            tvPrice = itemView.findViewById(R.id.lbl_price);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            tvOldPrice = itemView.findViewById(R.id.lbl_price_old);
            remove = itemView.findViewById(R.id.remove_whistlist);
            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public interface OnclickRemoveItemWhistList {
        void OnItemClicked(View view, int position);
    }

    private void setOrder(ProductObj item) {
        cartObjList = CartManager.getInstance().getArray();

        CartManager.getInstance().addItem(new CartObj(item.getId(), item.getTitle(), item.getPrice(), item.getImage(), 1, item.getPrice(), color, size, item.getOld_price(), item.getIs_prize()));
        doIncrease();
    }

    private void doIncrease() {
        count = DataStoreManager.getCountCart();
        count++;
        //getContext().invalidateOptionsMenu();
        DataStoreManager.saveCountCart(count);
        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
    }
}
