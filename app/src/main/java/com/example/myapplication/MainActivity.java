package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTaskName;
    private EditText editTextTaskContent;
    private Button buttonAdd;
    private Button buttonPickDate;
    private Button buttonPickTime;
    private Button buttonShowForm;
    private LinearLayout taskForm;  // The layout holding the form
    private ListView listViewTasks;

    private ArrayList<Task> taskList;
    private ArrayAdapter<String> taskAdapter;

    private String selectedDate = "";
    private String selectedTime = "";

    // Task class to hold task details
    public class Task {
        private String taskName;
        private String taskContent;
        private String taskDate;
        private String taskTime;

        public Task(String taskName, String taskContent, String taskDate, String taskTime) {
            this.taskName = taskName;
            this.taskContent = taskContent;
            this.taskDate = taskDate;
            this.taskTime = taskTime;
        }

        public String getTaskName() {
            return taskName;
        }

        public String getTaskContent() {
            return taskContent;
        }

        public String getTaskDate() {
            return taskDate;
        }

        public String getTaskTime() {
            return taskTime;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        editTextTaskName = findViewById(R.id.editTextTaskName);
        editTextTaskContent = findViewById(R.id.editTextTaskContent);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonPickDate = findViewById(R.id.buttonPickDate);
        buttonPickTime = findViewById(R.id.buttonPickTime);
        buttonShowForm = findViewById(R.id.buttonShowForm);
        taskForm = findViewById(R.id.taskForm);  // The task input form
        listViewTasks = findViewById(R.id.listViewTasks);

        // Initialize task list and adapter
        taskList = new ArrayList<>();
        ArrayList<String> taskNames = new ArrayList<>();
        taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskNames);

        // Set the adapter for the ListView
        listViewTasks.setAdapter(taskAdapter);

        // Initially, hide the task form
        taskForm.setVisibility(View.GONE);

        // Show task form when Add Task button is clicked
        buttonShowForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskForm.setVisibility(View.VISIBLE);  // Show the form
                buttonShowForm.setVisibility(View.GONE);  // Hide the Add Task button
            }
        });

        // Date picker dialog
        buttonPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        Toast.makeText(MainActivity.this, "Date selected: " + selectedDate, Toast.LENGTH_SHORT).show();
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Time picker dialog
        buttonPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedTime = hourOfDay + ":" + String.format("%02d", minute);
                        Toast.makeText(MainActivity.this, "Time selected: " + selectedTime, Toast.LENGTH_SHORT).show();
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        // Add task button click listener
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = editTextTaskName.getText().toString();
                String taskContent = editTextTaskContent.getText().toString();

                if (!taskName.isEmpty() && !taskContent.isEmpty() && !selectedDate.isEmpty() && !selectedTime.isEmpty()) {
                    Task newTask = new Task(taskName, taskContent, selectedDate, selectedTime);
                    taskList.add(newTask);
                    taskNames.add(taskName);  // Only display the task name in the ListView
                    taskAdapter.notifyDataSetChanged();  // Refresh ListView

                    // Clear the form and hide it again
                    editTextTaskName.setText("");
                    editTextTaskContent.setText("");
                    selectedDate = "";
                    selectedTime = "";
                    taskForm.setVisibility(View.GONE);  // Hide the form
                    buttonShowForm.setVisibility(View.VISIBLE);  // Show the Add Task button
                } else {
                    Toast.makeText(MainActivity.this, "Please enter task name, details, date, and time", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the item click listener to show task details on click
        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task clickedTask = taskList.get(position);
                showTaskDetailsDialog(clickedTask, position);
            }
        });
    }

    // Method to show task details in a dialog
    private void showTaskDetailsDialog(Task task, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(task.getTaskName());
        builder.setMessage("Task Details: \n\n" + task.getTaskContent()
                + "\n\nDue Date: " + task.getTaskDate() + "\nDue Time: " + task.getTaskTime());

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                taskList.remove(position);
                taskAdapter.remove(task.getTaskName());
                taskAdapter.notifyDataSetChanged();  // Refresh ListView
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Close", null);
        builder.show();
    }
}
