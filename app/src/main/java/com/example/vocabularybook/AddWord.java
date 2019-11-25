package com.example.vocabularybook;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import DBTools.DbHelper;

public class AddWord extends AppCompatActivity {
    EditText words;
    EditText Trans;
    EditText sentence;
    Button Add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        words = findViewById(R.id.Name);
        Trans = findViewById(R.id.Tans);
        sentence = findViewById(R.id.Sentence);
        Add =findViewById(R.id.confirm);


        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = words.getText().toString();
                String trans = Trans.getText().toString();
                String  sen = sentence.getText().toString();
                insert(word,trans,sen);
                finish();
            }
        });

    }

    public void insert(String word,String trans,String sentence){
        ContentValues values = new ContentValues();
        values.put("name",word);
        values.put("trans",trans);
        values.put("sentence",sentence);
        DbHelper helper = new DbHelper(AddWord.this,"word_db");
        SQLiteDatabase dbIn = helper.getWritableDatabase();
        long status = dbIn.insert("Words",null,values);
        if(status == -1){
            Toast.makeText(getApplicationContext(),"添加失败",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT).show();
        }

        dbIn.close();
    }
}
