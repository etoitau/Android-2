/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.etoitau.photocatch.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.etoitau.photocatch.EnterKeyListener;
import com.etoitau.photocatch.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

/**
 * Main activity is a login/signup screen
 */
public class MainActivity extends AppCompatActivity {
  TextView switchSignin, loginMessage;
  EditText usernameET, passwordET;
  Button loginButton;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (parseSomeoneSignedIn()) {
      goToListOfOthers();
    }

    setTitle("Photo Catch - Login");

    // find views
    usernameET = findViewById(R.id.usernameEditText);
    passwordET = findViewById(R.id.passwordEditText);
    switchSignin = findViewById(R.id.switchTextView);
    loginButton = findViewById(R.id.loginButton);
    loginMessage = findViewById(R.id.signinFeedbackTextView);

    // enter after password submits
    passwordET.setOnKeyListener(new EnterKeyListener(this));

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

//  When they click login button checks they provided info,
//  checks whether logging in or creating new accound, directs to appropriate method
  public void onLoginClick(View view) {
    String username = usernameET.getText().toString();
    String password = passwordET.getText().toString();
    if (username == null || username.length() < 1) {
      loginMessage.setText("Please enter username");
      return;
    } else if (password == null || password.length() < 1) {
      loginMessage.setText("Please enter password");
      return;
    } else if (username.contains(",")) {
      loginMessage.setText("Username cannot contain comma");
      return;
    }
    // if in login mode
    if (loginButton.getText().toString().equals(getResources().getString(R.string.login))) {
      userSignin(username, password);
    } else {
      // else it must be signup mode
      userSignup(username, password);
    }
  }

  public void saveParseObject(String table, String username, String note) {
    ParseObject pObj = new ParseObject(table);
    pObj.put("username", username);
    pObj.put("note", note);

    pObj.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException ex) {
        if (ex == null) {
          Log.i("Parse Result", "Successful!");
        } else {
          Log.i("Parse Result", "Failed" + ex.toString());
        }
      }
    });
  }

  // sign up a new user with parse
  public void userSignup(final String username, final String password) {
    ParseUser user = new ParseUser();
    user.setUsername(username);
    user.setPassword(password);
    user.signUpInBackground(new SignUpCallback() {
      @Override
      public void done(ParseException e) {
        if (e == null) {
          Log.i(username, "Sign up successful");
          goToListOfOthers();
        }
        else {
          loginMessage.setText(e.getMessage());
          Log.i(username, "error signing up");
          e.printStackTrace();
        }
      }
    });
  }

  // sign in an existing user with parse
  public void userSignin(final String username, final String password) {
    ParseUser.logInInBackground(username, password, new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
        if (user != null) {
          Log.i(user.getUsername(), "signed in");
          goToListOfOthers();
        } else {
          loginMessage.setText(e.getMessage());
          Log.i(username, "error signing in");
          e.printStackTrace();
        }
      }
    });
  }

  // check user is already signed in
  public boolean parseSomeoneSignedIn() {
    ParseUser user = ParseUser.getCurrentUser();
    if (user != null) {
      Log.i(user.getUsername(), "is signed in");
      return true;
    } else {
      Log.i("no one", "is signed in");
      return false;
    }
  }

  // used by ListUsersActivity
  public static void logout() {
    ParseUser.logOutInBackground(new LogOutCallback() {
      @Override
      public void done(ParseException e) {
        if (e == null) {
          Log.i("user", "logged out");
        } else {
          Log.i("error", "logging out");
          e.printStackTrace();
        }
      }
    });
  }

  // switch modes between login or create new account
  public void onSwitchSigninMode(View view) {
    // if currently login mode
    if (loginButton.getText().toString().equals(getResources().getString(R.string.login))) {
      loginButton.setText(R.string.signup);
      switchSignin.setText(R.string.orlogin);
    } else {
      loginButton.setText(R.string.login);
      switchSignin.setText(R.string.orsignup);
    }
  }

  // if they tap somewhere non-functional, hide the keyboard
  public void onOutsideClick(View view) {
    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    try {
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    } catch (NullPointerException e) {
      // easy, cowboy
    }
  }


  // go to list of users
  public void goToListOfOthers() {
    if (!parseSomeoneSignedIn()) {
      Log.i("no one signed in", "goToListOfUsers aborted");
      return;
    }
    String username = ParseUser.getCurrentUser().getUsername();
    Intent intent = new Intent(MainActivity.this, ListUsersActivity.class);
    intent.putExtra("username", username);
    MainActivity.this.startActivity(intent);
  }
}