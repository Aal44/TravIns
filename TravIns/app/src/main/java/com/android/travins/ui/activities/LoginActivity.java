package com.android.travins.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.travins.R;
import com.android.travins.databinding.ActivityLoginBinding;
import com.android.travins.databinding.ActivitySecondBoardingBinding;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
//import com.facebook.login.LoginManager;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private static final int RC_SIGN_IN = 123; // Any integer constant for the request code
    private GoogleSignInClient mGoogleSignInClient;
    private BeginSignInRequest signInRequest;
    private FirebaseAuth auth;
    private boolean passwordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //google
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

        binding.moveToRegister.setOnClickListener(v -> {
            Intent mainIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(mainIntent);
        });

        binding.btnLoginGoogle.setOnClickListener(v -> {
            signIn();
        });

        binding.btnLogin.setOnClickListener(v -> {
            if (binding.edtTextEmail.getText().toString().isEmpty() || binding.edtTextPassword.getText().toString().isEmpty()){
                Toast.makeText(LoginActivity.this,"Email or password is required!",Toast.LENGTH_SHORT).show();
            }else{
                signInWithEmailPassword();
            }
        });

        binding.edtTextPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if touch is within the bounds of the drawable
                if (event.getRawX() >= (binding.edtTextPassword.getRight() - binding.edtTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                    // Toggle password visibility
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    private void signInWithEmailPassword(){
        String email = binding.edtTextEmail.getText().toString();
        String password = binding.edtTextPassword.getText().toString();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                    } else {
                        Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
                else {
                    Toast.makeText(LoginActivity.this,"Login Error",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void togglePasswordVisibility() {
        if (passwordVisible) {
            // Hide password
            Drawable eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye_close);
            binding.edtTextPassword.setTransformationMethod(new PasswordTransformationMethod());
            binding.edtTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,eyeIcon,null);
        } else {
            // Show password
            Drawable eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye);
            binding.edtTextPassword.setTransformationMethod(null);
            binding.edtTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,eyeIcon,null);
        }
        passwordVisible = !passwordVisible;

        // Move cursor to the end of the text
        binding.edtTextPassword.setSelection(binding.edtTextPassword.getText().length());
    }

}