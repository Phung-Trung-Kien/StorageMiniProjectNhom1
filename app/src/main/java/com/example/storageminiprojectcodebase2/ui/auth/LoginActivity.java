package com.example.storageminiprojectcodebase2.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storageminiprojectcodebase2.R;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.repository.UserRepository;
import com.example.storageminiprojectcodebase2.utils.SessionManager;
import com.example.storageminiprojectcodebase2.data.entity.User;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView errorTextView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        errorTextView = findViewById(R.id.error_text_view);

        loginButton.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorTextView.setText("Vui lòng nhập tên đăng nhập và mật khẩu");
            return;
        }

        AppDatabase.databaseExecutor.execute(() -> {
            UserRepository repository = new UserRepository(getApplication());
            User user = repository.login(username, password);
            runOnUiThread(() -> {
                if (user != null) {
                    sessionManager.createSession(user.id, user.username);
                    finish();
                } else {
                    errorTextView.setText("Sai tên đăng nhập hoặc mật khẩu");
                }
            });
        });
    }
}
