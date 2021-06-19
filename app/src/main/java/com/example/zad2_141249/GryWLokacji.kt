package com.example.zad2_141249

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_gry_wlokacji.*
import java.lang.Error

class GryWLokacji : AppCompatActivity() {
    private val wersja = WersjaDB().zwrocWersje()
    private val db = MyDBHandler(this,"db", null,wersja)
    private var tytul = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gry_wlokacji)

        val titleText: TextView = findViewById(R.id.lokalizacjaSprawdzana)

        val lok = intent.getStringExtra("lokacja")
        Log.i("pomocy-141249-lok-it", "Klik klik: $lok")
        if (lok != null && lok != "") {
            titleText.text = lok
            tytul = lok
            try {
                val lista = znajdzGry()

                recycler_gier_w_lokacji.adapter = RecyclerGryWLokalizacji(lista)
                recycler_gier_w_lokacji.layoutManager = LinearLayoutManager(this)
                recycler_gier_w_lokacji.setHasFixedSize(true)
            }
            catch(e: Error) {

            }
        }
    }

    fun znajdzGry() : ArrayList<String>? {
        if (tytul != null) {
            val res = db.findGryZLokacji(tytul)
            return res
        }
        return null
    }
}