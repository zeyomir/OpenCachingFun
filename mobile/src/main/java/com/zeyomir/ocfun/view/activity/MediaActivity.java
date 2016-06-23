package com.zeyomir.ocfun.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.configuration.OCFunApplication;
import com.zeyomir.ocfun.storage.FileManager;
import com.zeyomir.ocfun.view.component.TouchImageView;
import com.zeyomir.ocfun.view.component.TransactionAnimation;

import java.io.File;

import javax.inject.Inject;

public class MediaActivity extends Activity {

    @Inject
    FileManager fileManager;

    private static final String NAME = "name";
    private static final String CODE = "code";
    private TouchImageView touchImageView;
    private Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        touchImageView = new TouchImageView(this, null);
        touchImageView.setBackgroundColor(ContextCompat.getColor(this, R.color.window));
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        ((OCFunApplication) getApplication()).getInjector().inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(touchImageView);
        picasso = Picasso.with(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        String code = extras.getString(CODE);
        String name = extras.getString(NAME);
        File file = fileManager.getImageFile(code, name);
        picasso.load(file).into(touchImageView);
    }

    public static Intent getIntent(Context context, String code, String name) {
        Intent intent = new Intent(context, MediaActivity.class);
        intent.putExtra(CODE, code);
        intent.putExtra(NAME, name);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(TransactionAnimation.FADE.getEnter(), TransactionAnimation.FADE.getExit());
    }
}
