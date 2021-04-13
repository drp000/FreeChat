package com.drp.freechat.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.drp.freechat.R;


/**
 * @author durui
 * @date 2020/8/14
 * @description
 */
public class LoadingDialog extends Dialog {
    private TextView tv_text;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        /**设置对话框背景透明*/
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_loading);
        tv_text = (TextView) findViewById(R.id.tv_text);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public LoadingDialog setMessage(String message) {
        tv_text.setText(message);
        return this;
    }
}
