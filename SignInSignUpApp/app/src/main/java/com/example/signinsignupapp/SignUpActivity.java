package com.example.signinsignupapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles registration, profile photo selection, validation,
 * Firebase Authentication, and profile storage.
 */
public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText inputFirstName;
    private TextInputEditText inputLastName;
    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;
    private TextInputEditText inputConfirmPassword;

    private ImageView imageProfile;

    private MaterialButton buttonChoosePhoto;
    private MaterialButton buttonCreateAccount;

    private ProgressBar progressCreateAccount;

    private Bitmap selectedImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;

    private ActivityResultLauncher<String> imagePickerLauncher;

    /**
     * Creates the registration screen and initializes Firebase services.
     *
     * @param savedInstanceState previously saved Activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        inputFirstName =
                findViewById(R.id.inputFirstName);

        inputLastName =
                findViewById(R.id.inputLastName);

        inputEmail =
                findViewById(R.id.inputEmail);

        inputPassword =
                findViewById(R.id.inputPassword);

        inputConfirmPassword =
                findViewById(R.id.inputConfirmPassword);

        imageProfile =
                findViewById(R.id.imageProfile);

        buttonChoosePhoto =
                findViewById(R.id.buttonChoosePhoto);

        buttonCreateAccount =
                findViewById(R.id.buttonCreateAccount);

        progressCreateAccount =
                findViewById(R.id.progressCreateAccount);

        imagePickerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        this::loadImage
                );

        buttonChoosePhoto.setOnClickListener(
                view -> choosePhoto()
        );

        imageProfile.setOnClickListener(
                view -> choosePhoto()
        );

        buttonCreateAccount.setOnClickListener(
                view -> createAccount()
        );

        findViewById(R.id.textBackToSignIn)
                .setOnClickListener(
                        view -> finish()
                );
    }

    /**
     * Opens the Android image picker.
     */
    private void choosePhoto() {

        imagePickerLauncher.launch("image/*");
    }

    /**
     * Loads the selected image and displays it in the profile preview.
     *
     * @param uri selected image URI
     */
    private void loadImage(Uri uri) {

        if (uri == null) {
            return;
        }

        try (
                InputStream inputStream =
                        getContentResolver()
                                .openInputStream(uri)
        ) {

            selectedImage =
                    BitmapFactory.decodeStream(
                            inputStream
                    );

            if (selectedImage != null) {

                imageProfile.setPadding(
                        0,
                        0,
                        0,
                        0
                );

                imageProfile.setImageBitmap(
                        selectedImage
                );
            }

        } catch (IOException exception) {

            Toast.makeText(
                    this,
                    "Unable to load image.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Validates the registration form and creates a Firebase Authentication account.
     */
    private void createAccount() {

        String firstName =
                getText(inputFirstName);

        String lastName =
                getText(inputLastName);

        String email =
                getText(inputEmail);

        String password =
                getText(inputPassword);

        String confirmPassword =
                getText(inputConfirmPassword);

        if (!validateForm(
                firstName,
                lastName,
                email,
                password,
                confirmPassword)) {

            return;
        }

        setLoading(true);

        firebaseAuth
                .createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        setLoading(false);

                        showToast(
                                R.string.account_failed
                        );

                        return;
                    }

                    FirebaseUser user =
                            firebaseAuth
                                    .getCurrentUser();

                    if (user == null) {

                        setLoading(false);

                        showToast(
                                R.string.account_failed
                        );

                        return;
                    }

                    saveProfile(
                            user.getUid(),
                            firstName,
                            lastName,
                            email
                    );
                });
    }

    /**
     * Validates every required registration field.
     *
     * @param firstName first name
     * @param lastName last name
     * @param email email address
     * @param password password
     * @param confirmPassword confirmation password
     * @return true when all inputs are valid
     */
    private boolean validateForm(
            String firstName,
            String lastName,
            String email,
            String password,
            String confirmPassword) {

        if (firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            showToast(
                    R.string.fill_all_fields
            );

            return false;
        }

        if (selectedImage == null) {

            showToast(
                    R.string.select_photo
            );

            return false;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            showToast(
                    R.string.invalid_email
            );

            return false;
        }

        if (password.length() < 6) {

            showToast(
                    R.string.password_short
            );

            return false;
        }

        if (!password.equals(confirmPassword)) {

            showToast(
                    R.string.password_mismatch
            );

            return false;
        }

        return true;
    }

    /**
     * Encodes the profile image on a background thread,
     * then saves the user's profile data to Firestore.
     *
     * @param userId Firebase user ID
     * @param firstName first name
     * @param lastName last name
     * @param email email address
     */
    private void saveProfile(
            String userId,
            String firstName,
            String lastName,
            String email) {

        new Thread(() -> {

            String encodedImage =
                    encodeImage(selectedImage);

            runOnUiThread(() -> {

                Map<String, Object> profile =
                        new HashMap<>();

                profile.put(
                        "userId",
                        userId
                );

                profile.put(
                        "firstName",
                        firstName
                );

                profile.put(
                        "lastName",
                        lastName
                );

                profile.put(
                        "email",
                        email
                );

                profile.put(
                        "profileImage",
                        encodedImage
                );

                profile.put(
                        "createdAt",
                        FieldValue.serverTimestamp()
                );

                database
                        .collection("users")
                        .document(userId)
                        .set(profile)
                        .addOnSuccessListener(unused -> {

                            setLoading(false);

                            firebaseAuth.signOut();

                            Toast.makeText(
                                    SignUpActivity.this,
                                    R.string.account_created,
                                    Toast.LENGTH_LONG
                            ).show();

                            Intent intent =
                                    new Intent(
                                            SignUpActivity.this,
                                            SignInActivity.class
                                    );

                            intent.putExtra(
                                    "registered_email",
                                    email
                            );

                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);
                        })
                        .addOnFailureListener(error -> {

                            setLoading(false);

                            showToast(
                                    R.string.account_failed
                            );
                        });
            });

        }).start();
    }

    /**
     * Resizes and converts the selected profile image to a Base64 JPEG string.
     *
     * @param bitmap original selected image
     * @return Base64 encoded JPEG image
     */
    private String encodeImage(Bitmap bitmap) {

        int width = 200;

        int height =
                bitmap.getHeight()
                        * width
                        / bitmap.getWidth();

        Bitmap resized =
                Bitmap.createScaledBitmap(
                        bitmap,
                        width,
                        height,
                        true
                );

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        resized.compress(
                Bitmap.CompressFormat.JPEG,
                65,
                outputStream
        );

        return Base64.encodeToString(
                outputStream.toByteArray(),
                Base64.NO_WRAP
        );
    }

    /**
     * Displays or hides the registration loading state.
     *
     * @param loading true while Firebase is processing
     */
    private void setLoading(boolean loading) {

        buttonCreateAccount.setEnabled(
                !loading
        );

        buttonChoosePhoto.setEnabled(
                !loading
        );

        progressCreateAccount.setVisibility(
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
     * @param messageId string resource identifier
     */
    private void showToast(int messageId) {

        Toast.makeText(
                this,
                messageId,
                Toast.LENGTH_SHORT
        ).show();
    }
}