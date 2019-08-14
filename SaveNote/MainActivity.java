package com.etoitau.savenote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * App where you can write a note and save, delete, reset
 */
public class MainActivity extends AppCompatActivity {
    private AlertDialog saveAlert, clearAlert, deleteAlert;
    private EditText editText;
    private SharedPreferences sharedPreferences;

    // initialize app
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);

        sharedPreferences = getSharedPreferences("com.etoitau.savenote", MODE_PRIVATE);
        loadSaved();

        buildAlerts();
    }

    // inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // when menu item is clicked on
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean status = super.onOptionsItemSelected(item);
        // show appropriate confirmation
        switch (item.getItemId()) {
            case (R.id.saveItem):
                saveAlert.show();
                break;
            case (R.id.clearItem):
                clearAlert.show();
                break;
            case (R.id.deleteItem):
                deleteAlert.show();
                break;
            default:
                showToast("Invalid selection");
                status = false;
                break;
        }
        return status;
    }

    // set up menu confirmations
    private void buildAlerts() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        // set up alert for save menu item
        adb.setIcon(R.drawable.ic_launcher_foreground)
                .setTitle("Do you want to save?")
                .setMessage("Any previous saves will be lost")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("alert diaglog selection", "yes");
                        saveText();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("alert dialog selection", "no");
                    }
                });
        saveAlert = adb.create();

        // set up alert for clear menu item
        adb = new AlertDialog.Builder(this);
        adb.setIcon(R.drawable.ic_launcher_foreground)
                .setTitle("Do you want to clear note?")
                .setMessage("Clear current text. Save not affected")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("alert diaglog selection", "yes");
                        editText.setText("");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("alert dialog selection", "no");
                    }
                });
        clearAlert = adb.create();

        // set up alert for delete menu item
        adb = new AlertDialog.Builder(this);
        adb.setIcon(R.drawable.ic_launcher_foreground)
                .setTitle("Do you want to delete?")
                .setMessage("Save file and current text will be cleared")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("alert diaglog selection", "yes");
                        deleteText();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("alert dialog selection", "no");
                    }
                });
        deleteAlert = adb.create();
    }

    /**
     * save current text to shared preferences memory
     */
    private void saveText() {
        boolean worked = true;
        String noteToSave = editText.getText().toString();
        if (noteToSave != null && noteToSave.length() > 0) {
            sharedPreferences.edit().putString("savedNote", noteToSave).apply();
            showToast("Note saved");
        }
    }

    /**
     * replace current text with save version from preferences memory
     */
    private void loadSaved() {
        String savedNote = sharedPreferences.getString("savedNote", null);
        if (savedNote != null) {
            editText.setText(savedNote);
            showToast("Saved note loaded");
        }
    }

    /**
     * clear text field and save in memory
     */
    private void deleteText() {
        editText.setText("");
        sharedPreferences.edit().putString("savedNote", "").apply();
        showToast("Save cleared");
    }

    /**
     * makes and shows a toast with provided message
     * @param message
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
