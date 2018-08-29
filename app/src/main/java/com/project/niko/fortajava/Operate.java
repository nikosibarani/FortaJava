package com.project.niko.fortajava;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Operate extends AppCompatActivity {

    long startTime = 0;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_operate);

        int x = 0;
        while( x < 20 ) {
            System.out.println("value of x : " + x*5);
            x++;
        }

        startTime = System.currentTimeMillis();
        day(6);
    }

    private int multiplication(int a, int b){
        return a*b;
    }

    private void day(int number){
        switch (number){
            case 1:
                Toast.makeText(context, "Senin", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(context, "Selasa", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(context, "Rabu", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(context, "Kamis", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                Toast.makeText(context, "Jumat", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, "Weekend", Toast.LENGTH_SHORT).show();
                break;
        }
        long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("ExecutionTime " + executionTime);
    }

    private void nestedLoop(int rows){
        for(int i = 1; i <= rows; ++i) {
            for(int j = 1; j <= i; ++j) {
                System.out.print(i*j + " ");
            }
        }
        long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("ExecutionTime " + executionTime);
    }

    private void ifelse(String day){
        if(day.equals("Minggu") || day.equals("minggu")){
            Toast.makeText(context, "Weekend", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Weekday", Toast.LENGTH_SHORT).show();
        }
        long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("ExecutionTime " + executionTime);
    }
}
