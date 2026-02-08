package com.example.campus_space_scheduler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleClient =
                GoogleSignIn.getClient(this, gso);


        EditText email = findViewById(R.id.etUser);
        EditText pass = findViewById(R.id.etPass);
        Button login = findViewById(R.id.btnLogin);

        login.setOnClickListener(v -> {
            auth.signInWithEmailAndPassword(
                    email.getText().toString(),
                    pass.getText().toString()
            ).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
        Button btnGoogle = findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(v ->
                startActivityForResult(
                        googleClient.getSignInIntent(), 100));

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount acct = task.getResult(ApiException.class);
                AuthCredential cred =
                        GoogleAuthProvider.getCredential(acct.getIdToken(), null);

                FirebaseAuth.getInstance()
                        .signInWithCredential(cred)
                        .addOnSuccessListener(r -> {
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        });
            } catch (Exception e) {
                Toast.makeText(this, "Google login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
