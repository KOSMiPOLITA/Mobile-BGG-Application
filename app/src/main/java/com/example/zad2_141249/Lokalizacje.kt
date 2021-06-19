package com.example.zad2_141249

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_lokalizacje.*
import kotlinx.android.synthetic.main.dodal_lokalizacje.view.*
import kotlinx.android.synthetic.main.edytuj_item.view.*


class Lokalizacje : AppCompatActivity(), AdapterView.OnItemSelectedListener, RecyclerAdapter.OnItemClickListener {
    private val wersja = WersjaDB().zwrocWersje()
    private val db = MyDBHandler(this,"db", null,wersja)

    private lateinit var adapter: RecyclerLokalizacje
    private var listaLokacji: ArrayList<String>? = null

    private var spinner: Spinner? = null
    private var arrayAdapter: ArrayAdapter<String>? =  null

    private var poz: Int = 0
    private var ch_game: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lokalizacje)
        val dodajLok = findViewById<ImageButton>(R.id.buttonDodajLokalizacje)
        val zapiszButton = findViewById<ImageButton>(R.id.buttonZapiszLokalizacje)
        val edytujButton = findViewById<ImageButton>(R.id.buttonEdytujLokaliazje)
        zapiszButton.setOnClickListener {
            usunLokacje()
            Toast.makeText(this, "Zapisano zmiany", Toast.LENGTH_SHORT).show()
        }
        val wrocButton = findViewById<Button>(R.id.buttonWrocZLokal)
        wrocButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        try {
            listaLokacji = znajdzLokacje()
            adapter = RecyclerLokalizacje(listaLokacji, this)
            recycler_view_lokacje.adapter = adapter
            recycler_view_lokacje.layoutManager = LinearLayoutManager(this)
            recycler_view_lokacje.setHasFixedSize(true)
        }
        catch (e: Error) {

       }
    }

    fun usunLokacje() {
        listaLokacji = db.deleteLokacje(listaLokacji)
        val index = adapter.itemCount
        listaLokacji!!.add(index, "")
        listaLokacji!!.removeAt(index)
        adapter.notifyDataSetChanged()
    }

    fun znajdzLokacje() : ArrayList<String>? {
        val res = db.findLokalizacje()
        return res
    }

    fun insertLok(view: View) {
        var newItem: String = "nowa lokalizacja"
        val lokDialog = LayoutInflater.from(this).inflate(R.layout.dodal_lokalizacje, null)
        val lokBuilder = AlertDialog.Builder(this)
            .setView(lokDialog)
            .setTitle("Nazwa lokalizacji")
        val lokAlertDialog = lokBuilder.show()
        lokDialog.zaakceptujLokalizacje.setOnClickListener {
            lokAlertDialog.dismiss()
            val name = lokDialog.dodawanieLokalizacjeName.text.toString()
            if (name != "") {
                newItem = name
            }
            var index: Int = 0
            if (!listaLokacji?.contains(newItem)!!) {
                db.dodajLokalizacje(newItem)
                if (listaLokacji != null && listaLokacji?.size != 0) {
                    index = adapter.itemCount
                }

                listaLokacji!!.add(index, newItem)
            }
            else {
                Toast.makeText(this, "Istnieje już lokacja o podanej nazwie", Toast.LENGTH_SHORT).show()
            }
            Log.i("pomocy-141249-dbhaaaa", listaLokacji!!.toString())
            adapter.notifyDataSetChanged()
        }
    }

    fun updateLok(view: View) {
        val lokDialog = LayoutInflater.from(this).inflate(R.layout.edytuj_item, null)
        val lokBuilder = AlertDialog.Builder(this)
            .setView(lokDialog)
            .setTitle("Edytuj lokalizacje")
        val lokAlertDialog = lokBuilder.show()
        var itemList: MutableList<String> = ArrayList()
        Log.i("pomocy-141249-1234", "ROZ: ${listaLokacji?.size}")
        if (listaLokacji != null && listaLokacji?.size != 0) {
            for (i in 0 until listaLokacji!!.size) {
                Log.i("pomocy-141249-1234", "ITEM: ${listaLokacji!![i]}")
                val elem = listaLokacji!![i]
                itemList.add(elem)
            }
            spinner = lokDialog.spinner_with_RV
            Log.i("pomocy-141249-1234", "spinner: $spinner")
            arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, itemList)
            spinner?.adapter = arrayAdapter
            spinner?.onItemSelectedListener = this}
        lokDialog.edytujLokalizacje.setOnClickListener {
            lokAlertDialog.dismiss()
            val lok1 = ch_game
            val lok2 = lokDialog.edit_item_z_RV.text.toString()
            db.aktualizujLokacje(lok1, lok2)
            val intent = Intent(this, Lokalizacje::class.java)
            startActivity(intent)
            //val index: Int = adapter.itemCount
            //listaLokacji!!.add(index, newItem)
            //adapter.notifyDataSetChanged()
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

    override fun onItemClick(position: Int) {
        val cliecedItem: String = listaLokacji!!.get(position)
        Log.i("pomocy-141249-lok-it", "Klik: $cliecedItem")
        val intent = Intent(this, GryWLokacji::class.java)
        intent.putExtra("lokacja", cliecedItem)
        startActivity(intent)
    }

}