package com.example.contact_cs3354;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    MyHelper myHelper;
    private EditText etName;
    private EditText etPhone;
    private TextView tvShow;
    private Button btnAdd;
    private Button btnQuery;
    private Button btnUpdate;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myHelper = new MyHelper(this);
        init();
    }
    private void init(){
        etName = (EditText)findViewById(R.id.et_name);
        etPhone = (EditText)findViewById(R.id.et_phone);
        tvShow = (TextView)findViewById(R.id.tv_show);
        btnAdd = (Button)findViewById(R.id.btn_add);
        btnQuery = (Button)findViewById(R.id.btn_query);
        btnUpdate = (Button)findViewById(R.id.btn_update);
        btnDelete = (Button)findViewById(R.id.btn_delete);
        btnAdd.setOnClickListener(this);   //Button listener
        btnQuery.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        tvShow.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
    @Override
    public void onClick(View v){
        String name;
        String phone;
        SQLiteDatabase db;
        switch (v.getId()){
            case R.id.btn_add:
                name = etName.getText().toString().trim();
                phone = etPhone.getText().toString().trim();
                db = myHelper.getWritableDatabase();
                if (name.equals("") || phone.equals("")){
                    Toast.makeText(this,"Can not add contact",Toast.LENGTH_SHORT).show();
                }
                else {
                    db.execSQL("insert into person (name,phone) values(?,?)", new Object[]{name, phone});
                    Toast.makeText(this,"Contact add!",Toast.LENGTH_SHORT).show();
                }
                db.close();
                break;
            case R.id.btn_query: //Find contact
                db = myHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select name,phone from person",null);
                if (cursor.getCount() == 0){
                    tvShow.setText("");
                    Toast.makeText(this,"Empty",Toast.LENGTH_SHORT).show();
                }else {
                    cursor.moveToFirst();
                    tvShow.setText("Name：" + cursor.getString(0) + " ; Tel：" + cursor.getString(1));
                    while (cursor.moveToNext()){
                        tvShow.append("\n" + "Name：" + cursor.getString(0) + " ; Tel：" + cursor.getString(1));
                    }
                }
                cursor.close();
                db.close();
                break;
            case R.id.btn_update://add contact
                db = myHelper.getWritableDatabase();
                name = etName.getText().toString().trim();
                phone = etPhone.getText().toString().trim();
                if (name.equals("") || phone.equals("")){
                    Toast.makeText(this,"Contact Add!",Toast.LENGTH_SHORT).show();
                }
                else {
                    db.execSQL("update person set name=?,phone=? where name=?", new Object[]{name, phone, name});
                    Toast.makeText(this,"Contact edited!",Toast.LENGTH_SHORT).show();
                }
                db.close();
                break;
            case R.id.btn_delete://Delete a contact
                db = myHelper.getWritableDatabase();
                name = etName.getText().toString().trim();
                phone = etPhone.getText().toString().trim();
                if (name.equals("") || phone.equals("")){
                    Toast.makeText(this,"Unable to Delete",Toast.LENGTH_SHORT).show();
                }
                else {
                    db.execSQL("delete from person where name=? and phone=?", new Object[]{name, phone});
                    Toast.makeText(this,"Deleted",Toast.LENGTH_SHORT).show();
                }
                db.close();
                break;
        }
    }
}
