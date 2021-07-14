package com.wfour.onlinestoreapp.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.wfour.onlinestoreapp.R;
import com.wfour.onlinestoreapp.view.chat.groupchat.GroupChatFragment;

import java.util.ArrayList;
import java.util.List;

public class ChatTemplateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> templates= new ArrayList<>();
    private GroupChatFragment.TemplateInterface onlick;
    public  ChatTemplateAdapter(List<String> templates, GroupChatFragment.TemplateInterface onClickListener){
        this.templates.addAll(templates);
        this.onlick = onClickListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new  ItemChat(view);
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemChat){
            ((ItemChat) holder).tvText.setText(templates.get(position));
            holder.itemView.setOnClickListener(v->{
                onlick.onclick(templates.get(position));
            });
        }
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }
    private  class ItemChat extends RecyclerView.ViewHolder {

        TextView tvText;

        public ItemChat(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }
}
