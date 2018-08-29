package com.project.niko.fortajava

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class Ooperate : AppCompatActivity() {

    internal var startTime: Long = 0
    val context : Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ooperate)

        var x = 0
        while (x < 20) {
            println("value of x : " + x * 5)
            x++
        }
    }

    private fun multiplication(a: Int, b: Int): Int {
        return a * b
    }

    private fun day(number: Int) {
        when (number) {
            1 -> Toast.makeText(context, "Senin", Toast.LENGTH_SHORT).show()
            2 -> Toast.makeText(context, "Selasa", Toast.LENGTH_SHORT).show()
            3 -> Toast.makeText(context, "Rabu", Toast.LENGTH_SHORT).show()
            4 -> Toast.makeText(context, "Kamis", Toast.LENGTH_SHORT).show()
            5 -> Toast.makeText(context, "Jumat", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(context, "Weekend", Toast.LENGTH_SHORT).show()
        }
        val executionTime = System.currentTimeMillis() - startTime
        println("ExecutionTime $executionTime")
    }

    private fun nestedLoop(rows: Int) {
        for (i in 1..rows) {
            for (j in 1..i) {
                print((i * j).toString() + " ")
            }
        }
        val executionTime = System.currentTimeMillis() - startTime
        println("ExecutionTime $executionTime")
    }

    private fun ifelse(day: String) {
        if (day == "Minggu" || day == "minggu") {
            Toast.makeText(context, "Weekend", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Weekday", Toast.LENGTH_SHORT).show()
        }
        val executionTime = System.currentTimeMillis() - startTime
        println("ExecutionTime $executionTime")
    }
}
