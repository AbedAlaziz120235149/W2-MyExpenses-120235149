package com.example.myexpenses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseAdapter extends ListAdapter<Expense, ExpenseAdapter.ExpenseHolder> {

    public interface OnExpenseAction {
        void onLongClick(Expense expense);
    }

    private static final DiffUtil.ItemCallback<Expense> DIFF =
            new DiffUtil.ItemCallback<Expense>() {
                @Override
                public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle())
                            && oldItem.getAmount() == newItem.getAmount();
                }
            };

    private final OnExpenseAction action;
    private final DecimalFormat numberFormat = new DecimalFormat("#.#");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", new Locale("ar"));

    public ExpenseAdapter(OnExpenseAction action) {
        super(DIFF);
        this.action = action;
    }

    @NonNull
    @Override
    public ExpenseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseHolder holder, int position) {
        Expense expense = getItem(position);
        holder.title.setText(expense.getTitle());
        holder.amount.setText(numberFormat.format(expense.getAmount()));
        holder.date.setText(dateFormat.format(new Date(expense.getCreatedAt())));

        holder.itemView.setOnLongClickListener(v -> {
            action.onLongClick(expense);
            return true;
        });
    }

    static class ExpenseHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView amount;
        TextView date;

        ExpenseHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvExpenseTitle);
            amount = itemView.findViewById(R.id.tvExpenseAmount);
            date = itemView.findViewById(R.id.tvExpenseDate);
        }
    }
}
