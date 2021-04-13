package com.drp.freechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.drp.freechat.util.Constants;
import com.drp.freechat.util.SpUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author durui
 * @date 2020/8/25
 * @description
 */
public class RoleActivity extends AppCompatActivity {
    @BindView(R.id.iv_1)
    ImageView iv1;
    @BindView(R.id.iv_2)
    ImageView iv2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SpUtil.contains(this, Constants.KEY_ROLE)) {
            int role = (int)SpUtil.get(this, Constants.KEY_ROLE, -1);
            startMainActivity(role);
        } else {
            setContentView(R.layout.activity_role);
            ButterKnife.bind(this);
        }
    }

    @OnClick({R.id.iv_1, R.id.iv_2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_1:
                startMainActivity(1);
                break;
            case R.id.iv_2:
                startMainActivity(2);
                break;
        }
    }

    public void startMainActivity(int i) {
        SpUtil.put(this, Constants.KEY_ROLE, i);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.EXTRA_ROLE, i);
        startActivity(intent);
        finish();
    }
}
