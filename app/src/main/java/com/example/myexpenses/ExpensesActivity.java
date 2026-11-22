package com.example.myexpenses;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class ExpensesActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseAction {

    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_CATEGORY_NAME = "category_name";

    private int categoryId;
    private ExpenseDao expenseDao;
    private ExpenseAdapter adapter;
    private EditText etTitle;
    private EditText etAmount;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        categoryId = getIntent().getIntExtra(EXTRA_CATEGORY_ID, -1);
        String categoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(categoryName);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitle = findViewById(R.id.etExpenseTitle);
        etAmount = findViewById(R.id.etExpenseAmount);
        Button btnAdd = findViewById(R.id.btnAddExpense);
        tvEmpty = findViewById(R.id.tvEmptyExpenses);
        RecyclerView recyclerView = findViewById(R.id.rvExpenses);

        expenseDao = ExpenseDatabase.getInstance(this).expenseDao();

        adapter = new ExpenseAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> addExpense());

        expenseDao.getByCategory(categoryId).observe(this, this::showExpenses);
    }

    private void showExpenses(List<Expense> expenses) {
        adapter.submitList(expenses);
        tvEmpty.setVisibility(expenses.isEmpty() ? TextView.VISIBLE : TextView.GONE);
    }

    private void addExpense() {
        String title = etTitle.getText().toString().trim();
        String amountText = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "اكتب وصف المصروف", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "اكتب مبلغ صحيح", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "المبلغ يجب أن يكون أكبر من صفر", Toast.LENGTH_SHORT).show();
            return;
        }

        Expense expense = new Expense(categoryId, title, amount, System.currentTimeMillis());
        ExpenseDatabase.runOnIo(() -> expenseDao.insert(expense));

        etTitle.setText("");
        etAmount.setText("");
    }

    @Override
    public void onLongClick(Expense expense) {
        new AlertDialog.Builder(this)
                .setTitle("حذف المصروف")
                .setMessage("هل تريد حذف \"" + expense.getTitle() + "\"؟")
                .setPositiveButton("حذف", (dialog, which) ->
                        ExpenseDatabase.runOnIo(() -> expenseDao.delete(expense)))
                .setNegativeButton("إلغاء", null)
                .show();
    }
}
