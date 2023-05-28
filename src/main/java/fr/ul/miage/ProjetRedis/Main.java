package fr.ul.miage.ProjetRedis;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Boolean running = true;
		ServeurRedis master = null;
		ServeurRedis slave = null;
		int port = 6379;
//		// TODO Auto-generated method stub
//		
//	}

		Scanner scanner = new Scanner(System.in);
		while (running) {
			System.out.println("Veuillez entrez une commande:");
			String command = scanner.nextLine();
			String[] arguments = command.split(" ");
			switch (arguments[0].toUpperCase()) {
			case "ADD":
				if (arguments.length >= 2) {
					switch (arguments[1].toUpperCase()) {
					case "MASTER":
						if (master == null) {
							master = new ServeurRedis(port);
							new Thread(master).start();
							++port;
						} else {
							System.err.println("Un serveur maitre est deja actif");
						}
						break;
					case "SLAVE":
						if (slave == null && master != null) {
							slave = new ServeurRedis(port, master.getDonnees());
							master.setDonneesEsclave(slave.getDonnees());
							new Thread(slave).start();
							++port;
						} else {
							if (slave != null) {
								System.err.println("Un serveur esclave est deja actif");
							} else {
								System.out.println("Il n'y a aucun serveur maitre actif");
							}
						}
						break;
					}
				} else {
					System.err.println("Arguments Manquants");
				}
				break;
			case "DELETE":
				if (arguments.length >= 2) {
					switch (arguments[1].toUpperCase()) {
					case "MASTER":
						if (master != null) {
							System.out.println("Fermeture du serveur maitre .....");
							if (slave != null) {
								master.setFerme(true);
								slave.setFerme(true);
								slave.setEstMaitre(true);
								slave.setFerme(false);
								master = slave;
								slave = null;
								System.out.println("Le serveur esclave est maintenant Maitre");
							} else {
								master.setFerme(true);
								master = null;
							}
						} else {
							System.err.println("Il n'y a aucun serveur maitre actif");
						}
						break;
					case "SLAVE":
						if (slave != null) {
							System.out.println("Fermeture du serveur esclave .....");
							slave.setFerme(true);
							slave = null;
						} else {
							System.err.println("Il n'y a aucun serveur esclave actif");
						}
						break;
					}
				} else {
					System.err.println("Arguments Manquants");
				}
				break;
			case "QUIT":
				System.out.println("Fermeture du serveur ...");
				running = false;
				break;
			default:
				System.out.println("La commande que vous avez saisi n'existe pas");
				break;
			}
		}
		scanner.close();
		System.exit(0);
	}

}
