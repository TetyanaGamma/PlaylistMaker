package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var clearButton: ImageView
    private var searchText: String = SAVED_TEXT  // переменная для хранения введённого текста

    // константы для сохранения и извлечения данных
    companion object {
        const val INPUT_TEXT = "SEARCH_TEXT"
        const val SAVED_TEXT = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.search_toolbar)
        searchInput = findViewById(R.id.serch_input)
        clearButton = findViewById<ImageView>(R.id.clear_icon)

        // нажатие на иконку назад
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // создаём анонимный класс TextWatcher для обработки ввода текста
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s.isNullOrEmpty()) {
                    clearButton.visibility =
                        View.GONE // если в строке поиска ничего нет, кнопка очистить не нужна
                } else {
                    clearButton.visibility = View.VISIBLE
                    searchText = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchInput.addTextChangedListener(searchTextWatcher)
        // по клику на строку поиска выводим клавиатуру
        searchInput.setOnClickListener() {
            showKeyBoard()
        }
        // обрабатываем нажатие на кнопку очистить
        clearButton.setOnClickListener() {
            searchInput.text.clear()
            clearButton.visibility = View.GONE
            hideKeyBoard()
        }
        // получаем сохраненное значение
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(INPUT_TEXT, SAVED_TEXT)
            searchInput.setText(searchText)
        }
    }

    private fun showKeyBoard() {
        searchInput.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(searchInput.windowToken, 0)
    }

    // сохраняем  введенное значение
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(INPUT_TEXT, SAVED_TEXT)
        searchInput.setText(searchText)

    }
}