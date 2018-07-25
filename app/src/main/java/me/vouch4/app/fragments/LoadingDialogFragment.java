package me.vouch4.app.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class LoadingDialogFragment extends DialogFragment {

    public static final String LOADING_TAG = "loading";

    public static LoadingDialogFragment newInstance() {

        Bundle args = new Bundle();

        LoadingDialogFragment fragment = new LoadingDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(me.vouch4.app.R.layout.dialog_fragment_loading, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if (getActivity().getSupportFragmentManager().findFragmentByTag(LOADING_TAG) != null) {
                    ((LoadingDialogFragment) getActivity().getSupportFragmentManager().findFragmentByTag(LOADING_TAG)).dismiss();
                    getActivity().onBackPressed();
                    return;
                }
                super.onBackPressed();
            }
        };
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Changing dark background and removing dialog borders

        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.5f;
            windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(windowParams);
        }
    }
}
