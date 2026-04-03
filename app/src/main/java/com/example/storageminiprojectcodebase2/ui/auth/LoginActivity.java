package com.example.storageminiprojectcodebase2.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.entity.User;
import com.example.storageminiprojectcodebase2.repository.UserRepository;
import com.example.storageminiprojectcodebase2.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvError;
    private MaterialButton btnLogin;
    private SessionManager sessionManager;
    private UserRepository userRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        userRepository = new UserRepository(getApplication());

        if (sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvError = findViewById(R.id.tv_error);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(view -> attemptLogin());
        etPassword.setOnEditorActionListener(this::onPasswordEditorAction);
    }

    private boolean onPasswordEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            attemptLogin();
            return true;
        }
        return false;
    }

    private void attemptLogin() {
        String username = getValue(etUsername.getText());
        String password = getValue(etPassword.getText());

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showError(getString(R.string.login_error_empty_fields));
            return;
        }

        showLoading(true);
        hideError();

        AppDatabase.databaseExecutor.execute(() -> {
            try {
                User user = userRepository.login(username, password);
                runOnUiThread(() -> handleLoginResult(user));
            } catch (Exception exception) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError(getString(R.string.login_error_generic));
                });
            }
        });
    }

    private void handleLoginResult(@Nullable User user) {
        showLoading(false);
        if (user != null) {
            sessionManager.createSession(user.id, user.username);
            finish();
            return;
        }
        showError(getString(R.string.login_error_invalid_credentials));
    }

    private void showLoading(boolean isLoading) {
        btnLogin.setEnabled(!isLoading);
        btnLogin.setText(isLoading ? getString(R.string.logging_in) : getString(R.string.login));
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setText(null);
        tvError.setVisibility(View.GONE);
    }

    private String getValue(@Nullable CharSequence value) {
        return value == null ? "" : value.toString().trim();
    }
}