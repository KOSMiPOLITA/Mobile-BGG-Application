package com.example.zad2_141249

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import java.util.ArrayList

class UsunGre : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val wersja = WersjaDB().zwrocWersje()
    private val db = MyDBHandler(this,"db", null,wersja)

    private var findGame:MutableList<Game>? = null
    private var spinner: Spinner? = null
    private var arrayAdapter: ArrayAdapter<String>? =  null

    private var poz: Int = 0
    private var ch_game: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usun_gre)

        try {
            val lista = db.findAllTitlesOfGames()
            var itemList: MutableList<String> = ArrayList()
            if (lista != null) {
                for (i in 0 until lista.size) {
                    val elem = lista[i]
                    itemList.add(elem)
                }
                spinner = findViewById(R.id.spinnerUsuwanieGry)
                arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, itemList)
                spinner?.adapter = arrayAdapter
                spinner?.onItemSelectedListener = this
            }
            else {
                itemList.add("pusta baza")
                spinner = findViewById(R.id.spinnerUsuwanieGry)
                arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, itemList)
                spinner?.adapter = arrayAdapter
                spinner?.onItemSelectedListener = this
            }
        }

        catch (e: Error) {

        }

        val buttonUsun = findViewById<Button>(R.id.usuwanieGre)
        buttonUsun.setOnClickListener {
            try {
                usunGreZBazy(ch_game, poz)
            }
            catch (e: Error) {

            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonWroc = findViewById<Button>(R.id.buttonWrocZUsuwanie)
        buttonWroc.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var items: String = parent?.getItemAtPosition(position) as String
        ch_game = items
        poz = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    fun usunGreZBazy(name: String, p: Int) {
        if (name != null && poz != null) {
            db.usunGreZBazy(name)
        }
    }
}