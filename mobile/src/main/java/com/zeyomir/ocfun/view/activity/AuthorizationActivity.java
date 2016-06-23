package com.zeyomir.ocfun.view.activity;

import android.content.Intent;
import android.net.Uri;

import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.configuration.HttpModule;
import com.zeyomir.ocfun.eventbus.command.api.FinishOAuthCommand;

import oauth.signpost.OAuth;

public class AuthorizationActivity extends BaseActivity {
    @Override
    protected void onPostInjection() {
        handleIntent();
    }

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        handleIntent();
    }

    private void handleIntent() {
        Uri uri = getIntent().getData();

        if (uri != null && uri.getScheme().equals(HttpModule.OAUTH_CALLBACK_SCHEME)) {
            String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
            bus.post(new FinishOAuthCommand(verifier));
        }
        finish();
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.a_empty;
    }

    @Override
    protected byte getMenuPosition() {
        return NOT_IN_MENU;
    }
}
