package com.example.myexpenses;

// كلاس عادي (ليس Entity) لحمل نتيجة الاستعلام: اسم التصنيف وميزانيته ومجموع مصاريفه
public class CategoryTotal {

    public int id;
    public String name;
    public double budget;
    public double total;

    public CategoryTotal(int id, String name, double budget, double total) {
        this.id = id;
        this.name = name;
        this.budget = budget;
        this.total = total;
    }
}
