package com.example.zad2_141249

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Error
import java.time.LocalDate
import kotlin.math.min

class MyDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, version) {

    companion object {
        private val DATABASE_NAME = "gamesDB.db"
        val TABLE_GAMES = "games"
        val TABLE_DESIGNERS = "designers"
        val TABLE_ARTISTS = "artists"
        val TABLE_HIST_POZ = "historyczne_pozycje"
        val TABLE_GAMES_ARTISTS = "games_artists"
        val TABLE_GAMES_DESIGNERS = "games_designers"
        val TABLE_LOK = "lokalizacje"
        val TABLE_LOK_FOR_GAME = "lokalizacje_dla_gier"
        val TABLE_DOD = "dodatki"
        val TABLE_DOD_FOR_GAME = "dodatki_dla_gier"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_GAMES_TABLE = ("CREATE TABLE " + TABLE_GAMES + " (" +
                "tytul TEXT PRIMARY KEY, org_tytul TEXT, rok_wyd INTEGER, opis TEXT, data_zam DATE, " +
                "data_dod DATE, koszt TEXT, SCD TEXT, EAN_UPC TEXT, BGGid LONG, kod_pro TEXT, " +
                "akt_poz INTEGER, wersja TEXT, komentarz TEXT, zdjecie TEXT)")
        val CREATE_DESIGNERS_TABLE = ("CREATE TABLE $TABLE_DESIGNERS (Id INTEGER PRIMARY KEY," +
                "imie_nazwisko TEXT)")
        val CREATE_AUTHORS_TABLE = ("CREATE TABLE $TABLE_ARTISTS (Id INTEGER PRIMARY KEY," +
                "imie_nazwisko TEXT)")
        val CREATE_HIST_TABLE = ("CREATE TABLE $TABLE_HIST_POZ (Tytul TEXT, Data DATE," +
                "Ranking LONG, PRIMARY KEY (Tytul, Data, Ranking))")
        val CREATE_LOK_TABLE = ("CREATE TABLE $TABLE_LOK (Lokalizacja TEXT PRIMARY KEY, Ilosc INTEGER)")
        val CREATE_DOD_TABLE = ("CREATE TABLE $TABLE_DOD (Nazwa TEXT PRIMARY KEY, Id LONG)")
        val CREATE_DOD_FOR_GAME_TABLE = ("CREATE TABLE $TABLE_DOD_FOR_GAME (Gra TEXT, Dodatek TEXT," +
                "PRIMARY KEY(Gra, Dodatek))")
        val CREATE_AUT_FOR_GAME_TABLE = ("CREATE TABLE $TABLE_GAMES_ARTISTS (Gra TEXT, Artysta TEXT," +
                "PRIMARY KEY(Gra, Artysta))")
        val CREATE_DES_FOR_GAME_TABLE = ("CREATE TABLE $TABLE_GAMES_DESIGNERS (Gra TEXT, Projektant TEXT," +
                "PRIMARY KEY(Gra, Projektant))")
        val CREATE_LOK_FOR_GAME_TABLE = ("create table lokalizacje_dla_gier (gra TEXT PRIMARY KEY, lokalizacja TEXT, " +
                "FOREIGN KEY(lokalizacja) references lokalizacje(lokalizacja))")
        db.execSQL(CREATE_GAMES_TABLE)
        db.execSQL(CREATE_DESIGNERS_TABLE)
        db.execSQL(CREATE_AUTHORS_TABLE)
        db.execSQL(CREATE_HIST_TABLE)
        db.execSQL(CREATE_LOK_TABLE)
        db.execSQL(CREATE_DOD_TABLE)
        db.execSQL(CREATE_DOD_FOR_GAME_TABLE)
        db.execSQL(CREATE_AUT_FOR_GAME_TABLE)
        db.execSQL(CREATE_DES_FOR_GAME_TABLE)
        db.execSQL(CREATE_LOK_FOR_GAME_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DESIGNERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ARTISTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HIST_POZ")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOK")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DOD")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DOD_FOR_GAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES_DESIGNERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES_ARTISTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOK_FOR_GAME")
        onCreate(db)
    }

    fun addGameFull(game: Game) {
        var values = ContentValues()
        values.put("tytul", game.tytul)
        values.put("org_tytul", game.oryginalnyTytul)
        values.put("rok_wyd", game.rokWydania)
        values.put("opis", game.opis)
        values.put("akt_poz", game.pozycjaAktualna)
        values.put("zdjecie", game.miniaturka)
        values.put("BGGid", game.idBGG)
        values.put("wersja", game.wersja)
        values.put("data_zam", game.dataZamowienia.toString())
        values.put("data_dod", game.dataDodania.toString())
        values.put("koszt", game.koszt)
        values.put("scd", game.SCD)
        values.put("ean_upc", game.kodEAN_UPC)
        values.put("kod_pro", game.kodProduktu)
        values.put("komentarz", game.komentarz)
        val db = this.writableDatabase
        db.insert(TABLE_GAMES, null, values)

        values = ContentValues()
        for (des in game.nazwiskaProjektantow!!) {
            values.put("imie_nazwisko", des)
            Log.i("pomocy-141249-db-list", "$des")
            db.insert(TABLE_DESIGNERS, null, values)
        }

        values = ContentValues()
        for (des in game.nazwiskaProjektantow!!) {
            values.put("Gra", game.tytul)
            values.put("Projektant", des)
            Log.i("pomocy-141249-db-list", "$des")
            db.insert(TABLE_GAMES_DESIGNERS, null, values)
        }

        values = ContentValues()
        for (art in game.nazwiskaArtystow!!) {
            values.put("imie_nazwisko", art)
            Log.i("pomocy-141249-db-list", "$art")
            db.insert(TABLE_ARTISTS, null, values)
        }

        values = ContentValues()
        for (art in game.nazwiskaArtystow!!) {
            values.put("Gra", game.tytul)
            values.put("Artysta", art)
            Log.i("pomocy-141249-db-list", "$art")
            db.insert(TABLE_GAMES_ARTISTS, null, values)
        }

        values = ContentValues()
        for (dod in game.dodatki!!) {
            values.put("Nazwa", dod.tytul)
            values.put("Id", dod.idBGG)
            db.insert(TABLE_DOD, null, values)
        }

        values = ContentValues()
        for (dod in game.dodatki!!) {
            values.put("Gra",game.tytul )
            values.put("Dodatek", dod.tytul)
            db.insert(TABLE_DOD_FOR_GAME, null, values)
        }

        db.close()
    }

    fun deleteGame(game: Game?) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM TABLE $TABLE_GAMES WHERE tytul = \"${game?.tytul}\"")
        Log.i("pomocy-141249-db-list", "Usunięto ${game?.tytul}")
        db.close()
    }

    fun addGame(game: Game) {
        val values = ContentValues()
        values.put("tytul", game.tytul)
        values.put("rok_wyd", game.rokWydania)
        values.put("opis", game.opis)
        values.put("akt_poz", game.pozycjaAktualna)
        values.put("zdjecie", game.miniaturka)
        val db = this.writableDatabase
        db.insert(TABLE_GAMES, null, values)
        db.close()
    }

    fun addHistPoz(game: Game) {
        val values = ContentValues()

        values.put("Tytul", game.tytul)
        values.put("Data", game.histPozycja[0].dataPozyskania.toString())
        values.put("Ranking", game.histPozycja[0].pozycja)
        Log.i("pomocy-141249-dbhist", "HIST: ${game.tytul} " +
                "${game.histPozycja[0].dataPozyskania} ${game.histPozycja[0].pozycja}")
        val db = this.writableDatabase
        db.insert(TABLE_HIST_POZ, null, values)
        db.close()
    }

    fun findGames(ord: Int) : MutableList<Game>? {
        //val query = "SELECT * FROM $TABLE_GAMES"
        val query = "SELECT * FROM $TABLE_GAMES WHERE akt_poz <> 0 ORDER BY $ord ASC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var game: Game? = null
        var gameList: MutableList<Game> = ArrayList()

        while (cursor.moveToNext()) {
            val Id = cursor.getLong(9)
            val tytul =  cursor.getString(0)
            val rok = cursor.getInt(2)
            val opis = cursor.getString(3)
            val pozycja =  cursor.getInt(11)
            val image = cursor.getString(14)
            Log.i("pomocy-141249-dbh", "$Id $tytul $rok $pozycja")
            game = Game(id_BGG = Id, tytul = tytul, rokWydania = rok, opis = opis,
                pozycjaAktualna = pozycja, miniaturka = image)
            Log.i("pomocy-141249-dbh", "Tytuł ${game.tytul}")
            gameList.add(game)
        }
        cursor.close()
        db.close()
        Log.i("pomocy-141249-dbh", "Ilosc pobranych danych ${gameList.size}")
        return gameList
    }

    fun findGame(title: String?) : MutableList<Game>? {
        val warunek = "TYTUL"
        val query = "SELECT * FROM $TABLE_GAMES WHERE $warunek = \"$title\""
        val query_art = "SELECT * FROM $TABLE_GAMES_ARTISTS WHERE Gra = \"$title\""
        val query_des = "SELECT * FROM $TABLE_GAMES_DESIGNERS WHERE Gra = \"$title\""
        val query_dod = "SELECT * FROM $TABLE_DOD_FOR_GAME WHERE Gra = \"$title\""

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)
        val cursor_art = db.rawQuery(query_art, null)
        val cursor_des = db.rawQuery(query_des, null)
        val cursor_dod = db.rawQuery(query_dod, null)

        var artysci: ArrayList<String> = ArrayList()
        var projektanci: ArrayList<String> = ArrayList()
        var dodatki: ArrayList<String> = ArrayList()
        var listaDodatkow: ArrayList<DodatkiDoGry> = ArrayList<DodatkiDoGry>()

        while (cursor_dod.moveToNext()) {
            val data = cursor_dod.getString(1)
            dodatki.add(data)
        }
        cursor_dod.close()

        var d: DodatkiDoGry? = null
        for (i in dodatki) {
            Log.i("pomocy-141249-dbh", "Wyszukiwany dodatek: $i")
            val query_dod_fin = "SELECT * FROM $TABLE_DOD WHERE Nazwa = \"$i\""
            val cursorDOD = db.rawQuery(query_dod_fin, null)
            if (cursorDOD.moveToFirst()) {
                Log.i("pomocy-141249-dbh", "Dodawany dodatek: $i")
                val data = cursorDOD.getString(0)
                val data2 = cursorDOD.getLong(1)
                d = DodatkiDoGry(data, data2)
                listaDodatkow.add(d)
                cursorDOD.close()
            }
        }

        var LOKALIZACJA: String = ""
        val q_lok = "SELECT * FROM $TABLE_LOK_FOR_GAME WHERE Gra = \"$title\""
        val cur_lok = db.rawQuery(q_lok, null)
        if (cur_lok.moveToFirst()) {
            LOKALIZACJA = cur_lok.getString(1)
        }
        cur_lok.close()

        while (cursor_art.moveToNext()) {
            val data = cursor_art.getString(1)
            artysci.add(data)
        }
        cursor_art.close()


        while (cursor_des.moveToNext()) {
            val data = cursor_des.getString(1)
            projektanci.add(data)
        }
        cursor_des.close()

        var game: Game? = null
        var gameList: MutableList<Game> = ArrayList()
        Log.i("pomocy-141249-dbhd", "DBHD")
        if (cursor.moveToNext()) {
            val Id = cursor.getLong(9)
            val tytul = cursor.getString(0)
            val orgTytul = cursor.getString(1)
            val rok = cursor.getInt(2)
            val opis = cursor.getString(3)
            val dataZam = cursor.getString(4)
            val dataDod = cursor.getString(5)
            val koszt = cursor.getString(6)
            val scd = cursor.getString(7)
            val ean = cursor.getString(8)
            val kodPro = cursor.getString(10)
            val pozycja = cursor.getInt(11)
            val wersja = cursor.getString(12)
            val koment = cursor.getString(13)
            val image = cursor.getString(14)
            Log.i("pomocy-141249-dbhd", "$Id $tytul $rok $pozycja")

            game = Game(
                tytul = tytul,
                oryginalnyTytul = orgTytul,
                rokWydania = rok,
                nazwiskaArtystow = artysci,
                nazwiskaProjektantow = projektanci,
                dodatki = listaDodatkow,
                opis = opis,
                dataZamowienia = LocalDate.parse(dataZam),
                dataDodania = LocalDate.parse(dataDod),
                koszt = koszt,
                SCD = scd,
                kodEAN_UPC = ean,
                idBGG = Id,
                kodProduktu = kodPro,
                pozycjaAktualna = pozycja,
                komentarz = koment,
                wersja = wersja,
                miniaturka = image,
                histPozycja = HistorycznaPozycja(
                    LocalDate.now(), pozycja
                ),
                lokalizacja = LOKALIZACJA
            )
            Log.i("pomocy-141249-dbhd", "Tytuł ${game.tytul}")
            gameList.add(game)
        }
        cursor.close()
        db.close()
        Log.i("pomocy-141249-dbhd", "Ilosc pobranych danych ${gameList.size}")
        return gameList

    }

        fun findHistoryOfGame(title: String?) : ArrayList<HistoriaItem>? {
            val warunek = "Tytul"
            val query = "SELECT * FROM $TABLE_HIST_POZ WHERE $warunek = \"$title\" ORDER BY 2 DESC"
            val db = this.writableDatabase
            val cursor = db.rawQuery(query, null)
            var histItem: HistoriaItem? = null
            var histGameList: ArrayList<HistoriaItem> = ArrayList()
            Log.i("pomocy-141249-dbhd", "DBHD HIST")
            while (cursor.moveToNext()) {
                val tytulH =  cursor.getString(0)
                val dataH = cursor.getString(1)
                val rankH = cursor.getInt(2)
                Log.i("pomocy-141249-dbhd", "HIST FIND $tytulH $dataH $rankH")
                histItem = HistoriaItem(tytul = tytulH, data = dataH, ranking = rankH.toString())
                histGameList.add(histItem)
            }
            cursor.close()
            db.close()
            Log.i("pomocy-141249-dbhd", "Ilosc pobranych danych hist ${histGameList.size}")
            return histGameList

    }

    fun findGryZLokacji(lok: String?) : ArrayList<String>? {
        val query = "SELECT * FROM $TABLE_LOK_FOR_GAME WHERE Lokalizacja = \"$lok\" ORDER BY 1 ASC"
        Log.i("pomocy-141249-dbhd", query)
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var tytul: String = ""
        val listaTytulow: ArrayList<String> = ArrayList()
        while (cursor.moveToNext()) {
            tytul = cursor.getString(0)
            Log.i("pomocy-141249-dbhd", "Gra w lokacji ${tytul}")
            listaTytulow.add(tytul)
        }
        cursor.close()
        db.close()
        return listaTytulow

    }

    fun findLokalizacje() : ArrayList<String>? {
        val query = "SELECT * FROM $TABLE_LOK"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var name: String? = null
        var lokList: ArrayList<String> = ArrayList()

        while (cursor.moveToNext()) {
            name = cursor.getString(0)
            Log.i("pomocy-141249-dbh", "nazwa lokalizacji $name")
            lokList.add(name)
        }
        cursor.close()
        db.close()
        Log.i("pomocy-141249-dbh", "Ilosc pobranych danych ${lokList.size}")
        return lokList
    }

    fun zmianaArtDet(game: Game?) {
        val tyt = game?.tytul
        val lista = game?.nazwiskaArtystow
        val db = this.writableDatabase
        val query = "DELETE FROM $TABLE_GAMES_ARTISTS WHERE Gra = \"$tyt\""
        db.execSQL(query)
        if (lista != null) {
            for (i in lista) {
                val q = "INSERT INTO $TABLE_GAMES_ARTISTS VALUES(\"$tyt\", \"$i\")"
                db.execSQL(q)
            }
        }
        db.close()
        //val cursor = db.rawQuery(query, null)
    }

    fun zmianaDesDet(game: Game?) {
        val tyt = game?.tytul
        val lista = game?.nazwiskaProjektantow
        val db = this.writableDatabase
        val query = "DELETE FROM $TABLE_GAMES_DESIGNERS WHERE Gra = \"$tyt\""
        db.execSQL(query)
        if (lista != null) {
            for (i in lista) {
                val q = "INSERT INTO $TABLE_GAMES_DESIGNERS VALUES(\"$tyt\", \"$i\")"
                db.execSQL(q)
            }
        }
        db.close()
        //val cursor = db.rawQuery(query, null)
    }

    fun usunLok(lok: ArrayList<String>?) {
        Log.i("pomocy-141249-dbh1", "USUWANIE USUWANIE")
        Log.i("pomocy-141249-dbh1", "!!!!!!!!!!!!!!!!! ")
        //Log.i("pomocy-141249-dbh1", "Rozmiar listy: ${lok?.size}")
        val db = this.writableDatabase
        var lok_z_grami: ArrayList<String> = ArrayList()
        val query = "SELECT * FROM $TABLE_LOK_FOR_GAME"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val l = cursor.getString(1)
            Log.i("pomocy-141249-dbh1", "nazwa lokalizacji $l")
            lok_z_grami.add(l)
        }
        if (lok_z_grami != null) {
            //val dist_lok_z_grami = lok_z_grami.distinct() as ArrayList<String>
        }
        Log.i("pomocy-141249-dbh1", lok.toString())
        Log.i("pomocy-141249-dbh1", lok_z_grami.toString())
    /*

        cursor.close()
        //Log.i("pomocy-141249-dbh1", "Rozmiar listy z grami: ${lok_z_grami.size}")
        val lok_z_bazy = findLokalizacje()
        //Log.i("pomocy-141249-dbh1", "Rozmiar listy z bazy: ${lok_z_bazy?.size}")


        Log.i("pomocy-141249-dbh1", lok_z_bazy.toString())
        Log.i("pomocy-141249-dbh1", lok_z_grami.toString())


        if (lok != null && lok_z_bazy != null) {
            for (l in lok_z_bazy) {
                if (l in lok_z_grami) {
                    Log.i("pomocy-141249-dbh", "nazwa lokalizacji $l")
                }
                else if (l !in lok  && lok.size != 0) {
                    Log.i("pomocy-141249-dbh", "Usuń lok: $l !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11")

                    try {
                        Log.i("pomocy-141249-dbh", lok.toString())
                        Log.i("pomocy-141249-dbh", lok_z_bazy.toString())
                        Log.i("pomocy-141249-dbh", lok_z_grami.toString())
                        db.execSQL("DELETE FROM $TABLE_LOK WHERE Lokalizacja = \"$l\"")
                    }
                    catch (e: Error) {

                    }
                }
                else if (l !in lok_z_grami  && lok_z_grami.size != 0) {
                    Log.i("pomocy-141249-dbh", "Usuń lok2: $l !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11")
                    try {
                        db.execSQL("DELETE FROM $TABLE_LOK WHERE Lokalizacja = \"$l\"")
                        Log.i("pomocy-141249-dbh", lok.toString())
                        Log.i("pomocy-141249-dbh", lok_z_bazy.toString())
                        Log.i("pomocy-141249-dbh", lok_z_grami.toString())
                    }
                    catch (e: Error) {

                    }
                }
            }
            for (l in lok) {
                if (l !in lok_z_bazy) {
                    db.execSQL("INSERT INTO $TABLE_LOK VALUES(\"$l\")")
                }
            }
        }
        db.close()

*/
        /*

        val query = "DELETE FROM $TABLE_GAMES_DESIGNERS WHERE Gra = \"$tyt\""
        db.execSQL(query)
        if (lista != null) {
            for (i in lista) {
                val q = "INSERT INTO $TABLE_GAMES_DESIGNERS VALUES(\"$tyt\", \"$i\")"
                db.execSQL(q)
            }
        }
        db.close()*/
        //val cursor = db.rawQuery(query, null)
    }

    fun findAllTitlesOfGames() : ArrayList<String>? {
        val query = "SELECT * FROM $TABLE_GAMES"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var name: String? = null
        var gameList: ArrayList<String> = ArrayList()

        while (cursor.moveToNext()) {
            name = cursor.getString(0)
            Log.i("pomocy-141249-dbh", "nazwa gry do usuniecia $name")
            gameList.add(name)
        }
        cursor.close()
        db.close()
        return gameList
    }

    fun findAllIdOfGames() : ArrayList<String>? {
        val query = "SELECT * FROM $TABLE_GAMES WHERE BGGid <> 0"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var name: String? = null
        var gameList: ArrayList<String> = ArrayList()

        while (cursor.moveToNext()) {
            name = cursor.getString(9)
            Log.i("pomocy-141249-dbh", "id znalezionej gry $name")
            gameList.add(name)
        }
        cursor.close()
        db.close()
        return gameList
    }

    fun usunGreZBazy(nazwa: String) {

        val db = this.writableDatabase
        val queryDel = "SELECT * FROM $TABLE_LOK_FOR_GAME WHERE gra = \"$nazwa\""
        val cursor = db.rawQuery(queryDel, null)
        var lok: String? = null
        if (cursor.moveToFirst()) {
            lok = cursor.getString(1)
        }
        cursor.close()

        val query1 = "DELETE FROM $TABLE_GAMES WHERE tytul = \"$nazwa\""
        val query2 = "DELETE FROM $TABLE_HIST_POZ WHERE Tytul = \"$nazwa\""
        val query3 = "DELETE FROM $TABLE_DOD_FOR_GAME WHERE Gra = \"$nazwa\""
        val query4 = "DELETE FROM $TABLE_GAMES_DESIGNERS WHERE Gra = \"$nazwa\""
        val query5 = "DELETE FROM $TABLE_GAMES_ARTISTS WHERE Gra = \"$nazwa\""
        val query6 = "DELETE FROM $TABLE_LOK_FOR_GAME WHERE Gra = \"$nazwa\""
        db.execSQL(query1)
        db.execSQL(query2)
        db.execSQL(query3)
        db.execSQL(query4)
        db.execSQL(query5)
        db.execSQL(query6)


        db.execSQL("UPDATE $TABLE_LOK SET Ilosc = Ilosc - 1 WHERE Lokalizacja = \"$lok\"")
        db.close()
    }

    fun dodajLokalizacje(nazwa: String) {
        val db = this.writableDatabase
        val query = "INSERT INTO $TABLE_LOK VALUES(\"$nazwa\", 0)"
        db.execSQL(query)
        db.close()

    }

    fun updateGame(t: String, g: Game) {
        val db = writableDatabase
        Log.i("pomocy-141249", "update: $t ${g.tytul} ${g.rokWydania}")
        val query = "UPDATE $TABLE_GAMES SET " +
                "tytul = \"${g.tytul}\", " +
                "org_tytul = \"${g.oryginalnyTytul}\", " +
                "rok_wyd = ${g.rokWydania}, " +
                "opis = \"${g.opis}\", " +
                "data_zam = \"${g.dataZamowienia}\", " +
                "data_dod = \"${g.dataDodania}\", " +
                "koszt = \"${g.koszt}\", " +
                "SCD = \"${g.SCD}\", " +
                "EAN_UPC = \"${g.kodEAN_UPC}\", " +
                "kod_pro = \"${g.kodProduktu}\", " +
                "akt_poz = ${g.pozycjaAktualna}, " +
                "wersja = \"${g.wersja}\", " +
                "komentarz = \"${g.komentarz}\", " +
                "zdjecie = \"${g.miniaturka}\"" +
                " where bggid = ${g.idBGG}"
        val query2 = "UPDATE $TABLE_GAMES_ARTISTS SET Gra = \"${g.tytul}\" WHERE Gra = \"$t\""
        val query3 = "UPDATE $TABLE_GAMES_DESIGNERS SET Gra = \"${g.tytul}\" WHERE Gra = \"$t\""
        val query4 = "UPDATE $TABLE_LOK_FOR_GAME SET Gra = \"${g.tytul}\" WHERE Gra = \"$t\""
        val query5 = "UPDATE $TABLE_DOD_FOR_GAME SET Gra = \"${g.tytul}\" WHERE Gra = \"$t\""
        db.execSQL(query)
        db.execSQL(query2)
        db.execSQL(query3)
        db.execSQL(query4)
        db.execSQL(query5)
        db.close()
    }

    fun findGamesInLok(lok: String) : ArrayList<String>? {
        val query = "SELECT * FROM $TABLE_LOK_FOR_GAME WHERE Lokalizacja = \"$lok\""
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var name: String? = null
        var gameList: ArrayList<String> = ArrayList()

        while (cursor.moveToNext()) {
            name = cursor.getString(0)
            Log.i("pomocy-141249-dbh", "nazwa gry do usuniecia $name")
            gameList.add(name)
        }
        cursor.close()
        db.close()
        return gameList
    }

    fun insertLokForGame(gra: String, lok: String) {
        val db = this.writableDatabase
        db.execSQL("INSERT INTO $TABLE_LOK_FOR_GAME VALUES(\"${gra}\", \"${lok}\")")
        db.execSQL("UPDATE $TABLE_LOK SET Ilosc = Ilosc + 1 WHERE Lokalizacja = \"$lok\"")
        db.close()
    }

    fun updateLok(gra: String, lok_stara: String, lok: String) {
        val db = this.writableDatabase
        db.execSQL("UPDATE $TABLE_LOK SET Ilosc = Ilosc + 1 WHERE Lokalizacja = \"$lok\"")
        if (lok_stara != "") {
            db.execSQL("UPDATE $TABLE_LOK SET Ilosc = Ilosc - 1 WHERE Lokalizacja = \"$lok_stara\"")
        }
        db.execSQL("UPDATE $TABLE_LOK_FOR_GAME SET Lokalizacja = \"${lok}\" where Gra = \"${gra}\" ")
        db.close()
    }

    fun aktualizujLokacje(lok_past: String, lok: String) {
        val db = this.writableDatabase
        db.execSQL("UPDATE $TABLE_LOK SET Lokalizacja = \"$lok\" WHERE Lokalizacja = \"$lok_past\"")
        db.execSQL("UPDATE $TABLE_LOK_FOR_GAME SET Lokalizacja = \"$lok\" WHERE Lokalizacja = \"$lok_past\"")
    }

    fun findTitleById(idGry: Long) : String? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_GAMES WHERE BGGid = $idGry"
        val cursor = db.rawQuery(query, null)
        var name: String? = null
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return name
    }

    fun updateAktualnaPozycja(tytul: String?, pozycja: Int?) {
        if (tytul != null && pozycja != null) {
            val db = this.writableDatabase
            val query = "UPDATE $TABLE_GAMES SET akt_poz = $pozycja WHERE tytul = \"$tytul\""
            db.execSQL(query)

            val values = ContentValues()
            values.put("Tytul", tytul)
            values.put("Data", LocalDate.now().toString())
            values.put("Ranking", pozycja)

            db.insert(TABLE_HIST_POZ, null, values)
            db.close()
        }
    }

    fun deleteLokacje(lok: ArrayList<String>?) : ArrayList<String>? {
        val lok_z_bazy = findLokalizacje()
        if (lok != null && lok_z_bazy != null) {
            val db = this.writableDatabase
            for (l in lok_z_bazy) {
                if (l !in lok) {
                    Log.i("pomocy-141249-dhb", "usuwanie z bazy: $l")
                    db.execSQL("DELETE FROM $TABLE_LOK WHERE Lokalizacja = \"$l\" AND Ilosc = 0")
                }
            }
            db.close()
        }
        val res = findLokalizacje()
        return res
    }

}