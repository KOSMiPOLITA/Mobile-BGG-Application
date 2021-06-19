package com.example.zad2_141249

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_historia_rankingu_gry.*

class HistoriaRankinguGry : AppCompatActivity() {
    private val wersja = WersjaDB().zwrocWersje()
    private val db = MyDBHandler(this,"db", null, wersja)
    private lateinit var tytul_do_znalezienia: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historia_rankingu_gry)

        val titleText: TextView = findViewById(R.id.tytulGryHistoraia)

        val tytul_gry = intent.getStringExtra("game-tite-rank")
        if (tytul_gry != null && tytul_gry != "") {
            titleText.text = tytul_gry
            try {
                tytul_do_znalezienia = tytul_gry
                val historiaGier = znajdzGre()



                recycler_view_historia.adapter = RecyclerHistoria(historiaGier)
                recycler_view_historia.layoutManager = LinearLayoutManager(this)
                recycler_view_historia.setHasFixedSize(true)
            }
            catch(e: Error) {

            }
        }

    }

    fun znajdzGre() : ArrayList<HistoriaItem>? {
        if (tytul_do_znalezienia != null) {
            val res = db.findHistoryOfGame(tytul_do_znalezienia)
            return res
        }
        return null
    }
}