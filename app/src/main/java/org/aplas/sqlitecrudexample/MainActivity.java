package org.aplas.sqlitecrudexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String DATABASE_NAME = "MobileDatabase";

    SQLiteDatabase mDatabase;


    EditText editTextName, editTextSalary;
    Spinner spinnerDept;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextSalary = (EditText) findViewById(R.id.editTextSalary);
        spinnerDept = (Spinner) findViewById(R.id.spinnerDepartment);

        findViewById(R.id.buttonAddEmployee).setOnClickListener(this);
        findViewById(R.id.textViewViewEmployees).setOnClickListener(this);
    }

    private void createTable(){
        String sql = "CREATE TABLE IF NOT EXISTS employees (\n" +
                "    id INTEGER NOT NULL CONSTRAINT employees_pk PRIMARY KEY AUTOINCREMENT,\n" +
                "    name varchar(200) NOT NULL,\n" +
                "    department varchar(200) NOT NULL,\n" +
                "    joiningdate datetime NOT NULL,\n" +
                "    salary double NOT NULL\n" +
                ");";
        mDatabase.execSQL(sql);
    }

    private void addEmployee(){
        String name = editTextName.getText().toString().trim();
        String salary = editTextSalary.getText().toString().trim();
        String dept = spinnerDept.getSelectedItem().toString();

        if(name.isEmpty()){
            editTextName.setError("Name can't be empty");
            editTextName.requestFocus();
            return;
        }
        if(salary.isEmpty()){
            editTextSalary.setError("Salary can't be empty");
            editTextSalary.requestFocus();
            return;
        }

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String joiningDate = sdf.format(cal.getTime());


        String sql ="INSERT INTO employees(name, department, joiningdate, salary)" +
                "VALUES (?, ?, ?, ?);";
        mDatabase.execSQL(sql, new String[]{name, dept, joiningDate,salary});

        Toast.makeText(this, "Employee Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonAddEmployee:

                addEmployee();

                break;
            case R.id.textViewViewEmployees:

                startActivity(new Intent(this, EmployeeActivity.class));

                break;
        }
    }
}