package com.example.signinsignupapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Displays a simple confirmation screen after successful sign in.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Creates the signed-in screen and prepares the Sign Out button.
     *
     * @param savedInstanceState previously saved Activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();

        TextView textEmail =
                findViewById(R.id.textEmail);

        if (currentUser == null) {

            openSignIn();
            return;
        }

        textEmail.setText(
                currentUser.getEmail()
        );

        findViewById(R.id.buttonSignOut)
                .setOnClickListener(view -> signOut());
    }

    /**
     * Signs the current Firebase user out.
     */
    private void signOut() {

        FirebaseAuth
                .getInstance()
                .signOut();

        openSignIn();
    }

    /**
     * Returns the user to the Sign In screen and clears the Activity history.
     */
    private void openSignIn() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        SignInActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}