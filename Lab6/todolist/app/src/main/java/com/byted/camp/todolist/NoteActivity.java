package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoContract.TodoNote;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.SpDemoActivity;

import java.io.File;
import java.util.Calendar;


public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private RadioGroup radioGroup;
    private AppCompatRadioButton Radio;
    private boolean autoSave;
    private String NoteSaved = "note_saved";
    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);
        autoSave = true;
        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        String key = TodoNote.COLUMN_NAME_CONTENT;
        SharedPreferences sp = NoteActivity.this.getSharedPreferences(NoteSaved, MODE_PRIVATE);
        String value = sp.getString(key, "");
        editText.setText(value);

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }
        radioGroup = findViewById(R.id.radio_group);
        key = TodoNote.COLUMN_NAME_STATE;
        int valueInt = sp.getInt(key,0);
        switch (valueInt) {
            case 0:
                Radio = findViewById(R.id.btn_low);
                break;
            case 1:
                Radio = findViewById(R.id.btn_medium);
                break;
            case 2:
                Radio = findViewById(R.id.btn_high);
                break;
            default:
                Radio = findViewById(R.id.btn_low);
        }
        Radio.setChecked(true);
        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSave = false;
                SharedPreferences sp = NoteActivity.this.getSharedPreferences(NoteSaved, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim(),
                        getSelectedPriority());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(autoSave) {
            SharedPreferences sp = NoteActivity.this.getSharedPreferences(NoteSaved, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            CharSequence content = editText.getText();
            if (!TextUtils.isEmpty(content))
                editor.putString(TodoNote.COLUMN_NAME_CONTENT,content.toString());
            editor.putInt(TodoNote.COLUMN_NAME_STATE,getSelectedPriority().intValue);
            editor.apply();
        }
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;
    }

    private boolean saveNote2Database(String content, Priority priority) {
        // TODO: 2021/7/19 8. 这里插入数据库
        Calendar calendar = Calendar.getInstance();
        Long Time = calendar.getTimeInMillis();
        ContentValues values = new ContentValues();
        values.put(TodoNote.COLUMN_NAME_DATE,String.valueOf(Time));
        values.put(TodoNote.COLUMN_NAME_PRIORITY,String.valueOf(priority.intValue));
        values.put(TodoNote.COLUMN_NAME_CONTENT,content);
        values.put(TodoNote.COLUMN_NAME_STATE,String.valueOf(State.TODO.intValue));
        database.insert(TodoNote.TABLE_NAME,null,values);
        return true;
    }

    private Priority getSelectedPriority() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.btn_high:
                return Priority.High;
            case R.id.btn_medium:
                return Priority.Medium;
            default:
                return Priority.Low;
        }
    }
}
