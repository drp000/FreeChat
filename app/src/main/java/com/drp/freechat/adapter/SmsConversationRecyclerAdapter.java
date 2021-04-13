package com.drp.freechat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.drp.freechat.R;
import com.drp.freechat.entity.SmsConversations;
import com.drp.freechat.util.Constants;
import com.drp.freechat.util.TimeUtil;

import java.util.List;

public class SmsConversationRecyclerAdapter extends RecyclerView.Adapter {

    public void setData(List<SmsConversations> list) {
        this.list = list;
    }

    class SmsViewHolder extends RecyclerView.ViewHolder {

        private TextView item_sms_date;
        private TextView item_sms_body;
        private LinearLayout ll_content;

        public SmsViewHolder(View itemView) {
            super(itemView);
            item_sms_date = itemView.findViewById(R.id.item_sms_date);
            item_sms_body = itemView.findViewById(R.id.item_sms_body);
            ll_content = itemView.findViewById(R.id.ll_content);
            ll_content.setHorizontalGravity(Gravity.LEFT);
        }
    }

    private Context mContext;
    private List<SmsConversations> list;
    private SmsItemClickListener mListener;

    public SmsConversationRecyclerAdapter(Context context, List<SmsConversations> list, SmsItemClickListener listener) {
        this.mContext = context;
        this.list = list;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms, parent, false);
        SmsViewHolder holder = new SmsViewHolder(inflater);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (list == null) {
            return;
        }
        SmsConversations conversations = list.get(position);
        ((SmsViewHolder) holder).item_sms_date.setText(TimeUtil.formatTimeStampString(mContext, conversations.getDate(), false));
        ((SmsViewHolder) holder).item_sms_body.setText(conversations.getBody());
        if (conversations.getType() == Constants.TYPE_RECEIVE) {
            ((SmsViewHolder) holder).ll_content.setHorizontalGravity(Gravity.LEFT);
            ((SmsViewHolder) holder).item_sms_body.setBackground(mContext.getDrawable(R.drawable.shap_message_come));
            ((SmsViewHolder) holder).item_sms_body.setTextColor(Color.parseColor("#000000"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 120, 0);
            ((SmsViewHolder) holder).item_sms_body.setLayoutParams(lp);
        } else {
            ((SmsViewHolder) holder).ll_content.setHorizontalGravity(Gravity.RIGHT);
            ((SmsViewHolder) holder).item_sms_body.setBackground(mContext.getDrawable(R.drawable.shap_message_go));
            ((SmsViewHolder) holder).item_sms_body.setTextColor(Color.parseColor("#FFFFFF"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(120, 0, 0, 0);
            ((SmsViewHolder) holder).item_sms_body.setLayoutParams(lp);
        }
        ((SmsViewHolder) holder).ll_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mListener.onSmsItemClick(smsItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public interface SmsItemClickListener {
        void onSmsItemClick();
    }
}
