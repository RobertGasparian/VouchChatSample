package me.vouch4.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.vouch4.app.Utils;
import me.vouch4.app.activities.BaseActivity;
import me.vouch4.app.activities.MainActivity;

public class SignUpFragment extends Fragment {

    @BindView(me.vouch4.app.R.id.email_edit)
    EditText email;
    @BindView(me.vouch4.app.R.id.password_edit)
    EditText password;
    @BindView(me.vouch4.app.R.id.password_confirm_edit)
    EditText passwordConfirm;
    @BindView(me.vouch4.app.R.id.display_name_edit)
    EditText displayName;

    private FirebaseAuth auth;
    private final String TAG = this.getClass().getSimpleName();

    public static SignUpFragment newInstance() {

        Bundle args = new Bundle();

        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(me.vouch4.app.R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        auth = FirebaseAuth.getInstance();
    }

    @OnClick(me.vouch4.app.R.id.sign_up_btn)
    public void signUp(Button button) {
        if (Utils.isValidEmail(email.getText())
                && !displayName.getText().toString().trim().isEmpty()
                && !password.getText().toString().trim().isEmpty()
                && !passwordConfirm.getText().toString().trim().isEmpty()
                && passwordConfirm.getText().toString().equals(password.getText().toString())) {
            createAccount(email.getText().toString(), password.getText().toString());
        } else {
            Toast.makeText(getActivity(), me.vouch4.app.R.string.not_valid_toast, Toast.LENGTH_LONG).show();
        }
    }

    private void createAccount(String email, String password) {
        ((BaseActivity) getActivity()).showLoadingDialog();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                addDisplayName();
            } else {
                ((BaseActivity) getActivity()).dismissLoadingDialog();
                Toast.makeText(getActivity(), me.vouch4.app.R.string.sign_up_error_toast, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addDisplayName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayName.getText().toString()).build();
        user.updateProfile(profileUpdates).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "addDisplayName: success");
            ((BaseActivity) getActivity()).dismissLoadingDialog();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }).addOnFailureListener(e -> {
            ((BaseActivity) getActivity()).dismissLoadingDialog();
            Toast.makeText(getActivity(), getString(me.vouch4.app.R.string.display_name_add_error), Toast.LENGTH_LONG).show();
            Log.d(TAG, "addDisplayName: failure: " + e.getMessage());
        });
    }
}
