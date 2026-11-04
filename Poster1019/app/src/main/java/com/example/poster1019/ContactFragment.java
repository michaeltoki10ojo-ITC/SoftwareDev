package com.example.poster1019;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ContactFragment extends Fragment {

    /**
     * Creates the Contact Us page.
     */
    public ContactFragment(){
        super(R.layout.fragment_contact);
    }

    /**
     * Sets up the email section and personal introduction button.
     *
     * @param view current Contact Us page
     * @param savedInstanceState saved page information
     */
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        TextView textCompanyIntro =
                view.findViewById(R.id.textCompanyIntro);

        Button buttonAboutMe =
                view.findViewById(R.id.buttonAboutMe);

        buttonAboutMe.setOnClickListener(
                buttonView -> {

                    textCompanyIntro.setText(
                            R.string.personal_intro
                    );
                });

        view.findViewById(R.id.layoutEmail)
                .setOnClickListener(
                        emailView -> {

                            openEmailApp(
                                    getString(
                                            R.string.contact_email
                                    )
                            );
                        });
    }

    /**
     * Opens the user's email app.
     *
     * @param emailAddress email address to open
     */
    private void openEmailApp(String emailAddress){

        Intent intent =
                new Intent(
                        Intent.ACTION_SENDTO,
                        Uri.parse(
                                "mailto:" + emailAddress
                        )
                );

        try {

            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            Toast.makeText(
                    requireContext(),
                    R.string.no_email_app,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}