package com.example.myexpenses;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    long insert(Category category);

    @Delete
    void delete(Category category);

    // لكل تصنيف: الاسم والميزانية ومجموع المصاريف
    @Query("SELECT c.id AS id, c.name AS name, c.budget AS budget, " +
            "COALESCE(SUM(e.amount), 0) AS total " +
            "FROM categories c LEFT JOIN expenses e ON e.category_id = c.id " +
            "GROUP BY c.id ORDER BY c.id DESC")
    LiveData<List<CategoryTotal>> getCategoriesWithTotal();
}
