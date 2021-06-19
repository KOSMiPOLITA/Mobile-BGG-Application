package com.example.zad2_141249

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_ekran_bgg.*
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Error
import java.net.MalformedURLException
import java.net.URL
import java.time.LocalDate
import javax.xml.parsers.DocumentBuilderFactory

class EkranBgg : AppCompatActivity(), RecyclerAdapter.OnItemClickListener {


    private lateinit var adapter: RecyclerBGGImport
    private val wersja = WersjaDB().zwrocWersje()
    val db = MyDBHandler(this,"db", null, wersja)

    private var urlConst = "https://www.boardgamegeek.com/xmlapi2/collection?username="
    private var actualURL = ""

    private var gameToEdit:MutableList<GraImportowana>? = null
    private var gamesHistory:MutableList<GraIdZRank>? = null

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
                val fos = FileOutputStream("$testDirectory/gamesImported.xml")
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

    private inner class BGGbyIdAktualizator: AsyncTask<String, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

        override fun doInBackground(vararg params: String?): String {
            Log.i("pomocy-141249", "doInBackground - aktualizacja")
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
                val fos = FileOutputStream("$testDirectory/gamesHistory.xml")
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

    fun downloadAktualizacja() {
        val cd = BGGbyIdAktualizator()
        cd.execute().get()
    }

    fun loadData() {
        gameToEdit = mutableListOf()
        Log.i("pomocy-141249", "Start loadData()")

        val filename = "gamesImported.xml"
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
                        var id_gry = elem.getAttribute("objectid").toLong()
                        val children = elem.childNodes

                        var currentCategory:MutableList<GraImportowana>? = null
                        var tytul: String? = null
                        var miniaturka: String?  = null

                        for (j in 0 until children.length) {
                            val node = children.item(j)
                            if (node is Element) {
                                Log.i("pomocy-141249", "${node.getAttribute("value")}")
                                currentCategory = gameToEdit
                                when (node.nodeName) {
                                    "name" -> {
                                        tytul = node.textContent
                                    }

                                    "thumbnail" -> {
                                        miniaturka = node.textContent
                                    }
                                }
                            }
                        }

                        if(currentCategory != null && tytul != null && miniaturka != null) {
                            Log.i("pomocy-141249-44", "POBIERANIE $id_gry $tytul")
                            val g = GraImportowana(id_gry, tytul, miniaturka)
                            currentCategory.add(g)
                        }
                    }
                }
            }
        }
        Log.i("pomocy-141249-445", "KONIEC POBRANYCH: ${gameToEdit?.size}")
    }

    fun AktualizujHist() {
        gamesHistory = mutableListOf()
        Log.i("pomocy-141249", "Start loadData()")

        val filename = "gamesHistory.xml"
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

                        var currentCategory:MutableList<GraIdZRank>? = null
                        var pozycjaAktualna: Int? = null
                        var histPozycja: HistorycznaPozycja? = null

                        for (j in 0 until children.length) {
                            val node = children.item(j)
                            if (node is Element) {
                                Log.i("pomocy-141249", "${node.getAttribute("value")}")
                                currentCategory = gamesHistory
                                when (node.nodeName) {
                                    "statistics" -> {
                                        val ranking: NodeList = xmlDoc.getElementsByTagName("rank")
                                        for (r in 0 until ranking.length) {
                                            val iNode: Node = ranking.item(r)
                                            if (iNode.nodeType == Node.ELEMENT_NODE) {
                                                val e = iNode as Element
                                                Log.i("pomocy-141249-poz", "POZ: ${e.getAttribute("value")}")
                                                if (e.getAttribute("name") == "boardgame") {
                                                    try {
                                                        if (e.getAttribute("value") != "Not Ranked") {
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

                                    }

                                }
                            }
                        }
                        val tytul = db.findTitleById(id_gry)
                        Log.i("pomocy-141249-akt", "AKTUALIZOWANIE $id_gry $pozycjaAktualna $tytul")
                        db.updateAktualnaPozycja(tytul, pozycjaAktualna)
                    }
                }
            }
        }
        Log.i("pomocy-141249-akt", "KONIEC AKTUALIZOWANIA: ${gamesHistory?.size}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ekran_bgg)

        val textInfo = findViewById<TextView>(R.id.informacjaPrzyimporcie)

        val buttonImport = findViewById<Button>(R.id.buttonImportujKolekcje)
        val buttonAktualizuj = findViewById<Button>(R.id.buttonAktualizujHist)

        buttonAktualizuj.setOnClickListener {
            var listaGierWBazie = db.findAllIdOfGames()
            if (listaGierWBazie != null && listaGierWBazie?.size != 0) {
                for (idX in listaGierWBazie) {
                    var urlId = "https://www.boardgamegeek.com/xmlapi2/thing?id=$idX&stats=1"
                    actualURL = urlId
                    downloadAktualizacja()
                    AktualizujHist()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            Toast.makeText(applicationContext, "Zauktualizowano aktualne pozycje gier", Toast.LENGTH_SHORT).show()
        }

        val buttonWroc = findViewById<Button>(R.id.buttonWrocZBGG)
        buttonWroc.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val poleTytulu = findViewById<EditText>(R.id.textImportujBGG)

        buttonImport.setOnClickListener {
            if (poleTytulu.text.toString() != "" && poleTytulu.text.toString() != null) {
                actualURL = urlConst + poleTytulu.text.toString()
                Log.i("pomocy-141249-445", "$actualURL")
                Log.i("pomocy-141249-445", "ROZMIAR POBRANYCH: ${gameToEdit?.size}")
                downloadData()
                loadData()
                Log.i("pomocy-141249-445", "ROZMIAR POBRANYCH: ${gameToEdit?.size}")
                try {
                    if (gameToEdit != null && gameToEdit?.size != 0) {
                        textInfo.text = "kliknij w grę, aby zimportować wskazany tytuł"
                        adapter = RecyclerBGGImport(gameToEdit, this)
                        Log.i("pomocy-141249-445", "PRzyPISANIE ADAPTERA")
                        recycler_bgg_list.adapter = adapter
                        recycler_bgg_list.layoutManager = LinearLayoutManager(this)
                        recycler_bgg_list.setHasFixedSize(true)
                    }
                    else if (gameToEdit?.size == 0){
                        Toast.makeText(this, "Nie odnaleziono użytkownika: " +
                                "${poleTytulu.text.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: Error) {

                }
            }
        }

    }

    override fun onItemClick(position: Int) {
        val cliecedItem: GraImportowana? = gameToEdit?.get(position)
        //Toast.makeText(this, "KLIKNIĘTO: ${cliecedItem?.tytul}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, FindResult::class.java)
        //adapter.notifyItemChanged(position)
        intent.putExtra("gra_nazwa", cliecedItem?.tytul)
        intent.putExtra("BGG-ID", cliecedItem?.idBBG)
        intent.putExtra("przejscie",1)
        startActivity(intent)
    }
}