package com.example.zad2_141249

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import org.w3c.dom.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

class DodawanieGry : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var findGame:MutableList<Game>? = null
    private var spinner:Spinner? = null
    private var arrayAdapter:ArrayAdapter<String>? =  null

    private var poz: Int = 0
    private var ch_game: String = ""

    private var urlConst = "https://www.boardgamegeek.com/xmlapi2/search?query="
    private var actualURL = ""

    private inner class GamesFinder: AsyncTask<String, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

        override fun doInBackground(vararg params: String?): String {
            Log.i("pomocy-141249-2", "doInBackground - start")
            try {
                val url = URL(actualURL)
                val connection = url.openConnection()
                connection.connect()
                Log.i("pomocy-141249-2", "doInBackground - connection")
                val lenghtOfFile = connection.contentLength
                val isStream = url.openStream()
                val testDirectory = File("$filesDir/XML")
                if (!testDirectory.exists()) testDirectory.mkdir()
                Log.i("pomocy-141249-2", "doInBackground - if test directory")
                val fos = FileOutputStream("$testDirectory/find_games.xml")
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
                Log.i("pomocy-141249-2","$e")
                return "Malformed URL"
            }

            catch (e: FileNotFoundException) {
                Log.i("pomocy-141249-2","$e")
                return "File not found"
            }

            catch (e: IOException) {
                Log.i("pomocy-141249-2","$e")
                return "IO Exception"
            }

            Log.i("pomocy-141249-2","Close doInBackGroung")
            return "success"
        }

    }

    fun downloadData() {
        Log.i("pomocy-141249-2", "Start downloadData")
        val cd=GamesFinder()
        cd.execute().get();
    }

    fun refresh() {
        downloadData()
    }

    fun loadData() {
        findGame = mutableListOf()
        Log.i("pomocy-141249-2", "Start loadData()")

        val filename = "find_games.xml"
        val path = filesDir
        val inDir = File(path, "XML")

        if (inDir.exists()) {

            Log.i("pomocy-141249-2", "dodawanie")

            val file = File(inDir, filename)
            if (file.exists()) {
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
                xmlDoc.documentElement.normalize()
                Log.i("pomocy-141249-2", "Document Normalization")

                val items: NodeList = xmlDoc.getElementsByTagName("item")

                for (i in 0 until items.length) {
                    val itemNode: Node = items.item(i)
                    if (itemNode.nodeType == Node.ELEMENT_NODE) {
                        val elem = itemNode as Element

                        var id_gry = elem.getAttribute("id").toLong()
                        val children = elem.childNodes

                        var currentCategory:MutableList<Game>? = null
                        var tytul: String? = null
                        for (j in 0 until children.length) {
                            val node = children.item(j)
                            if (node is Element) {

                                Log.i("pomocy-141249-2", "")
                                currentCategory = findGame
                                when (node.nodeName) {
                                    "name" -> {
                                        tytul = node.getAttribute("value")
                                    }
                                }
                            }
                        }

                        if(currentCategory != null && tytul != null && id_gry != null) {
                            val g = Game(id_BGG = id_gry, tytul = tytul, rokWydania = 0, miniaturka = "", opis = "", pozycjaAktualna = 0)
                            currentCategory.add(g)
                            Log.i("pomocy-141249-2", "LD: $id_gry $tytul")
                        }

                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dodawanie_gry)

        val tytulGry = findViewById<TextView>(R.id.searchTitle)
        val wyszukajButton = findViewById<Button>(R.id.dodajGreButton)

        val buttonWroc = findViewById<Button>(R.id.wrocZDodawanieButton)
        buttonWroc.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonPotwierdz = findViewById<Button>(R.id.zatwierdzGre)
        buttonPotwierdz.setOnClickListener{
            try {
                if (findGame != null) {
                    val intent = Intent(this, FindResult::class.java)
                    var idBGG: Long = 0
                    if (poz < findGame!!.size) {
                        idBGG = findGame?.get(poz)?.idBGG!!
                    }
                    Log.i("pomocy-141249-23", "ID BGG $idBGG $ch_game")
                    intent.putExtra("BGG-ID", idBGG)
                    intent.putExtra("gra_nazwa", ch_game)
                    startActivity(intent)
                }

            }
            catch(e: Error) {
                Toast.makeText(this, "Nie znaleziono podanej gry!", Toast.LENGTH_SHORT).show()
            }
        }

        wyszukajButton.setOnClickListener {
            val title: String = tytulGry.text.toString()
            if (title != "" && title != null) {
                try {
                    actualURL = (urlConst + title + "&type=boardgame")
                    downloadData()
                    loadData()
                    var itemList: MutableList<String> = ArrayList()
                    if (findGame?.get(0)?.tytul != null) {
                        Log.i("pomocy-141249-2", "${findGame?.size}")
                        for (i in 0 until findGame!!.size) {
                            val element = findGame?.get(i)?.tytul.toString()
                            itemList.add(element)
                        }
                        itemList.add("<inna>")
                        spinner = findViewById(R.id.spinnerDodawanieGry)
                        arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, itemList)
                        spinner?.adapter = arrayAdapter
                        spinner?.onItemSelectedListener = this
                    }
                }

                catch (e: IOException) {
                    Toast.makeText(this, "Nie znaleziono podanej gry", Toast.LENGTH_SHORT).show()
                }

            }

        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var items: String = parent?.getItemAtPosition(position) as String
        ch_game = items
        poz = position
        Log.i("pomocy-141249-2", "Lista: $items")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i("pomocy-141249-2", "nic")
    }

}