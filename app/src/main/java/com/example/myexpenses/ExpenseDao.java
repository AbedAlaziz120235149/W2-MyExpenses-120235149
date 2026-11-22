package com.example.myexpenses;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    long insert(Expense expense);

    @Delete
    void delete(Expense expense);

    // مصاريف تصنيف واحد فقط، من الأحدث إلى الأقدم
    @Query("SELECT * FROM expenses WHERE category_id = :categoryId ORDER BY createdAt DESC")
    LiveData<List<Expense>> getByCategory(int categoryId);
}
