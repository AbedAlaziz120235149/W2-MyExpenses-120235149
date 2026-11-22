package com.example.myexpenses;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Category.class, Expense.class}, version = 1, exportSchema = false)
public abstract class ExpenseDatabase extends RoomDatabase {

    private static final String DB_NAME = "expenses.db";
    private static ExpenseDatabase instance;

    // خيط للكتابة في الخلفية
    private static final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public abstract CategoryDao categoryDao();

    public abstract ExpenseDao expenseDao();

    public static synchronized ExpenseDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ExpenseDatabase.class, DB_NAME).build();
        }
        return instance;
    }

    public static void runOnIo(Runnable task) {
        ioExecutor.execute(task);
    }
}
