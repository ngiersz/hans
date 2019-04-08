package com.hans;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.domain.User;

import static android.support.constraint.Constraints.TAG;

public class SignInGoogleActivity extends AppCompatActivity
{
    private final int RC_SIGN_IN = 1;
    private final int RC_COMPLETE_ACCOUNT_DATA = 2;


    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_with_google);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("koy", "signIn");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("koy", "onActivityResult");

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else if (requestCode == RC_COMPLETE_ACCOUNT_DATA)
        {
            String userJSON = data.getStringExtra("userJSON");
            // firebaseUser ready to use
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            User user = User.createFromJSON(userJSON);
            Log.d("koy","mail of new user" +firebaseUser.getEmail());
            user.setGoogleEmail(firebaseUser.getEmail());
            Log.d("koy","id of new user" +firebaseUser.getUid());
            user.setGoogleId(firebaseUser.getUid());
            DatabaseFirebase db = new DatabaseFirebase();
            db.insertUserToDatabase(user);

            Intent output = new Intent();
            output.putExtra("firebaseUser", firebaseUser);
            output.putExtra("userJSON", user.toJSON());
            setResult(RESULT_OK, output);
            finish();
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {        Log.d("koy", "handler");

        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated email.
            Log.w("koy","zalogowanie na google" + account.getEmail());
            firebaseAuthWithGoogle(account);

        } catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("koy", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.d("koy", "firebaseAuth");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            DatabaseFirebase db = new DatabaseFirebase();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("koy", "signInWithCredential:success");
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            db.getUser(firebaseUser.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().isEmpty()){
                                            Intent intent = new Intent(getBaseContext(), CompleteAccountDataActivity.class);
                                            startActivityForResult(intent, RC_COMPLETE_ACCOUNT_DATA);
                                        }
                                        else{
                                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                            startActivity(intent);

                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });



                            // activity for completing user info


                        } else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w("koy", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.layout.activity_sign_in_with_google), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
