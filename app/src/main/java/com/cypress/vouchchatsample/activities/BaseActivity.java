package com.cypress.vouchchatsample.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.cypress.vouchchatsample.fragments.LoadingDialogFragment;


public class BaseActivity extends AppCompatActivity {

    private int pendingLoadingCount = 0;

    private LoadingDialogFragment loadingDialog;

    public void showLoadingDialog() {
        if (pendingLoadingCount == 0) {
            dismissLoadingDialog();
            loadingDialog = LoadingDialogFragment.newInstance();
            FragmentManager manager = getSupportFragmentManager();
            loadingDialog.show(manager, LoadingDialogFragment.LOADING_TAG);
        }
        pendingLoadingCount++;

    }

    public void dismissLoadingDialog() {

        if (getSupportFragmentManager().findFragmentByTag(LoadingDialogFragment.LOADING_TAG) != null) {
            if (pendingLoadingCount > 0) {
                pendingLoadingCount--;
            }
            if (pendingLoadingCount == 0) {
                loadingDialog.dismiss();
            }
        }

    }
}
