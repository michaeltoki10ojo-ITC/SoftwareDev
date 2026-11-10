package com.example.signinsignupapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Handles existing user authentication.
 */
public class SignInActivity extends AppCompatActivity {

    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;

    private MaterialButton buttonSignIn;
    private ProgressBar progressSignIn;

    private FirebaseAuth firebaseAuth;

    /**
     * Creates the Sign In screen and initializes Firebase Authentication.
     *
     * @param savedInstanceState previously saved Activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        buttonSignIn = findViewById(R.id.buttonSignIn);
        progressSignIn = findViewById(R.id.progressSignIn);

        String registeredEmail =
                getIntent().getStringExtra("registered_email");

        if (registeredEmail != null) {
            inputEmail.setText(registeredEmail);
        }

        buttonSignIn.setOnClickListener(
                view -> signIn()
        );

        findViewById(R.id.textCreateAccount)
                .setOnClickListener(
                        view -> openSignUp()
                );
    }

    /**
     * Opens the Sign Up screen.
     */
    private void openSignUp() {

        startActivity(
                new Intent(
                        SignInActivity.this,
                        SignUpActivity.class
                )
        );
    }

    /**
     * Validates the entered credentials and signs the user in with Firebase.
     */
    private void signIn() {

        String email = getText(inputEmail);
        String password = getText(inputPassword);

        if (email.isEmpty() || password.isEmpty()) {

            showToast(R.string.fill_all_fields);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            showToast(R.string.invalid_email);
            return;
        }

        setLoading(true);

        firebaseAuth
                .signInWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(task -> {

                    setLoading(false);

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                SignInActivity.this,
                                R.string.sign_in_successful,
                                Toast.LENGTH_LONG
                        ).show();

                        Intent intent =
                                new Intent(
                                        SignInActivity.this,
                                        MainActivity.class
                                );

                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);

                    } else {

                        showToast(
                                R.string.sign_in_failed
                        );
                    }
                });
    }

    /**
     * Displays or hides loading feedback while sign in is processing.
     *
     * @param loading true when Firebase is processing
     */
    private void setLoading(boolean loading) {

        buttonSignIn.setEnabled(!loading);

        progressSignIn.setVisibility(
                loading
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    /**
     * Reads and trims text from an input field.
     *
     * @param input input field
     * @return trimmed text
     */
    private String getText(
            TextInputEditText input) {

        if (input.getText() == null) {
            return "";
        }

        return input
                .getText()
                .toString()
                .trim();
    }

    /**
     * Displays a Toast message.
     *
     * @param messageId string resource for the message
     */
    private void showToast(int messageId) {

        Toast.makeText(
                this,
                messageId,
                Toast.LENGTH_SHORT
        ).show();
    }
}