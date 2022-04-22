/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package repository;

import domain.Korisnik;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rusimovic
 */
public class Repository {
    List<Korisnik> korisnici;
    private static Repository instance;
    /**
     * @param args the command line arguments
     */
    private Repository(){
        korisnici=new ArrayList<>();
        korisnici.add(new Korisnik("K1","k1","k1@gmail.com","sifra1"));
        korisnici.add(new Korisnik("K2","k2","k2@gmail.com","sifra2"));
        korisnici.add(new Korisnik("K3","k3","k3@gmail.com","sifra3"));
        korisnici.add(new Korisnik("K4","k4","k4@gmail.com","sifra4"));
    }
   
    public static Repository getInstance(){
        if(instance == null){
            instance = new Repository();
        }
        return instance;
    }
    
    public Korisnik login(String email, String sifra){
        for(Korisnik korisnik : korisnici){
            if(korisnik.getEmail().equals(email) && korisnik.getSifra().equals(sifra)){
                return korisnik;
            }
        }
        
        return null;
    }
}
