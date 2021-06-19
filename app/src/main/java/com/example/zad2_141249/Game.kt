package com.example.zad2_141249

import java.time.LocalDate

class Game {

    var tytul: String = ""
    var oryginalnyTytul: String = ""
    var rokWydania: Int = 0
    var nazwiskaProjektantow: ArrayList<String>? = null
    var nazwiskaArtystow: ArrayList<String>? = null
    var dodatki: ArrayList<DodatkiDoGry>? = null
    var opis: String  = ""
    var dataZamowienia: LocalDate = LocalDate.now()
    var dataDodania: LocalDate = LocalDate.now()
    var koszt: String = ""
    var SCD: String = ""
    var kodEAN_UPC: String = ""
    var idBGG: Long = 0
    var kodProduktu: String = ""
    var pozycjaAktualna: Int = 0
    var wersja: String  = ""
    var komentarz: String  = ""
    var miniaturka: String  = ""
    var histPozycja: MutableList<HistorycznaPozycja> = ArrayList()
    var lokalizacja: String  = ""

    constructor(tytul: String, oryginalnyTytul: String, rokWydania: Int, nazwiskaProjektantow: ArrayList<String>?,
                nazwiskaArtystow: ArrayList<String>?, dodatki: ArrayList<DodatkiDoGry>?,
                opis: String, dataZamowienia: LocalDate, dataDodania: LocalDate, koszt: String, SCD: String, kodEAN_UPC: String,
                idBGG: Long, kodProduktu: String, pozycjaAktualna: Int, wersja: String, komentarz: String, miniaturka: String,
                histPozycja: HistorycznaPozycja, lokalizacja: String) {
        this.tytul = tytul
        this.rokWydania = rokWydania
        this.opis = opis
        this.pozycjaAktualna = pozycjaAktualna
        this.miniaturka = miniaturka
        this.oryginalnyTytul = oryginalnyTytul
        this.dataDodania = dataDodania
        this.dataZamowienia = dataZamowienia
        this.koszt = koszt
        this.SCD = SCD
        this.kodEAN_UPC = kodEAN_UPC
        this.idBGG = idBGG
        this.kodProduktu = kodProduktu
        this.wersja = wersja
        this.komentarz = komentarz
        this.histPozycja.add(histPozycja)
        this.lokalizacja = lokalizacja
        this.nazwiskaArtystow = nazwiskaArtystow
        this.nazwiskaProjektantow = nazwiskaProjektantow
        this.dodatki = dodatki
    }


    constructor(id_BGG: Long, tytul: String, rokWydania: Int, opis: String, pozycjaAktualna: Int, miniaturka: String) {
        this.tytul = tytul
        this.rokWydania = rokWydania
        this.opis = opis
        this.pozycjaAktualna = pozycjaAktualna
        this.miniaturka = miniaturka
        this.oryginalnyTytul = ""
        this.dataDodania = LocalDate.now()
        this.dataZamowienia = LocalDate.now()
        this.koszt = ""
        this.SCD = ""
        this.kodEAN_UPC = ""
        this.idBGG = id_BGG
        this.kodProduktu = ""
        this.wersja = ""
        this.komentarz = ""
        this.histPozycja.add(HistorycznaPozycja(LocalDate.now(), -1))
        this.lokalizacja = ""
        this.nazwiskaArtystow = null
        this.nazwiskaProjektantow = null
        this.dodatki = null
    }

    constructor(id_BGG: Long, tytul: String, oryginalnyTytul: String, rokWydania: Int, opis: String,
                nazwiskaProjektantow: ArrayList<String>, nazwiskaArtystow: ArrayList<String>,
                dodatki: ArrayList<DodatkiDoGry>?, pozycjaAktualna: Int,
                miniaturka: String, wersja: String, histPozycja: HistorycznaPozycja) {
        this.tytul = tytul
        this.rokWydania = rokWydania
        this.opis = opis
        this.pozycjaAktualna = pozycjaAktualna
        this.miniaturka = miniaturka
        this.oryginalnyTytul = oryginalnyTytul
        this.dataDodania = LocalDate.now()
        this.dataZamowienia = LocalDate.now()
        this.koszt = ""
        this.SCD = ""
        this.kodEAN_UPC = ""
        this.idBGG = id_BGG
        this.kodProduktu = ""
        this.wersja = wersja
        this.komentarz = ""
        this.histPozycja.add(histPozycja)
        this.lokalizacja = ""
        this.nazwiskaArtystow = nazwiskaArtystow
        this.nazwiskaProjektantow = nazwiskaProjektantow
        this.dodatki = dodatki
    }
}