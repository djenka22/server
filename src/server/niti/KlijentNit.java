/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.niti;

import domain.Korisnik;
import domain.Poruka;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import komunikacija.Komunikacija;
import komunikacija.Odgovor;
import komunikacija.Operacije;
import komunikacija.Zahtev;
import repository.Repository;

/**
 *
 * @author Rusimovic
 */
public class KlijentNit extends Thread {

    private Komunikacija komunikacija;
    private Korisnik korisnik;
    private ServerNit serverNit;

    public KlijentNit(Socket socket, ServerNit aThis) {
        try {
            komunikacija = new Komunikacija(socket);
        } catch (IOException ex) {
            Logger.getLogger(KlijentNit.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.serverNit = aThis;

    }

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public Komunikacija getKomunikacija() {
        return komunikacija;
    }

    @Override
    public void run() {
        System.out.println("Server pokrenuo klijentNit.");
        try {
            while (true) {

                Zahtev zahtev = (Zahtev) komunikacija.procitaj();

                obradaKlijenta(zahtev);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void obradaKlijenta(Zahtev zahtev) throws IOException {
        Odgovor odgovor = new Odgovor();
        switch (zahtev.getOperacija()) {
            case Operacije.LOGIN:

                login(zahtev, odgovor);
                break;
            case Operacije.SLANJE_OPSTE_PORUKE:
                posaljiPorukuSvima(zahtev);
            case Operacije.SLANJE_PORUKA_ODREDJENOM_KORISNIKU:
                slanjePorukeOdredjenomKorisniku(zahtev);
                break;
        }
    }

    private void login(Zahtev zahtev, Odgovor odgovor) throws IOException {
        String email = (String) zahtev.getData("email");
        String sifra = (String) zahtev.getData("sifra");

        if (serverNit.korisnikPrijavljen(email, sifra)) {
            odgovor.setGreska("Korisnik je vec prijavljen");
        } else {
            korisnik = Repository.getInstance().login(email, sifra);
            if (korisnik != null) {
                odgovor.putData("korisnik", korisnik);
                odgovor.setGreska(null);
            } else {
                odgovor.setGreska("IKorisnik ne postoji");
            }
            odgovor.setOperacija(Operacije.LOGIN);
            komunikacija.posalji(odgovor);

            if (korisnik != null) {
                serverNit.noviKlijentSePrijavio(this);
            }

        }
    }

    public void prekiniKomunikaciju() {
        komunikacija.prekiniKomunikaciju();
    }

    private void posaljiPorukuSvima(Zahtev zahtev) {
        serverNit.posaljiPorukuSvima(zahtev);
    }

    private void slanjePorukeOdredjenomKorisniku(Zahtev zahtev) {
        serverNit.posaljiPorukuSvima(zahtev);
    }

}
