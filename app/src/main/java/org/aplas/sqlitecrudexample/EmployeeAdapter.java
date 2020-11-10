package org.aplas.sqlitecrudexample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class EmployeeAdapter extends ArrayAdapter<Employee> {

    Context mCtx;
    int layoutRes;
    List<Employee> employeeList;
    SQLiteDatabase mDatabase;

    public EmployeeAdapter(Context mCtx, int layoutRes, List<Employee> employeeList, SQLiteDatabase mDatabase){
        super(mCtx, layoutRes, employeeList);

        this.mCtx = mCtx;
        this.layoutRes = layoutRes;
        this.employeeList = employeeList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        View view = inflater.inflate(layoutRes, null);

        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewDept = view.findViewById(R.id.textViewDepartment);
        TextView textViewSalary = view.findViewById(R.id.textViewSalary);
        TextView textViewJoinDate = view.findViewById(R.id.textViewJoiningDate);

        Employee employee = employeeList.get(position);

        textViewName.setText(employee.getName());
        textViewDept.setText(employee.getDept());
        textViewSalary.setText(String.valueOf(employee.getSalary()));
        textViewJoinDate.setText(employee.getJoiningdate());

        view.findViewById(R.id.buttonDeleteEmployee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEmployee(employee);
            }
        });


        view.findViewById(R.id.buttonEditEmployee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmployee(employee);
            }
        });

        return view;
    }

    private void updateEmployee(Employee employee){
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dialog_update_employee, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        EditText editTextName = view.findViewById(R.id.editTextName);
        EditText editTextSalary = view.findViewById(R.id.editTextSalary);
        Spinner spinner = view.findViewById(R.id.spinnerDepartment);

        editTextName.setText(employee.getName());
        editTextSalary.setText(String.valueOf(employee.getSalary()));




        view.findViewById(R.id.buttonUpdateEmployee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String salary = editTextSalary.getText().toString().trim();
                String dept = spinner.getSelectedItem().toString().trim();

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

                String sql = "UPDATE employees SET name = ?, department = ?, salary = ? WHERE id = ?";

                mDatabase.execSQL(sql, new String[]{name, dept, salary, String.valueOf(employee.getId())});

                Toast.makeText(mCtx, "Employee Updated", Toast.LENGTH_SHORT).show();

                loadEmployeesFromDatabaseAgain();

                alertDialog.dismiss();
            }
        });
    }

    private  void deleteEmployee(Employee employee){
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    String sql = "DELETE FROM employees WHERE id = ?";
                    mDatabase.execSQL(sql, new Integer[]{employee.getId()});
                    loadEmployeesFromDatabaseAgain();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadEmployeesFromDatabaseAgain(){
        String sql = "SELECT * FROM employees";

        Cursor cursor = mDatabase.rawQuery(sql, null);

        if(cursor.moveToFirst()){
            employeeList.clear();
            do{
                employeeList.add(new Employee(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4)
                ));
            }while(cursor.moveToNext());

            notifyDataSetChanged();
        }
    }
}
