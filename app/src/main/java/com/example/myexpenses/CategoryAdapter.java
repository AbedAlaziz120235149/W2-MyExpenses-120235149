package com.example.myexpenses;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.Locale;

public class CategoryAdapter extends ListAdapter<CategoryTotal, CategoryAdapter.CategoryHolder> {

    public interface OnCategoryAction {
        void onClick(CategoryTotal item);

        void onLongClick(CategoryTotal item);
    }

    private static final DiffUtil.ItemCallback<CategoryTotal> DIFF =
            new DiffUtil.ItemCallback<CategoryTotal>() {
                @Override
                public boolean areItemsTheSame(@NonNull CategoryTotal oldItem, @NonNull CategoryTotal newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull CategoryTotal oldItem, @NonNull CategoryTotal newItem) {
                    return oldItem.name.equals(newItem.name)
                            && oldItem.budget == newItem.budget
                            && oldItem.total == newItem.total;
                }
            };

    private final OnCategoryAction action;
    private final DecimalFormat numberFormat = new DecimalFormat("#.#");

    public CategoryAdapter(OnCategoryAction action) {
        super(DIFF);
        this.action = action;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        CategoryTotal item = getItem(position);
        holder.name.setText(item.name);

        String text = String.format(Locale.US, "%s / %s",
                numberFormat.format(item.total), numberFormat.format(item.budget));
        holder.amount.setText(text);

        // إذا تجاوز المجموع الميزانية يظهر الرقم باللون الأحمر
        if (item.total > item.budget) {
            holder.amount.setTextColor(Color.parseColor("#D32F2F"));
        } else {
            holder.amount.setTextColor(Color.parseColor("#616161"));
        }

        holder.itemView.setOnClickListener(v -> action.onClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            action.onLongClick(item);
            return true;
        });
    }

    static class CategoryHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView amount;

        CategoryHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvCategoryName);
            amount = itemView.findViewById(R.id.tvCategoryAmount);
        }
    }
}
