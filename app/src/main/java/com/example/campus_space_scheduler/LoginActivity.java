package com.example.campus_space_scheduler;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.campus_space_scheduler.databinding.AActivityLoginBinding;
import com.example.campus_space_scheduler.helper.LogHelper;
import com.example.hod.utils.NotificationService;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private AActivityLoginBinding binding;
    private FirebaseAuth auth;
    private GoogleSignInClient googleClient;

    private static final int PERM_NOTIF = 101;

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleGoogleSignInResult(task);
                    } else {
                        binding.btnGoogle.setEnabled(true);
                        toast("Google login cancelled");
                    }
                } else {
                    binding.btnGoogle.setEnabled(true);
                    toast("Google login failed or cancelled");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        // Check for notification permission early
        checkNotificationPermission();

        GoogleSignInOptions options =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        googleClient = GoogleSignIn.getClient(this, options);

        binding.btnLogin.setOnClickListener(v -> {
            v.setEnabled(false);
            emailLogin();
        });
        binding.btnGoogle.setOnClickListener(v -> {
            v.setEnabled(false);
            googleLogin();
        });
        binding.tvForgotPass.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        binding.tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERM_NOTIF);
            }
        }
    }

    private void emailLogin() {
        String email = binding.etUser.getText().toString().trim();
        String password = binding.etPass.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            toast("Fields required");
            binding.btnLogin.setEnabled(true);
            return;
        }

        auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user != null) {
                        verifyUser(user);
                    } else {
                        binding.btnLogin.setEnabled(true);
                        toast("Login failed (no user)");
                    }
                })
                .addOnFailureListener(e -> {
                    binding.btnLogin.setEnabled(true);
                    toast("Login failed: " + e.getMessage());
                });
    }

    private void googleLogin(){
        googleClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null && account.getIdToken() != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            } else {
                binding.btnGoogle.setEnabled(true);
                toast("Google token error");
            }
        } catch (ApiException e) {
            binding.btnGoogle.setEnabled(true);
            toast("Google sign in failed: " + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user != null) {
                        verifyUser(user);
                    } else {
                        binding.btnGoogle.setEnabled(true);
                        toast("Google auth failed");
                    }
                })
                .addOnFailureListener(e -> {
                    binding.btnGoogle.setEnabled(true);
                    toast("Google auth failed: " + e.getMessage());
                });
    }

    private void verifyUser(FirebaseUser user){
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (isFinishing() || isDestroyed()) return;

                    if(snapshot.exists()){
                        LogHelper.log("LOGIN", user.getEmail() + " logged in");
                        
                        try {
                            Intent serviceIntent = new Intent(this, NotificationService.class);
                            startService(serviceIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        startActivity(new Intent(this, MainActivity.class));
                        finish();

                    }else{
                        user.delete().addOnCompleteListener(t -> {
                            auth.signOut();
                            binding.btnLogin.setEnabled(true);
                            binding.btnGoogle.setEnabled(true);
                            toast("User not authorized");
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    binding.btnLogin.setEnabled(true);
                    binding.btnGoogle.setEnabled(true);
                    toast("Database error: " + e.getMessage());
                });
    }

    private void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
