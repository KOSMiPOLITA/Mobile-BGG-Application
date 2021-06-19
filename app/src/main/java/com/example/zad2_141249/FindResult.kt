package com.example.zad2_141249

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.TextValueSanitizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_find_result.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.recycler_view
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
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.random.Random

class FindResult : AppCompatActivity() {
    private val wersja = WersjaDB().zwrocWersje()
    private val db = MyDBHandler(this,"db", null,wersja)

    var gameToEdit:MutableList<Game>? = null

    private var urlConst = "https://www.boardgamegeek.com/xmlapi2/thing?id="
    private var actualURL = ""


    private inner class BGGbyIdDownloader: AsyncTask<String, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

        override fun doInBackground(vararg params: String?): String {
            Log.i("pomocy-141249", "doInBackground - start")
            try {
                val url = URL(actualURL)
                val connection = url.openConnection()
                connection.connect()
                Log.i("pomocy-141249", "doInBackground - connection")
                val lenghtOfFile = connection.contentLength
                val isStream = url.openStream()
                val testDirectory = File("$filesDir/XML")
                if (!testDirectory.exists()) testDirectory.mkdir()
                Log.i("pomocy-141249", "doInBackground - if test directory")
                val fos = FileOutputStream("$testDirectory/games.xml")
                val data = ByteArray(1024)
                var count = 0
                var total: Long = 0
                var progress = 0
                count = isStream.read(data)
                while (count != -1) {
                    total += count.toLong()
                    val progress_temp = total.toInt() * 100/ lenghtOfFile
                    if (progress_temp % 10 == 0 && progress != progress_temp) {
                        progress = progress_temp
                    }
                    fos.write(data,0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()
            }
            catch (e: MalformedURLException) {
                Log.i("pomocy-141249","$e")
                return "Malformed URL"
            }

            catch (e: FileNotFoundException) {
                Log.i("pomocy-141249","$e")
                return "File not found"
            }

            catch (e: IOException) {
                Log.i("pomocy-141249","$e")
                return "IO Exception"
            }

            return "success"
        }

    }
    fun downloadData() {
        Log.i("pomocy-141249", "Start downloadData")
        val cd=BGGbyIdDownloader()
        cd.execute().get()
    }

    fun refresh() {
        downloadData()
    }

    fun loadData() {
        gameToEdit = mutableListOf()
        Log.i("pomocy-141249", "Start loadData()")

        val filename = "games.xml"
        val path = filesDir
        val inDir = File(path, "XML")

        if (inDir.exists()) {

            Log.i("pomocy-141249", "After inDir")
            val file = File(inDir, filename)
            if (file.exists()) {
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
                xmlDoc.documentElement.normalize()
                Log.i("pomocy-141249", "Document Normalization")
                val items: NodeList = xmlDoc.getElementsByTagName("item")

                for (i in 0 until items.length) {
                    val itemNode: Node = items.item(i)
                    if (itemNode.nodeType == Node.ELEMENT_NODE) {
                        val elem = itemNode as Element
                        var id_gry = elem.getAttribute("id").toLong()
                        val children = elem.childNodes

                        var currentCategory:MutableList<Game>? = null
                        var tytul: String? = null
                        var oryginalnyTytul: String? = null
                        var rokWydania: Int? = null
                        var nazwiskaProjektantow: ArrayList<String> = ArrayList()
                        var nazwiskaArtystow: ArrayList<String> = ArrayList()
                        var dodatki: ArrayList<DodatkiDoGry> = ArrayList()
                        var opis: String?  = null
                        var pozycjaAktualna: Int? = null
                        var wersja: String?  = null
                        var miniaturka: String?  = null
                        var histPozycja: HistorycznaPozycja? = null

                        for (j in 0 until children.length) {
                            val node = children.item(j)
                            if (node is Element) {
                                Log.i("pomocy-141249", "${node.getAttribute("value")}")
                                currentCategory = gameToEdit
                                when (node.nodeName) {
                                    "name" -> {
                                        if (node.getAttribute("type") == "primary") {
                                            oryginalnyTytul = node.getAttribute("value")
                                        }
                                        else {
                                            if (tytul == null) {
                                                tytul = node.getAttribute("value")
                                            }
                                        }
                                    }

                                    "yearpublished" -> {
                                        Log.i("pomocy-141249-44", "rok ${node.getAttribute("value")}")
                                        try {
                                            if (node.getAttribute("value") != "") {
                                                if (node.getAttribute("value") != null && node.getAttribute("value") != "") {
                                                rokWydania = node.getAttribute("value").toInt()}
                                            }
                                            else {
                                                rokWydania = null
                                            }
                                        }

                                        catch(e: Error) {
                                            rokWydania = null
                                        }

                                    }

                                    "link" -> {
                                        if (node.getAttribute("type") == "boardgamedesigner") {
                                            nazwiskaProjektantow.add(node.getAttribute("value"))
                                        }
                                        if (node.getAttribute("type") == "boardgameartist") {
                                            nazwiskaArtystow.add(node.getAttribute("value"))
                                        }
                                        if (node.getAttribute("type") == "boardgameexpansion") {
                                            var id = node.getAttribute("id")
                                            var name = node.getAttribute("value")
                                            if (id != null && name != null) {
                                                dodatki.add(DodatkiDoGry(tytul = name.toString(), idBGG = id.toLong()))
                                            }
                                        }
                                    }

                                    "statistics" -> {
                                        val ranking: NodeList = xmlDoc.getElementsByTagName("rank")
                                        for (r in 0 until ranking.length) {
                                            val iNode: Node = ranking.item(r)
                                            if (iNode.nodeType == Node.ELEMENT_NODE) {
                                                val e = iNode as Element
                                                Log.i("pomocy-141249-poz", "POZ: ${e.getAttribute("value")}")
                                                if (e.getAttribute("name") == "boardgame") {
                                                    try {
                                                        if (e.getAttribute("value") != "Not Ranked" && e.getAttribute("value") != null) {
                                                            Log.i("pomocy-141249-poz", "${e.getAttribute("value")}")
                                                            pozycjaAktualna = e.getAttribute("value").toInt()
                                                        }
                                                        else {
                                                            pozycjaAktualna = 0
                                                        }

                                                    }
                                                    catch(e: Error) {
                                                        pozycjaAktualna = -1
                                                    }
                                                    histPozycja = pozycjaAktualna?.let {
                                                        HistorycznaPozycja(LocalDate.now(),
                                                            it
                                                        )
                                                    }

                                                }
                                            }
                                        }
                                        if (pozycjaAktualna != null && pozycjaAktualna != 0) {
                                            wersja = "Podstawowa"
                                        }
                                        else {
                                            wersja = "Dodatek"
                                        }
                                    }


                                    "thumbnail" -> {
                                        miniaturka = node.textContent
                                    }

                                    "description" -> {
                                        opis = node.textContent
                                    }

                                    else -> {
                                        //Log.i("pomocy-141249-44", "ELSE: ${node.nodeName}")
                                    }
                                }

                            }

                        }
                        if (tytul == null) {
                            tytul = oryginalnyTytul
                        }
                        Log.i("pomocy-141249-44", "ify w load()")
                        Log.i("pomocy-141249-44", "$tytul $pozycjaAktualna $rokWydania")
                        if(currentCategory != null && tytul != null && rokWydania != null && id_gry != null && opis != null && miniaturka != null
                            && oryginalnyTytul != null && nazwiskaArtystow != null && nazwiskaProjektantow != null && pozycjaAktualna != null
                            && wersja != null && histPozycja != null) {
                            Log.i("pomocy-141249-44", "Dluga $id_gry $tytul")
                            val g = Game(id_BGG = id_gry, tytul = tytul, oryginalnyTytul = oryginalnyTytul, rokWydania = rokWydania, opis = opis,
                                nazwiskaProjektantow = nazwiskaProjektantow, nazwiskaArtystow = nazwiskaArtystow, dodatki = dodatki, pozycjaAktualna = pozycjaAktualna,
                                miniaturka = miniaturka, wersja = wersja, histPozycja = histPozycja
                            )
                            currentCategory.add(g)
                        }

                        else if(currentCategory != null && tytul != null && rokWydania != null && id_gry != null) {
                            Log.i("pomocy-141249-44", "Krotka $id_gry $tytul")
                            val g = Game(id_BGG = id_gry, tytul = tytul, rokWydania = rokWydania, miniaturka = "", opis = "", pozycjaAktualna = 0)
                            currentCategory.add(g)
                        }

                        else {
                            Log.i("pomocy-141249-44", "$tytul $id_gry $miniaturka")
                        }

                    }
                }
            }
        }
    }

    fun showData() {
        Log.i("pomocy-141249", "SHOW DATA")
    }

    private lateinit var adapter: RecyclerProjektantDodawanie
    private lateinit var adapter2: RecyclerArtystaDodawanie
    private lateinit var adapter3: RecyclerDodatkiFind

    private var przejscie = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_result)

