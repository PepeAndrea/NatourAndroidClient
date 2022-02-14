package com.exam.natour.UI.View.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.exam.natour.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.Task;

public class AuthMainPage extends Fragment {

    private AuthViewModel authViewModel;
    private Button FacebookLogin,GoogleLogin,EmailLogin;
    private LoginButton fbLoginButton;
    private CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;

    public AuthMainPage() {
        // Required empty public constructor
    }

    public static AuthMainPage newInstance() {
        AuthMainPage fragment = new AuthMainPage();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auth_main_page, container, false);
        this.EmailLogin = view.findViewById(R.id.EmailLogin);
        this.FacebookLogin = view.findViewById(R.id.FacebookLogin);
        this.GoogleLogin = view.findViewById(R.id.GoogleLogin);


        //SETUP LOGIN FACEBOOK
        this.setFacebookLogin(view);

        //SETUP GOOGLE FACEBOOK
        this.setGoogleLogin(view);

        this.EmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginPage();
            }
        });




        return view;
    }

    private void goToLoginPage(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.AuthContainer, new Login())
                .addToBackStack("FragStack").
                commit();
    }

    private void setFacebookLogin(View view) {

        this.callbackManager = CallbackManager.Factory.create();
        this.fbLoginButton = (LoginButton) view.findViewById(R.id.fb_login_button);

        this.fbLoginButton.setReadPermissions("email");
        // If using in a fragment
        this.fbLoginButton.setFragment(this);

        this.FacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbLoginButton.performClick();
            }
        });

        // Callback registration
        this.fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String token = AccessToken.getCurrentAccessToken().getToken();
                Log.i("Accesso Facebook ruscito","Token restituito: "+token );
                LoginManager.getInstance().logOut();
                authViewModel.loginProvider(view.getContext(),"facebook",token);
            }

            @Override
            public void onCancel() {
                Log.i("Facebook","Accesso cancellato");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.i("Facebook Error",exception.getMessage());

            }
        });

    }

    private void setGoogleLogin(View view){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_server_client_id))
                .requestServerAuthCode(getString(R.string.google_server_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);

        this.GoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });



    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            authViewModel.loginProvider(getContext(),"google",account.getServerAuthCode());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i("Google login fallito", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }




}