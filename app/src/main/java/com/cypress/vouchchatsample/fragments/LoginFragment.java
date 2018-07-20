package com.cypress.vouchchatsample.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cypress.vouchchatsample.R;
import com.cypress.vouchchatsample.Utils;
import com.cypress.vouchchatsample.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginFragment extends Fragment {

    @BindView(R.id.email_edit)
    EditText email;
    @BindView(R.id.pass_edit)
    EditText password;
    @BindView(R.id.sign_up_tv)
    TextView signUp;
    @BindView(R.id.login_btn)
    Button login;

    private FirebaseAuth auth;

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        auth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.sign_up_tv)
    public void signUp(TextView view) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, SignUpFragment.newInstance())
                .addToBackStack("")
                .commit();
    }

    @OnClick(R.id.login_btn)
    public void login(Button button) {
        if (Utils.isValidEmail(email.getText()) && password.getText().length() != 0) {
            login(email.getText().toString(), password.getText().toString());
        } else {
            Toast.makeText(getActivity(), R.string.not_valid_toast, Toast.LENGTH_LONG).show();
        }
    }

    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), R.string.sign_in_error_toast, Toast.LENGTH_LONG).show();
            }
        });
    }
}
