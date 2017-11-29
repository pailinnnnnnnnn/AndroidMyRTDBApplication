package com.pailin_rungruang.myrtdbapplication;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.internal.gmsg.HttpClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 590;
    private DatabaseReference mRootRef; /*= FirebaseDatabase.getInstance().getReference();*/

    private FirebaseAuth mAuth;

    // Google Sign In

    private LinearLayout Prof_Section;
    private Button SignOut;
    private SignInButton SignIn;
    private TextView Name, Email;
    private ImageView Pic;
    private GoogleSignInClient mGoogleSignInClient;

    private Button btnSaveRecord;
    private Button btnDisplayRecord;


    private void onViewBind() {

        btnSaveRecord = (Button) findViewById(R.id.btn_save);
        btnDisplayRecord = (Button) findViewById(R.id.btn_display);

        // Google Sign In
        Prof_Section = (LinearLayout) findViewById(R.id.prof_section);
        SignOut = (Button) findViewById(R.id.btn_logout_google_account);
        SignIn = (SignInButton) findViewById(R.id.sign_in_button);
        Name = (TextView) findViewById(R.id.tv_display_name);
        Email = (TextView) findViewById(R.id.tv_display_email);
        Pic = (ImageView) findViewById(R.id.iv_display_image);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onViewBind();

        // Google Sign In
        SignIn.setSize(SignIn.SIZE_STANDARD);
        SignIn.setOnClickListener(this);
        SignOut.setOnClickListener(this);
        Prof_Section.setVisibility(View.GONE);




        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();



        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        checkAlreadySigned();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true); //set Offline mode
        mRootRef = FirebaseDatabase.getInstance().getReference();


        btnSaveRecord.setOnClickListener(this);
        btnDisplayRecord.setOnClickListener(this);

//        mAuth = FirebaseAuth.getInstance();

    }

    public void sendIdToken(){
        // ID token to my server
//        HttpClient httpClient = new DefaultHttpClient();
    }

    public void saveRecord() {

        //FirebaseDatabase database = FirebaseDatabase.getInstance();


        EditText etMessage = (EditText) findViewById(R.id.et_message);
        EditText etUsername = (EditText) findViewById(R.id.et_message2);
        String stringMessage = etMessage.getText().toString();
        String stringUsername = etUsername.getText().toString();

        if (stringMessage.isEmpty() || stringUsername.isEmpty()) {
            return;
        }


        //TODO: RTDB saveRecord
//        DatabaseReference mUsernameRef = database.getReference()
//                .child("records").child("username");

        DatabaseReference mUsernameRef = mRootRef
                .child("records").child("username");

        DatabaseReference mMessageRef = mRootRef
                .child("records").child("message");

        mUsernameRef.setValue(stringUsername);
        mMessageRef.setValue(stringMessage);

    }

    public void displayRecord() {

        //TODO: RTDB fetchMessage

        final TextView tvDisplay = findViewById(R.id.tv_display);

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get data (String)
//                String value = dataSnapshot.getValue(String.class);

                String stringMessage = dataSnapshot.child("records").child("message").getValue().toString();
                String stringUsername = dataSnapshot.child("records").child("username").getValue().toString();

                tvDisplay.setText(stringMessage + "\n" + stringUsername + " ..ไม่ได้กล่าวไว้");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.btn_logout_google_account:
                signOut();
                break;

            case R.id.btn_save:
                saveRecord();
                break;
            case R.id.btn_display:
                displayRecord();
                break;

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
//         Check for existing Google Sign In account, if the user is already signed in
//         the GoogleSignInAccount will be non-null.

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            updateUI(true);
        } else {
            updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(false);
                    }
                });

//        Auth.GoogleSignInApi.signOut(mGoogleSignInClient).setResultCallback(new ResultCallback<Status>() {
//            @Override
//            public void onResult(@NonNull Status status) {
//                updateUI(false);
//            }
//        });

    }

    private void revokeAccess(){
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(false);
                    }
                });
    }

    private void checkAlreadySigned(){
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        handleSignInResult(task);
                    }
                });
    }

//    private void handleResult(GoogleSignInResult result){
//        if(result.isSuccess()){
//            GoogleSignInAccount account = result.getSignInAccount();
//            String name = account.getDisplayName();
//            String email = account.getEmail();
//            String img_url = account.getPhotoUrl().toString();
//
//            Name.setText(name);
//            Email.setText(email);
//
//            Glide.with(this).load(img_url).into(Pic);
//
//            updateUI(true);
//        }
//
//    }

    private void updateUI(boolean isLogin) {
        if (isLogin) {
            Prof_Section.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.GONE);
        } else {
            Prof_Section.setVisibility(View.GONE);
            SignIn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleResult(result);
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
                String idToken = account.getIdToken();
                String name = account.getDisplayName();
                String email = account.getEmail();
                String img_url = account.getPhotoUrl().toString();
                Name.setText(name);
                Email.setText(email);

                Glide.with(this).load(img_url).into(Pic);

                updateUI(true);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            updateUI(false);
        }

    }

}
