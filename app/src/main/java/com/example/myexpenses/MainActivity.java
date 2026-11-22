package com.example.myexpenses;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryAction {

    private EditText etName;
    private EditText etBudget;
    private TextView tvEmpty;
    private MaterialToolbar toolbar;
    private CategoryAdapter adapter;
    private CategoryDao categoryDao;
    private final DecimalFormat numberFormat = new DecimalFormat("#.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etName = findViewById(R.id.etCategoryName);
        etBudget = findViewById(R.id.etCategoryBudget);
        Button btnAdd = findViewById(R.id.btnAddCategory);
        RecyclerView recyclerView = findViewById(R.id.rvCategories);
        tvEmpty = findViewById(R.id.tvEmptyCategories);

        categoryDao = ExpenseDatabase.getInstance(this).categoryDao();

        adapter = new CategoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> addCategory());

        categoryDao.getCategoriesWithTotal().observe(this, this::showCategories);
    }

    private void showCategories(List<CategoryTotal> categories) {
        adapter.submitList(categories);
        tvEmpty.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);

        // مجموع كل المصاريف يظهر في شريط العنوان ويتحدث لوحده
        double allTotal = 0;
        for (CategoryTotal item : categories) {
            allTotal += item.total;
        }
        toolbar.setSubtitle("Total: " + numberFormat.format(allTotal));
    }

    private void addCategory() {
        String name = etName.getText().toString().trim();
        String budgetText = etBudget.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "اكتب اسم التصنيف", Toast.LENGTH_SHORT).show();
            return;
        }

        double budget = 0;
        if (!TextUtils.isEmpty(budgetText)) {
            try {
                budget = Double.parseDouble(budgetText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "الميزانية غير صحيحة", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Category category = new Category(name, budget);
        ExpenseDatabase.runOnIo(() -> categoryDao.insert(category));

        etName.setText("");
        etBudget.setText("");
    }

    @Override
    public void onClick(CategoryTotal item) {
        Intent intent = new Intent(this, ExpensesActivity.class);
        intent.putExtra(ExpensesActivity.EXTRA_CATEGORY_ID, item.id);
        intent.putExtra(ExpensesActivity.EXTRA_CATEGORY_NAME, item.name);
        startActivity(intent);
    }

    @Override
    public void onLongClick(CategoryTotal item) {
        new AlertDialog.Builder(this)
                .setTitle("حذف التصنيف")
                .setMessage("سيتم حذف التصنيف \"" + item.name + "\" وكل مصاريفه أيضاً. هل أنت متأكد؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    Category category = new Category(item.name, item.budget);
                    category.setId(item.id);
                    ExpenseDatabase.runOnIo(() -> categoryDao.delete(category));
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }
}
