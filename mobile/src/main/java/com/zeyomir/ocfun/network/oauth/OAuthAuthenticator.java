package com.zeyomir.ocfun.network.oauth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.eventbus.command.db.SaveAuthorizationCredentialsCommand;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import timber.log.Timber;

public class OAuthAuthenticator {

    private final Context context;
    private final OAuthProvider provider;
    private final OAuthConsumer oAuthConsumer;
    private final String callbackUrl;

    public OAuthAuthenticator(Context context, OAuthProvider provider, OAuthConsumer oAuthConsumer, String callbackUrl) {
        this.context = context;
        this.provider = provider;
        this.oAuthConsumer = oAuthConsumer;
        this.callbackUrl = callbackUrl;
    }

    public void startOAuth() {
        String authorizationUrl = null;
        try {
            authorizationUrl = provider.retrieveRequestToken(oAuthConsumer, callbackUrl);
        } catch (OAuthMessageSignerException | OAuthCommunicationException | OAuthExpectationFailedException | OAuthNotAuthorizedException e) {
            Timber.e(e, "failed to retrieve oauth authorization url");
        }
        if (authorizationUrl != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public OAuthCredentials retrieveCredentials(String verifier) {
        try {
            provider.retrieveAccessToken(oAuthConsumer, verifier);
        } catch (OAuthMessageSignerException | OAuthCommunicationException | OAuthExpectationFailedException e) {
            Timber.w(e, "failed to retrieve access token");
        } catch (OAuthNotAuthorizedException e) {
            Timber.d(e, "failed to retrieve access token");
        }
        return new OAuthCredentials(oAuthConsumer.getToken(), oAuthConsumer.getTokenSecret());
    }

    public static class OAuthCredentials {
        public final String token;
        public final String tokenSecret;

        public OAuthCredentials(String token, String tokenSecret) {
            this.token = token;
            this.tokenSecret = tokenSecret;
        }
    }
}
