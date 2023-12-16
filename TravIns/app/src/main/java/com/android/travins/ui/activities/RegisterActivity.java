package com.android.travins.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.android.travins.R;
import com.android.travins.databinding.ActivityLoginBinding;
import com.android.travins.databinding.ActivityRegisterBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private static final int RC_SIGN_IN = 123; // Any integer constant for the request code
    private GoogleSignInClient mGoogleSignInClient;
    private BeginSignInRequest signInRequest;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private boolean passwordVisibleC = false;
    private boolean passwordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(signInRequest.getGoogleIdTokenRequestOptions().getServerClientId())
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.editTextPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if touch is within the bounds of the drawable
                if (event.getRawX() >= (binding.editTextPassword.getRight() - binding.editTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                    // Toggle password visibility
                    passwordVisible = togglePasswordVisibility(binding.editTextPassword,passwordVisible);
                    return true;
                }
            }
            return false;
        });

        binding.editTextCPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if touch is within the bounds of the drawable
                if (event.getRawX() >= (binding.editTextCPassword.getRight() - binding.editTextCPassword.getCompoundDrawables()[2].getBounds().width())) {
                    // Toggle password visibility
                    passwordVisibleC = togglePasswordVisibility(binding.editTextCPassword,passwordVisibleC);
                    return true;
                }
            }
            return false;
        });

        binding.moveToLogin.setOnClickListener(v -> {
            Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(mainIntent);
        });

        binding.btnRegisterGoogle.setOnClickListener(v -> {
            signIn();
        });
        
        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString();
            String fullName = binding.editTextFullName.getText().toString();
            String password = binding.editTextPassword.getText().toString();
            String cPassword = binding.editTextCPassword.getText().toString();
            
            if (email.isEmpty() || fullName.isEmpty() || password.isEmpty() || cPassword.isEmpty()){
                Toast.makeText(RegisterActivity.this,"Name, email or password is required!",Toast.LENGTH_SHORT).show();
            }else{
                if (cPassword.matches(password)){
                    registerEmailPassword(email,fullName,password,cPassword);
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Password not match!",Toast.LENGTH_SHORT).show();
                }
                
            }
        });
    }

    private void registerEmailPassword(String email, String fullName, String password, String cPassword) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        addToFirebase(fullName,email,password);
                    } else {
                        // If registration fails, display a message to the user.
                        Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToFirebase(String name, String email, String password){
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", name);
        user.put("email", email);
        user.put("password", password);
        user.put("bod","");
        user.put("username","");
        user.put("gender","");
        user.put("country","");
        user.put("address","");

        db.collection("users").document(auth.getCurrentUser().getUid()).set(user)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(RegisterActivity.this, "Registration successful, please login.", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Error adding document", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(account);

        } catch (ApiException e) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusMessage());
            Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
                else {
                    Toast.makeText(RegisterActivity.this,"Login Error",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private Boolean togglePasswordVisibility(EditText editText,Boolean pass) {
        if (pass) {
            // Hide password
            Drawable eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye_close);
            editText.setTransformationMethod(new PasswordTransformationMethod());
            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,eyeIcon,null);
        } else {
            // Show password
            Drawable eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye);
            editText.setTransformationMethod(null);
            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,eyeIcon,null);
        }
        pass = !pass;

        // Move cursor to the end of the text
        editText.setSelection(editText.getText().length());

        return pass;
    }
}