        val addButton = findViewById<Button>(R.id.addZatwierdz)

        val tytulPole = findViewById<TextView>(R.id.tytulEdit)
        val tytulOrgPole = findViewById<TextView>(R.id.tytulOrgEdit)
        val rokPole = findViewById<TextView>(R.id.rokWydaniaEdit)
        val opisPole = findViewById<TextView>(R.id.opisGryEdit)
        val dataZamPole = findViewById<TextView>(R.id.dataZamowieniaEdit)
        val dataDodPole = findViewById<TextView>(R.id.dataDodaniaGryEdit)
        val kosztPole = findViewById<TextView>(R.id.kosztZakupuEdit)
        val scdPole = findViewById<TextView>(R.id.scdGryEdit)
        val eanPole = findViewById<TextView>(R.id.kodEanGryEdit)
        val idBggPole = findViewById<TextView>(R.id.idBggGryNotEdit)
        val kodProdPole = findViewById<TextView>(R.id.kodProdukcyjnyGryEdit)
        val rankingPole = findViewById<TextView>(R.id.aktPozycjaGryNotEdit)
        val wersjaPole = findViewById<TextView>(R.id.wersjaGryEdit)
        val komentarzPole = findViewById<TextView>(R.id.komentarzGryEdit)
        val zdjeciePole = findViewById<TextView>(R.id.zdjecieGryEdit)
        Log.i("pomocy-141249-23", "ify w load()")
        val idBGG = intent.getLongExtra("BGG-ID", 0)
        val tytul_wyszukiwanej = intent.getStringExtra("gra_nazwa")
        przejscie = intent.getIntExtra("przejscie", 0)
        if (idBGG != 0.toLong()) {

            Log.i("pomocy-141249-23", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
            actualURL = (urlConst + idBGG.toString() + "&stats=1")
            Log.i("pomocy-141249-poz", actualURL)
            downloadData()
            loadData()
            Log.i("pomocy-141249-44", "ify w load()")
            try {
                tytulPole.text = tytul_wyszukiwanej
                tytulOrgPole.text = gameToEdit?.get(0)?.oryginalnyTytul
                rokPole.text = gameToEdit?.get(0)?.rokWydania.toString()
                opisPole.text = gameToEdit?.get(0)?.opis
                dataZamPole.text = gameToEdit?.get(0)?.dataZamowienia.toString()
                dataDodPole.text = gameToEdit?.get(0)?.dataDodania.toString()
                kosztPole.text = gameToEdit?.get(0)?.koszt
                scdPole.text = gameToEdit?.get(0)?.SCD
                eanPole.text = gameToEdit?.get(0)?.kodEAN_UPC
                idBggPole.text = gameToEdit?.get(0)?.idBGG.toString()
                kodProdPole.text = gameToEdit?.get(0)?.kodProduktu
                rankingPole.text = gameToEdit?.get(0)?.pozycjaAktualna.toString()
                wersjaPole.text = gameToEdit?.get(0)?.wersja
                komentarzPole.text = gameToEdit?.get(0)?.komentarz
                zdjeciePole.text = gameToEdit?.get(0)?.miniaturka

                adapter = RecyclerProjektantDodawanie(gameToEdit?.get(0)?.nazwiskaProjektantow)
                recycler_view_proj_dod.adapter = adapter
                recycler_view_proj_dod.layoutManager = LinearLayoutManager(this)
                recycler_view_proj_dod.setHasFixedSize(true)

                adapter2 = RecyclerArtystaDodawanie(gameToEdit?.get(0)?.nazwiskaArtystow)
                recycler_view_art_dod.adapter = adapter2
                recycler_view_art_dod.layoutManager = LinearLayoutManager(this)
                recycler_view_art_dod.setHasFixedSize(true)

                adapter3 = RecyclerDodatkiFind(gameToEdit?.get(0)?.dodatki)

                Log.i("pomocy-141249-dodatki-find", "DOD SIZE ${gameToEdit?.get(0)?.dodatki?.size}")
                recycler_find_dodatki.adapter = adapter3
                recycler_find_dodatki.layoutManager = LinearLayoutManager(this)
                recycler_find_dodatki.setHasFixedSize(true)

            }
            catch (e: Error) {

            }

            addButton.setOnClickListener{
                var g = Game(
                    tytul = tytulPole.text.toString(),
                    oryginalnyTytul = tytulOrgPole.text.toString(),
                    rokWydania = Integer.parseInt(rokPole.text.toString()),
                    nazwiskaProjektantow = gameToEdit?.get(0)?.nazwiskaProjektantow,
                    nazwiskaArtystow = gameToEdit?.get(0)?.nazwiskaArtystow,
                    dodatki = gameToEdit?.get(0)?.dodatki,
                    opis = opisPole.text.toString(),
                    dataZamowienia = LocalDate.now(),
                    dataDodania = LocalDate.now(),
                    koszt = kosztPole.text.toString(),
                    SCD = scdPole.text.toString(),
                    kodEAN_UPC = eanPole.text.toString(),
                    idBGG = idBGG,
                    kodProduktu = kodProdPole.text.toString(),
                    pozycjaAktualna = Integer.parseInt(rankingPole.text.toString()),
                    wersja = wersjaPole.text.toString(),
                    komentarz = komentarzPole.text.toString(),
                    miniaturka = zdjeciePole.text.toString(),
                    histPozycja = HistorycznaPozycja(LocalDate.now(), Integer.parseInt(rankingPole.text.toString())),
                    lokalizacja = ""
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

                Log.i("pomocy-141249-dodatki-find", "DOD SIZE ${g.dodatki?.size}")
                addNewGame(g)
                if (przejscie == 0) {
                    val intent = Intent(this, DodawanieGry::class.java)
                    startActivity(intent)
                }
                else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

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
            Log.i("pomocy-141249-45", "P: $index ${gameToEdit?.get(0)?.nazwiskaProjektantow!!}")
            gameToEdit?.get(0)?.nazwiskaProjektantow!!.add(index, newItem)
            Log.i("pomocy-141249-45", "P: ${gameToEdit?.get(0)?.nazwiskaProjektantow!!}")
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
            Log.i("pomocy-141249-45", "P: $index ${gameToEdit?.get(0)?.nazwiskaArtystow!!}")
            gameToEdit?.get(0)?.nazwiskaArtystow!!.add(index, newItem)
            Log.i("pomocy-141249-45", "P: ${gameToEdit?.get(0)?.nazwiskaArtystow!!}")
            //adapter2.notifyItemInserted(index)
            adapter2.notifyDataSetChanged()
        }

    }


    fun addNewGame(g: Game) {
        db.addGameFull(g)
        db.addHistPoz(g)
    }

    fun zmienArtystow(view: View) {
        db.zmianaArtDet(gameToEdit?.get(0))
    }

    fun zmieProjektantow(view: View) {
        db.zmianaArtDet(gameToEdit?.get(0))
    }

}