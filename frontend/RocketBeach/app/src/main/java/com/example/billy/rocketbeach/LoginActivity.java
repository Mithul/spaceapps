package com.example.billy.rocketbeach;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pushbots.push.Pushbots;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private RocketBeach rocket;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("RocketBeach", 0);
        rocket = Utils.getService();
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Pushbots.sharedInstance().registerForRemoteNotifications();
        //TODO: Send the registrationId to the server after logging in


    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLogin();
    }

    private void checkLogin() {
        if (preferences.contains("X-Auth-Token")) {
            startActivity(new Intent(getApplicationContext(), TeamActivity.class));
        }
    }

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            rocket.loginUser(email, password).enqueue(new Callback<Beachgoer>() {
                @Override
                public void onResponse(Call<Beachgoer> call, Response<Beachgoer> response) {
                    if (response.code() == 200 && response.body().message == null) {
                        showProgress(false);
                        Beachgoer person = response.body();
//                        Log.e("Debug_GCM",response.body().toString());
//                        Log.e("Debug_GCM",person.auth_token);
                        Utils.makeToken(preferences, "X-Auth-Token", person.auth_token);
                        final String token = getSharedPreferences("RocketBeach", 0).getString("X-Auth-Token", "");
//                        Log.e("Debug_GCM",token);
                        String registrationId = Pushbots.sharedInstance().getGCMRegistrationId();
//                        Log.e("Debug_GCM",registrationId);



                        rocket.registerUserDevice(registrationId, token).enqueue(new Callback<Beachgoer>() {
                            @Override
                            public void onResponse(Call<Beachgoer> call, Response<Beachgoer> response) {
                                if (response.code() == 200 && response.body().message == null) {
                                    Log.e("Debug_GCM","Passed");
                                    showProgress(false);
                                    Beachgoer person = response.body();
                                    Utils.addToken(preferences, "X-Auth-Token", person.auth_token);
                                    String registrationId = Pushbots.sharedInstance().getGCMRegistrationId();
                                    startActivity(new Intent(getApplicationContext(), TeamActivity.class));
                                }
                            }

                            @Override
                            public void onFailure(Call<Beachgoer> call, Throwable t) {
                                showProgress(false);
                                mEmailView.setError(String.format(Locale.ENGLISH, "Some problem occured %s", t.getMessage() + ""));
                                mEmailView.requestFocus();
                                Toast.makeText(getBaseContext(), "Failed registering device", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<Beachgoer> call, Throwable t) {
                    showProgress(false);
                    mEmailView.setError(String.format(Locale.ENGLISH, "Some problem occured %s", t.getMessage() + ""));
                    mEmailView.requestFocus();

                }
            });

        }
    }

    private void attemptSignup() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            rocket.addUser(email, password, password).enqueue(new Callback<Beachgoer>() {
                @Override
                public void onResponse(Call<Beachgoer> call, Response<Beachgoer> response) {
                    if (response.code() == 201) {
                        Beachgoer token = response.body();
                        Log.d("TEST", token.auth_token);
                        Utils.addToken(preferences, "X-Auth-Token", token.auth_token);
                        startActivity(new Intent(getApplicationContext(), TeamActivity.class));
                    } else {
                        mEmailView.setError("Some problem occured");
                        mEmailView.requestFocus();
                    }
                    showProgress(false);
                }

                @Override
                public void onFailure(Call<Beachgoer> call, Throwable t) {
                    showProgress(false);
                    mEmailView.setError(String.format(Locale.ENGLISH, "Some problem occured %s", t.getMessage() + ""));
                    mEmailView.requestFocus();
                }
            });
        }
    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}

