package com.example.zad2_141249

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_find_result.*
import kotlinx.android.synthetic.main.activity_game_details.*
import kotlinx.android.synthetic.main.dodaj_artyste.view.*
import kotlinx.android.synthetic.main.dodaj_projektanta.view.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.time.LocalDate
import java.util.ArrayList
import javax.xml.parsers.DocumentBuilderFactory


class GameDetails : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val wersja = WersjaDB().zwrocWersje()
    private val db = MyDBHandler(this,"db", null,wersja)

    var gameToShow:MutableList<Game>? = null
    var poz = 0
    var lokGry = ""
    var staraLok = ""

    private lateinit var adapter: RecyclerProjektantSzczegoly
    private lateinit var adapter2: RecyclerArtystaSzczegoly
    private lateinit var adapter3: RecyclerDodatkiFind

    var tytulDoZapamietania = ""

    private var spinner: Spinner? = null
    private var arrayAdapter:ArrayAdapter<String>? =  null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)


        val addButton = findViewById<Button>(R.id.addZatwierdzDet)

        val backButton = findViewById<Button>(R.id.wrocZatwierdzDet)
        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val tytulPole = findViewById<TextView>(R.id.tytulEditDet)
        val tytulOrgPole = findViewById<TextView>(R.id.tytulOrgEditDet)
        val rokPole = findViewById<TextView>(R.id.rokWydaniaEditDet)
        val opisPole = findViewById<TextView>(R.id.opisGryEditDet)
        val dataZamPole = findViewById<TextView>(R.id.dataZamowieniaEditDet)
        val dataDodPole = findViewById<TextView>(R.id.dataDodaniaGryEditDet)
        val kosztPole = findViewById<TextView>(R.id.kosztZakupuEditDet)
        val scdPole = findViewById<TextView>(R.id.scdGryEditDet)
        val eanPole = findViewById<TextView>(R.id.kodEanGryEditDet)
        val idBggPole = findViewById<TextView>(R.id.idBggGryNotEditDet)
        val kodProdPole = findViewById<TextView>(R.id.kodProdukcyjnyGryEditDet)
        val rankingPole = findViewById<TextView>(R.id.aktPozycjaGryNotEditDet)
        val wersjaPole = findViewById<TextView>(R.id.wersjaGryEditDet)
        val komentarzPole = findViewById<TextView>(R.id.komentarzGryEditDet)
        val zdjeciePole = findViewById<TextView>(R.id.zdjecieGryEditDet)
        val lokalPole = findViewById<Spinner>(R.id.spinner_lokalizacje)

        val tytul_wyszukiwanej = intent.getStringExtra("gra")
        Log.i("pomocy-141249-22", "$tytul_wyszukiwanej")
        gameToShow = db.findGame(tytul_wyszukiwanej)
        Log.i("pomocy-141249-222", "Dane ${gameToShow?.get(0)?.tytul} ${gameToShow?.get(0)?.oryginalnyTytul}")
        if (tytul_wyszukiwanej != null) {

            try {
                tytulPole.text = tytul_wyszukiwanej

                tytulDoZapamietania = tytulPole.text.toString()
                tytulOrgPole.text = gameToShow?.get(0)?.oryginalnyTytul
                rokPole.text = gameToShow?.get(0)?.rokWydania.toString()
                opisPole.text = gameToShow?.get(0)?.opis
                dataZamPole.text = gameToShow?.get(0)?.dataZamowienia.toString()
                dataDodPole.text = gameToShow?.get(0)?.dataDodania.toString()
                kosztPole.text = gameToShow?.get(0)?.koszt
                scdPole.text = gameToShow?.get(0)?.SCD
                eanPole.text = gameToShow?.get(0)?.kodEAN_UPC
                idBggPole.text = gameToShow?.get(0)?.idBGG.toString()
                kodProdPole.text = gameToShow?.get(0)?.kodProduktu
                rankingPole.text = gameToShow?.get(0)?.pozycjaAktualna.toString()
                wersjaPole.text = gameToShow?.get(0)?.wersja
                komentarzPole.text = gameToShow?.get(0)?.komentarz
                zdjeciePole.text = gameToShow?.get(0)?.miniaturka
                if (gameToShow?.get(0)?.lokalizacja != null && gameToShow?.get(0)?.lokalizacja != "") {
                    staraLok = gameToShow?.get(0)?.lokalizacja!!
                }

                adapter = RecyclerProjektantSzczegoly(gameToShow?.get(0)?.nazwiskaProjektantow)
                recycler_view_proj_dodDet.adapter = adapter
                recycler_view_proj_dodDet.layoutManager = LinearLayoutManager(this)
                recycler_view_proj_dodDet.setHasFixedSize(true)

                adapter2 = RecyclerArtystaSzczegoly(gameToShow?.get(0)?.nazwiskaArtystow)
                recycler_view_art_dodDet.adapter = adapter2
                recycler_view_art_dodDet.layoutManager = LinearLayoutManager(this)
                recycler_view_art_dodDet.setHasFixedSize(true)

                adapter3 = RecyclerDodatkiFind(gameToShow?.get(0)?.dodatki)
                recycler_edit_dodatki.adapter = adapter3
                recycler_edit_dodatki.layoutManager = LinearLayoutManager(this)
                recycler_edit_dodatki.setHasFixedSize(true)

                var ilistaA = znajdzLokacje()
                var itemList: MutableList<String> = ArrayList()
                if (ilistaA != null) {
                    for (i in 0 until ilistaA?.size) {
                        Log.i("pomocy-141249-ilista", "ILISTA: ${ilistaA.get(i)}")
                        itemList.add(ilistaA.get(i))
                    }
                }
                if (itemList == null) {
                    itemList.add("BRAK DODATKÓW")
                }

                spinner = findViewById(R.id.spinner_lokalizacje)
                arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, itemList)
                spinner?.adapter = arrayAdapter
                spinner?.onItemSelectedListener = this
                Log.i("pomocy-141249", "SPINNER DET: ${gameToShow?.get(0)?.lokalizacja} oraz ${itemList.size}")
                if (gameToShow?.get(0)?.lokalizacja != "" && itemList != null) {
                    spinner?.setSelection(itemList.indexOf(gameToShow?.get(0)?.lokalizacja))
                }


            }
            catch (e: Error) {

            }

        }

        addButton.setOnClickListener {
            Log.i("pomocy-141249", "DO UPD: $tytulDoZapamietania ${tytulPole.text.toString()}")
            var g = Game(
                tytul = tytulPole.text.toString(),
                oryginalnyTytul = tytulOrgPole.text.toString(),
                rokWydania = Integer.parseInt(rokPole.text.toString()),
                nazwiskaProjektantow = gameToShow?.get(0)?.nazwiskaProjektantow,
                nazwiskaArtystow = gameToShow?.get(0)?.nazwiskaArtystow,
                dodatki = gameToShow?.get(0)?.dodatki,
                opis = opisPole.text.toString(),
                dataZamowienia = LocalDate.now(),
                dataDodania = LocalDate.now(),
                koszt = kosztPole.text.toString(),
                SCD = scdPole.text.toString(),
                kodEAN_UPC = eanPole.text.toString(),
                idBGG = Integer.parseInt(idBggPole.text.toString()).toLong(),
                kodProduktu = kodProdPole.text.toString(),
                pozycjaAktualna = Integer.parseInt(rankingPole.text.toString()),
                wersja = wersjaPole.text.toString(),
                komentarz = komentarzPole.text.toString(),
                miniaturka = zdjeciePole.text.toString(),
                histPozycja = HistorycznaPozycja(LocalDate.now(), Integer.parseInt(rankingPole.text.toString())),
                lokalizacja = lokGry
            )
            tytulPole.text = ""
            tytulOrgPole.text = ""
            rokPole.text = ""
            opisPole.text = ""
            dataZamPole.text = ""
            dataDodPole.text = ""
            kosztPole.text = ""
            scdPole.text = ""
            eanPole.text = ""
            idBggPole.text = ""
            kodProdPole.text = ""
            rankingPole.text = ""
            wersjaPole.text = ""
            komentarzPole.text = ""
            zdjeciePole.text = ""

            if (gameToShow?.get(0)?.lokalizacja == "") {
                db.insertLokForGame(g.tytul, lokGry)
            }
            Log.i("pomocy-141249-lokacje", "$tytulDoZapamietania ${staraLok} $lokGry")
            db.updateGame(tytulDoZapamietania, g)
            db.updateLok(tytulDoZapamietania, staraLok, lokGry)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

        rankingPole.setOnClickListener{
            if (gameToShow?.get(0)?.pozycjaAktualna != 0) {
                val intent = Intent(this, HistoriaRankinguGry::class.java)
                intent.putExtra("game-tite-rank", gameToShow?.get(0)?.tytul)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Nie można wyświetlić historii dodatku",
                    Toast.LENGTH_SHORT).show()
            }
        }


    }


    fun insertProj(view: View) {
        var newItem: String = "nowy projektant"
        val desDialog = LayoutInflater.from(this).inflate(R.layout.dodaj_projektanta, null)
        val desBuilder = AlertDialog.Builder(this)
            .setView(desDialog)
            .setTitle("Nazwa projektanta")
        val desDialogAlert = desBuilder.show()
        desDialog.zaakceptujProjektant.setOnClickListener {
            desDialogAlert.dismiss()
            val name = desDialog.dodawanieProjektantName.text.toString()
            if (name != "") {
                newItem = name
            }

            val index: Int = adapter.itemCount
            Log.i("pomocy-141249-45", "P: $index ${gameToShow?.get(0)?.nazwiskaProjektantow!!}")
            gameToShow?.get(0)?.nazwiskaProjektantow!!.add(index, newItem)
            Log.i("pomocy-141249-45", "P: ${gameToShow?.get(0)?.nazwiskaProjektantow!!}")
            adapter.notifyDataSetChanged()
        }

    }

    fun insertArt(view: View) {
        var newItem: String = "nowy artysta"
        val artDialog = LayoutInflater.from(this).inflate(R.layout.dodaj_artyste, null)
        val artBuilder = AlertDialog.Builder(this)
            .setView(artDialog)
            .setTitle("Nazwa artysty")
        val artAlertDialog = artBuilder.show()
        artDialog.zaakceptujArtyste.setOnClickListener {
            artAlertDialog.dismiss()
            val name = artDialog.dodawanieArtystaName.text.toString()
            if (name != "") {
                    newItem = name
                }

            val index: Int = adapter2.itemCount
            Log.i("pomocy-141249-45", "P: $index ${gameToShow?.get(0)?.nazwiskaArtystow!!}")
            gameToShow?.get(0)?.nazwiskaArtystow!!.add(index, name)
            Log.i("pomocy-141249-45", "P: ${gameToShow?.get(0)?.nazwiskaArtystow!!}")
            //adapter2.notifyItemInserted(index)
            adapter2.notifyDataSetChanged()
        }

    }

    fun znajdzLokacje() : ArrayList<String>? {
        val res = db.findLokalizacje()
        return res
    }

    fun zmienArtystow(view: View) {
        db.zmianaArtDet(gameToShow?.get(0))
    }

    fun zmieProjektantow(view: View) {
        db.zmianaDesDet(gameToShow?.get(0))
    }

    fun addNewGame(g: Game) {
        db.addGameFull(g)
        db.addHistPoz(g)
    }

    fun delKnownGame(g: Game?) {
        db.deleteGame(g)
    }

    fun changeArtystow(g: Game) {
        db.zmianaArtDet(g)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var items: String = parent?.getItemAtPosition(position) as String
        poz = position
        lokGry = items
        Log.i("pomocy-141249-2", "Lista details: $items")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i("pomocy-141249-2", "nic")
    }
}