package com.etoitau.photocatch;

import android.view.KeyEvent;
import android.view.View;

import com.etoitau.photocatch.activities.MainActivity;

/**
 * Login when enter hit in password field
 */
public class EnterKeyListener implements View.OnKeyListener {
    MainActivity context;

    public EnterKeyListener(MainActivity context) {
        this.context = context;
    }

    // on enter key, submit form
    @Override
    public boolean onKey(View view, int i, KeyEvent event) {
        if (i == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            context.onLoginClick(view);
        }
        return false;
    }
}
