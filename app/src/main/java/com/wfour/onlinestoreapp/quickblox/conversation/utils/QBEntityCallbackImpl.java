package com.wfour.onlinestoreapp.quickblox.conversation.utils;

import android.os.Bundle;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

/**
 * Created by Suusoft on 15.06.16.
 */
public class QBEntityCallbackImpl <T> implements QBEntityCallback<T> {

    @Override
    public void onSuccess(T result, Bundle params) {

    }

    @Override
    public void onError(QBResponseException responseException) {

    }
}
