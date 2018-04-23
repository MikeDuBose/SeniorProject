
package com.examples.whywait.login;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainLogin extends Activity {

	private boolean isLoggedInBackendless = false;
	private CheckBox rememberLoginBox;

	
	// backendless
	private TextView registerLink, restoreLink;
	private EditText identityField, passwordField;
	private Button bkndlsLoginButton;
	
	// twitter
	private Button loginTwitterButton;
	private boolean isLoggedInTwitter = false;
	
	// google
	private SignInButton loginGooglePlusButton;
	private final int RC_SIGN_IN = 112233; // arbitrary number
	private GoogleApiClient mGoogleApiClient;
	private String gpAccessToken = null;
	private boolean isLoggedInGoogle = false;
	
	// facebook
	private LoginButton loginFacebookButton;
	private CallbackManager callbackManager;
	private String fbAccessToken = null;
	private boolean isLoggedInFacebook = false;
	Button button;

	public void mapsOnClick(View v){
		Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());

		AppEventsLogger.activateApp(this);
		setContentView(R.layout.activity_main_login);


		Backendless.initApp( this, getString(R.string.backendless_AppId), getString(R.string.backendless_ApiKey));
		Backendless.setUrl(getString(R.string.backendless_ApiHost));

		initUI();
		initUIBehaviour();


		Backendless.UserService.isValidLogin(new DefaultCallback<Boolean>(this) {
			@Override
			public void handleResponse(Boolean isValidLogin) {
				super.handleResponse(null);
				if (isValidLogin && Backendless.UserService.CurrentUser() == null) {
					String currentUserId = Backendless.UserService.loggedInUser();

					if (!currentUserId.equals("")) {
						Backendless.UserService.findById(currentUserId, new DefaultCallback<BackendlessUser>(MainLogin.this, "Logging in...") {
							@Override
							public void handleResponse(BackendlessUser currentUser) {
									super.handleResponse(currentUser);
									isLoggedInBackendless = true;
									Backendless.UserService.setCurrentUser(currentUser);
									startLoginResult(currentUser);
								}
						});
					}
				}
				super.handleResponse(isValidLogin);
			}
		});
	}

	private void initUI() {
		rememberLoginBox = (CheckBox) findViewById( R.id.rememberLoginBox );

		
		// backendless
		registerLink = (TextView) findViewById( R.id.registerLink );
		restoreLink = (TextView) findViewById( R.id.restoreLink );
		identityField = (EditText) findViewById( R.id.identityField );
		passwordField = (EditText) findViewById( R.id.passwordField );
		bkndlsLoginButton = (Button) findViewById( R.id.bkndlsLoginButton);
		
		// twitter
		loginTwitterButton = (Button) findViewById(R.id.loginTwitterButton);
		
		// google

		
		// facebook
		loginFacebookButton = (LoginButton) findViewById(R.id.button_FacebookLogin);




	}

	private void initUIBehaviour() {
		
		// backendless
		bkndlsLoginButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				onLoginWithBackendlessButtonClicked();
			}
		} );
		registerLink.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				onRegisterLinkClicked();
			}
		} );
		restoreLink.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				onRestoreLinkClicked();
			}
		} );
		
		// twitter
		loginTwitterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onLoginWithTwitterButtonClicked();
			}
		});
		
		// facebook
		callbackManager = configureFacebookSDKLogin();
		if (AccessToken.getCurrentAccessToken() != null)
		{
			isLoggedInFacebook = true;
			fbAccessToken = AccessToken.getCurrentAccessToken().getToken();
		}
		

		
	}

	private void startLoginResult(BackendlessUser user)
	{
		String msg = "ObjectId: " + user.getObjectId() + "\n"
				+ "UserId: " + user.getUserId() + "\n"
				+ "Email: " + user.getEmail() + "\n"
				+ "Properties: " + "\n";

		for (Map.Entry<String, Object> entry : user.getProperties().entrySet())
			msg += entry.getKey() + " : " + entry.getValue() + "\n";


		Intent intent = new Intent(this, MapsActivity.class);
		Intent intent2 = new Intent(this, queueRemover.class);
		intent.putExtra(LoginResult.userInfo_key, msg);
		intent.putExtra(LoginResult.logoutButtonState_key, true);
		if(user.getEmail().equals("indiagarden@indiagarden.com") || user.getEmail().equals("packstavern@packstavern.com") ||
				user.getEmail().equals("luellasbbq@luellasbbq.com") ||
				user.getEmail().equals("earygirl@earlygirl.com")){
			startActivity(intent2);
		}
		else {
			startActivity(intent);
		}

	}

	private void startLoginResult(String msg, boolean logoutButtonState)
	{
		Intent intent = new Intent(this, LoginResult.class);
		intent.putExtra(LoginResult.userInfo_key, msg);
		intent.putExtra(LoginResult.logoutButtonState_key, logoutButtonState);

		startActivity(intent);
	}

	
	private void onLoginWithBackendlessButtonClicked()
	{
		String identity = identityField.getText().toString();
		String password = passwordField.getText().toString();
		boolean rememberLogin = rememberLoginBox.isChecked();

		Backendless.UserService.login( identity, password, new DefaultCallback<BackendlessUser>( MainLogin.this )
		{
			public void handleResponse( BackendlessUser backendlessUser )
			{
				super.handleResponse( backendlessUser );
				isLoggedInBackendless = true;
				startLoginResult(backendlessUser);
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				super.handleFault(fault);
				startLoginResult(fault.toString(), false);
			}
		}, rememberLogin );
	}

	private void onRegisterLinkClicked()
	{
		startActivity( new Intent( this, RegisterActivity.class ) );
	}

	private void onRestoreLinkClicked()
	{
		startActivity( new Intent( this, RestorePasswordActivity.class ) );
	}

	private void onMapsClicked(){
		Intent intent = new Intent(this, MapsActivity.class);
		startActivity(intent);
	}
	
	// ------------------------------ twitter ------------------------------
	private void onLoginWithTwitterButtonClicked() {
		Map<String, String> twitterFieldsMapping = new HashMap<>();
		twitterFieldsMapping.put("name", "name");
		boolean rememberLogin = rememberLoginBox.isChecked();

		Backendless.UserService.loginWithTwitter(MainLogin.this, twitterFieldsMapping, new BackendlessCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser backendlessUser) {
				isLoggedInBackendless = true;
				isLoggedInTwitter = true;
				startLoginResult(backendlessUser);
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				isLoggedInBackendless = false;
				super.handleFault(fault);
				startLoginResult(fault.toString(), false);
			}
		}, rememberLogin);
	}
	// ------------------------------ end twitter ------------------------------
	
	// ------------------------------ facebook ------------------------------
	private void loginToBackendlessWithFacebook()
	{
		boolean rememberLogin = rememberLoginBox.isChecked();
		Backendless.UserService.loginWithFacebookSdk(fbAccessToken, new AsyncCallback<BackendlessUser>() {
			@Override
			public void handleResponse(BackendlessUser backendlessUser) {
				isLoggedInBackendless = true;
				startLoginResult(backendlessUser);
			}

			@Override
			public void handleFault(BackendlessFault fault) {
				isLoggedInBackendless = false;
				startLoginResult(fault.toString(), false);
			}
		}, rememberLogin);
	}

	private CallbackManager configureFacebookSDKLogin() {
		loginFacebookButton.setReadPermissions("email");
		// If using in a fragment
		//loginFacebookButton.setFragment(this);

		CallbackManager callbackManager = CallbackManager.Factory.create();

		// Callback registration
		loginFacebookButton.registerCallback(callbackManager, new FacebookCallback<com.facebook.login.LoginResult>() {
			@Override
			public void onSuccess(com.facebook.login.LoginResult loginResult) {
				isLoggedInFacebook = true;
				fbAccessToken = loginResult.getAccessToken().getToken();
				loginToBackendlessWithFacebook();
			}

			@Override
			public void onCancel() {
				// App code
				Log.i("LoginProcess", "loginFacebookButton::onCancel");
				Toast.makeText(MainLogin.this, "Facebook login process cancelled.", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onError(FacebookException exception) {
				isLoggedInFacebook = false;
				fbAccessToken = null;
				String msg = exception.getMessage() + "\nCause:\n" + (exception.getCause() != null ? exception.getCause().getMessage() : "none");
				Toast.makeText(MainLogin.this, msg, Toast.LENGTH_LONG).show();
			}
		});

		return callbackManager;
	}

	private void logoutFromFacebook()
	{
		if (!isLoggedInFacebook)
			return;

		if (AccessToken.getCurrentAccessToken() != null)
			LoginManager.getInstance().logOut();

		isLoggedInFacebook = false;
		fbAccessToken = null;
	}
	// ------------------------------ end facebook ------------------------------
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		

		
		// facebook
		callbackManager.onActivityResult(requestCode, resultCode, data);
		
	}
}
    