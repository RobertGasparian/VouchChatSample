package me.vouch4.app.activities;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import me.vouch4.app.fragments.LoadingDialogFragment;


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

    public void showPickerDialog(DialogInterface.OnClickListener cameraListener, DialogInterface.OnClickListener galleyListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);

        builder.setMessage(me.vouch4.app.R.string.picker_message);
        builder.setPositiveButton(me.vouch4.app.R.string.camera_option, cameraListener);
        builder.setNegativeButton(me.vouch4.app.R.string.gallery_option, galleyListener);
        builder.setNeutralButton(me.vouch4.app.R.string.cancel_option, (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }
}
