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
import android.widget.Toast;

import com.cypress.vouchchatsample.R;
import com.cypress.vouchchatsample.Utils;
import com.cypress.vouchchatsample.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpFragment extends Fragment {

    @BindView(R.id.email_edit)
    EditText email;
    @BindView(R.id.password_edit)
    EditText password;
    @BindView(R.id.password_confirm_edit)
    EditText passwordConfirm;

    private FirebaseAuth auth;

    public static SignUpFragment newInstance() {

        Bundle args = new Bundle();

        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        auth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.sign_up_btn)
    public void signUp(Button button) {
        if (Utils.isValidEmail(email.getText())
                && password.getText().length() != 0
                && passwordConfirm.getText().length() != 0
                && passwordConfirm.getText().toString().equals(password.getText().toString())) {
            createAccount(email.getText().toString(), password.getText().toString());
        }else{
            Toast.makeText(getActivity(), R.string.not_valid_toast, Toast.LENGTH_LONG).show();
        }
    }

    private void createAccount(String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            if(task.isSuccessful()){
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }else {
                Toast.makeText(getActivity(), R.string.sign_up_error_toast, Toast.LENGTH_LONG).show();
            }
        });
    }
}
