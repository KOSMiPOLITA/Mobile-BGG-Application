package com.example.zad2_141249

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.game_list_item.*


class MainActivity : AppCompatActivity(), RecyclerAdapter.OnItemClickListener {

    var gamesFromXML:MutableList<Game>? = null

    private val growaLista: MutableList<GameItem> = ArrayList()
    private val adapter = RecyclerAdapter(growaLista, this)

    private val wersja = WersjaDB().zwrocWersje()
    val db = MyDBHandler(this,"db", null,wersja)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonAddGame = findViewById<Button>(R.id.addGame)
        buttonAddGame.setOnClickListener{
            val intent = Intent(this, DodawanieGry::class.java)
            startActivity(intent)
        }

        val buttonEkrannBGG = findViewById<Button>(R.id.ekranBGG)
        buttonEkrannBGG.setOnClickListener {
            val intent = Intent(this, EkranBgg::class.java)
            startActivity(intent)
        }

        val buttonUsunGre = findViewById<Button>(R.id.buttonUsunGre)
        buttonUsunGre.setOnClickListener {
            val intent = Intent(this, UsunGre::class.java)
            startActivity(intent)
        }

        val buttonLokalizacje = findViewById<Button>(R.id.buttonLokalizacje)
        buttonLokalizacje.setOnClickListener {
            val intent = Intent(this, Lokalizacje::class.java)
            startActivity(intent)
        }

        var listaPosiadanychGier = loadGamesFromDB(1)
        Log.i("pomocy-141249-rl", "Rozmiar listy${listaPosiadanychGier?.size}")
        if (listaPosiadanychGier?.size != null) {
            for (i in listaPosiadanychGier!!) {
                growaLista.add(
                    GameItem(pozycja = i.pozycjaAktualna.toString(), obrazek = i.miniaturka, tytul = i.tytul,
                        opis = i.opis, rok = i.rokWydania.toString())
                )
            }
        }
        if (growaLista.size != 0) {
            Log.i("pomocy-141249", "${growaLista.size}")
            recycler_view.adapter = adapter
            recycler_view.layoutManager = LinearLayoutManager(this)
            recycler_view.setHasFixedSize(true)
        }
        

        val sortAZ = findViewById<Button>(R.id.buttonAlfabetycznie)
        sortAZ.setOnClickListener {
            growaLista.clear()
            var listaPosiadanychGier = loadGamesFromDB(1)
            Log.i("pomocy-141249-rl", "Rozmiar listy${listaPosiadanychGier?.size}")
            if (listaPosiadanychGier?.size != null) {
                for (i in listaPosiadanychGier!!) {
                    growaLista.add(
                        GameItem(pozycja = i.pozycjaAktualna.toString(), obrazek = i.miniaturka, tytul = i.tytul,
                            opis = i.opis, rok = i.rokWydania.toString())
                    )
                }
            }
            adapter.notifyDataSetChanged()
        }

        val sortROK = findViewById<Button>(R.id.buttonDataWydania)
        sortROK.setOnClickListener {
            growaLista.clear()
            var listaPosiadanychGier = loadGamesFromDB(3)
            Log.i("pomocy-141249-rl", "Rozmiar listy${listaPosiadanychGier?.size}")
            if (listaPosiadanychGier?.size != null) {
                for (i in listaPosiadanychGier!!) {
                    growaLista.add(
                        GameItem(pozycja = i.pozycjaAktualna.toString(), obrazek = i.miniaturka, tytul = i.tytul,
                            opis = i.opis, rok = i.rokWydania.toString())
                    )
                }
            }
            adapter.notifyDataSetChanged()
        }

        val sortPOZ = findViewById<Button>(R.id.buttonRanking)
        sortPOZ.setOnClickListener {
            growaLista.clear()
            var listaPosiadanychGier = loadGamesFromDB(12)
            Log.i("pomocy-141249-rl", "Rozmiar listy${listaPosiadanychGier?.size}")
            if (listaPosiadanychGier?.size != null) {
                for (i in listaPosiadanychGier!!) {
                    growaLista.add(
                        GameItem(pozycja = i.pozycjaAktualna.toString(), obrazek = i.miniaturka, tytul = i.tytul,
                            opis = i.opis, rok = i.rokWydania.toString())
                    )
                }
            }
            adapter.notifyDataSetChanged()
        }


    }

    override fun onItemClick(position: Int) {
        val cliecedItem: GameItem = growaLista[position]
        val intent = Intent(this, GameDetails::class.java)
        adapter.notifyItemChanged(position)
        intent.putExtra("gra", cliecedItem.tytul)
        startActivity(intent)
    }

    fun addNewGame() {
        gamesFromXML?.let { db.addGameFull(it[0]) }
    }

    fun loadGamesFromDB(ord: Int) : MutableList<Game>? {
        val dbHandler = db
        var lista: MutableList<Game>? = dbHandler.findGames(ord)
        if (lista?.size != 0) {
            Log.i("pomocy-141249", "Pobrano db: ${lista?.get(0)?.tytul} ${lista?.get(0)?.idBGG}")
            return lista
        }
        else {
            Log.i("pomocy-141249", "Nie pobrano z db")
            return null
        }
    }
}