package com.budgetbuddysa.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CategoriesActivity : AppCompatActivity() {

    private lateinit var categoryListText: TextView
    private lateinit var newCategoryInput: EditText
    private lateinit var addCategoryButton: Button

    private val preferencesName =
        "BudgetBuddyCategories"

    private val categoriesKey =
        "categories"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_categories)

        categoryListText =
            findViewById(R.id.categoryListText)

        newCategoryInput =
            findViewById(R.id.newCategoryInput)

        addCategoryButton =
            findViewById(R.id.addCategoryBtn)

        loadCategories()

        addCategoryButton.setOnClickListener {

            addNewCategory()
        }
    }

    override fun onResume() {
        super.onResume()

        loadCategories()
    }

    private fun addNewCategory() {

        val categoryName =
            newCategoryInput.text
                .toString()
                .trim()

        if (categoryName.isEmpty()) {

            Toast.makeText(
                this,
                "Please enter a category name",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val preferences =
            getSharedPreferences(
                preferencesName,
                MODE_PRIVATE
            )

        val savedCategories =
            preferences.getStringSet(
                categoriesKey,
                emptySet()
            )?.toMutableSet()
                ?: mutableSetOf()

        if (
            savedCategories.any {
                it.equals(
                    categoryName,
                    ignoreCase = true
                )
            }
        ) {

            Toast.makeText(
                this,
                "This category already exists",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        savedCategories.add(categoryName)

        preferences.edit()
            .putStringSet(
                categoriesKey,
                savedCategories
            )
            .apply()

        newCategoryInput.text.clear()

        loadCategories()

        Toast.makeText(
            this,
            "Category added successfully",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadCategories() {

        val preferences =
            getSharedPreferences(
                preferencesName,
                MODE_PRIVATE
            )

        val savedCategories =
            preferences.getStringSet(
                categoriesKey,
                emptySet()
            ) ?: emptySet()

        if (savedCategories.isEmpty()) {

            categoryListText.text =
                "No custom categories yet."

            return
        }

        val categoryDisplay =
            StringBuilder()

        savedCategories
            .toList()
            .sorted()
            .forEachIndexed { index, category ->

                categoryDisplay.append(
                    "${index + 1}. $category\n\n"
                )
            }

        categoryListText.text =
            categoryDisplay.toString()
    }
}