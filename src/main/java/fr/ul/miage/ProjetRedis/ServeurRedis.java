package fr.ul.miage.ProjetRedis;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurRedis implements Runnable {
	private int port;
	private DonneesRedis donnees;
	private boolean estMaitre;
	private DonneesRedis donneesEsclave;
	private boolean ferme;
	// Constructeur pour maitre
	public ServeurRedis(int port) {
		this.donnees = new DonneesRedis(estMaitre);
		this.port = port;
		this.estMaitre = true;
		this.donneesEsclave = null;
		this.ferme = false;
		System.out.println("Serveur maitre crée");
	}

	// Constructeur pour esclave
	public ServeurRedis(int port, DonneesRedis donnees) {
		this.port = port;
		this.donnees = donnees; // synchronisation des données
		this.estMaitre = false;
		this.donneesEsclave = null;
		System.out.println("Serveur esclave crée ... synchronisation des données effectuée");
	}

	public int getPort() {
		return port;
	}

	public DonneesRedis getDonnees() {
		return donnees;
	}

	public boolean isEstMaitre() {
		return estMaitre;
	}

	public DonneesRedis getDonneesEsclave() {
		return donneesEsclave;
	}

	public void setDonneesEsclave(DonneesRedis donneesEsclave) {
		this.donneesEsclave = donneesEsclave;
	}

	public void setEstMaitre(boolean estMaitre) {
		this.estMaitre = estMaitre;
		this.donneesEsclave = null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		new Thread(donnees).start();
		try (ServerSocket serverSocket = new ServerSocket(this.port)) {
			while (!ferme) {
				Socket clientSocket = serverSocket.accept();
				new Thread(new ClientRedis(clientSocket, this)).start();
			}
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setFerme(boolean ferme) {
		this.ferme = ferme;
	}

}
