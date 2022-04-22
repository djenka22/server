/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.niti;

import domain.Korisnik;
import domain.Poruka;
import java.io.IOException;
import java.util.List;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import komunikacija.Odgovor;
import komunikacija.Operacije;
import komunikacija.Zahtev;
import server.frm.FrmServer;

/**
 *
 * @author Rusimovic
 */
public class ServerNit extends Thread {

    ServerSocket serverSocket;
    List<KlijentNit> klijenti = new ArrayList<>();
    FrmServer frmServer;
    private List<Poruka> poruke = new ArrayList<>();

    public ServerNit(FrmServer frmServer) throws IOException {

        serverSocket = new ServerSocket(9000);
        this.frmServer = frmServer;
    }

    @Override
    public void run() {
        try {
            System.out.println("Server pokrenut");
            while (true) {

                Socket socket = serverSocket.accept();
                poveziKlijenta(socket);
            }
        } catch (IOException ex) {
            System.out.println("Server zaustavljen");
        }
    }

    private void poveziKlijenta(Socket socket) {

        KlijentNit klijent = new KlijentNit(socket, this);

        klijenti.add(klijent);

        klijent.start();

    }
    
    

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public boolean korisnikPrijavljen(String email, String sifra) {
        for (KlijentNit klijent : klijenti) {
            if (klijent.getKorisnik() != null) {
                if (klijent.getKorisnik().getEmail().equals(email) && klijent.getKorisnik().getSifra().equals(sifra)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    void izbaciKlijekta(KlijentNit knit) {
        klijenti.remove(knit);
    }
    
    void noviKlijentSePrijavio(KlijentNit aThis) throws IOException {
        System.out.println("Novi klijent se prijavio");
        posaljiKorisnikeSvimKlijentima(Operacije.NOVI_KORISNIK_SE_PRIJAVIO);

    }
    
    private void posaljiKorisnikeSvimKlijentima(int operacija) {
        List<Korisnik> korisnici = new ArrayList<>();
        for (KlijentNit k : klijenti) {
            korisnici.add(k.getKorisnik());
        }
        for (KlijentNit k : klijenti) {

            Odgovor response = new Odgovor();
            response.setOperacija(operacija);
            response.putData("korisnici", korisnici);
            try {
                k.getKomunikacija().posalji(response);
            } catch (IOException ex) {
                Logger.getLogger(ServerNit.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    void posaljiPorukuSvima(Zahtev zahtev) {
        poruke.add((Poruka) zahtev.getPodaci().get("poruka"));
        for (KlijentNit k : klijenti) {
            // poruke.add((String) zahtev.getData("poruka"));
            Odgovor response = new Odgovor();
            response.setOperacija(zahtev.getOperacija());
            response.putData("poruka", zahtev.getPodaci().get("poruka"));

            try {
                k.getKomunikacija().posalji(response);
            } catch (IOException ex) {
                Logger.getLogger(ServerNit.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    void odjavaKlijenta(KlijentNit aThis) {
        List<Korisnik> korisnici = new ArrayList<>();
        aThis.prekiniKomunikaciju();
        klijenti.remove(aThis);
        for (KlijentNit korisnik : klijenti) {
            korisnici.add(korisnik.getKorisnik());
        }
        for (KlijentNit k : klijenti) {

            Odgovor response = new Odgovor();
            response.setOperacija(Operacije.ODJAVA_KLIJENTA);

            response.putData("korisnici", korisnici);

            try {
                k.getKomunikacija().posalji(response);
            } catch (IOException ex) {
                Logger.getLogger(ServerNit.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    void posaljiPorukuSvimKlijentima(Zahtev zahtev) {
        Poruka poruka = (Poruka) zahtev.getPodaci().get("poruka");
        System.out.println(poruka.getKaKorisniku());
        for (KlijentNit k : klijenti) {
            if(poruka.getKaKorisniku() != null){
            if (poruka.getKaKorisniku().equals(k.getKorisnik())) {
                Odgovor response = new Odgovor();
                response.setOperacija(zahtev.getOperacija());
                response.putData("poruka", poruka);

                try {
                    k.getKomunikacija().posalji(response);
                } catch (IOException ex) {
                    Logger.getLogger(ServerNit.class.getName()).log(Level.SEVERE, null, ex);
                }}

            }
        }
    }
}
