package fr.ul.miage.ProjetRedis;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientRedis implements Runnable {
	private DonneesRedis donnees;
	private InputStream entreeCommande;
	private PrintWriter reponseCommande;
	private List<String> listeChaine;
	private ServeurRedis serveurActuelle;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				byte[] buffer = new byte[1024];
				int bytesRead = this.entreeCommande.read(buffer);
				String requete = new String(buffer, 0, bytesRead, "UTF-8");
				if (requete != null) {
					this.gestionDesActions(requete);
				}
			}
		} catch (IOException e) {
			System.err.println("Error handling client");
		}
	}

	public ClientRedis(Socket clientRedis, ServeurRedis serveurRedis) throws IOException {
		this.serveurActuelle = serveurRedis;
		this.entreeCommande = clientRedis.getInputStream();
		this.reponseCommande = new PrintWriter(clientRedis.getOutputStream());
		this.listeChaine = new ArrayList<>();
		this.donnees = serveurRedis.getDonnees();
		System.out.println("nouveau client: " + clientRedis.getInetAddress());
	}

	private void gestionDesActions(String requete) {
		String[] arguments = Utils.preparerRequete(requete).split(" ");
		switch (arguments[0].toUpperCase()) {
		case "SET":
			if (this.serveurActuelle.isEstMaitre()) {
				if (arguments.length >= 3) {
					this.donnees.miseAJourDonnes(arguments[1], arguments[2]);
					this.envoyerReponse(Utils.envoyerReponseStandard("OK"));
					this.serveurActuelle.setDonneesEsclave(this.donnees); // synchronisation esclave
				} else {
					this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
				}
			} else {
				this.envoyerReponse(
						Utils.envoyerReponseErreur("Cette commande ne peut pas être executé sur un serveur esclave"));
			}
			break;
		case "SETNX":
			if (this.serveurActuelle.isEstMaitre()) {
				if (arguments.length >= 3) {
					if (!this.donnees.cleExiste(arguments[1])) {
						this.donnees.miseAJourDonnes(arguments[1], arguments[2]);
					}
					this.envoyerReponse(Utils.envoyerReponseStandard("OK"));
					this.serveurActuelle.setDonneesEsclave(this.donnees);
				} else {
					this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
				}
			} else {
				this.envoyerReponse(
						Utils.envoyerReponseErreur("Cette commande ne peut pas être executé sur un serveur esclave"));
			}

			break;
		case "GET":
			if (arguments.length >= 2) {
				String resultat = this.donnees.trouverValeur(arguments[1]);
				String reponse = resultat != null ? Utils.envoyerReponseStandard(resultat)
						: Utils.envoyerReponseErreur("Cle Inexistante");
				this.envoyerReponse(reponse);
			} else {
				this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
			}
			break;
		case "STRLEN":
			if (arguments.length >= 2) {
				int resultat = this.donnees.longeurCle(arguments[1]);
				if (resultat < 0) {
					this.envoyerReponse(Utils.envoyerReponseErreur("Cle Inexistante"));
				} else {
					this.envoyerReponse(Utils.envoyerReponseEntier(resultat));
				}
			} else {
				this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
			}
			break;
		case "APPEND":
			if (this.serveurActuelle.isEstMaitre()) {
				if (arguments.length >= 3) {
					this.donnees.concatenerValeur(arguments[1], arguments[2]);
					this.envoyerReponse(Utils.envoyerReponseStandard("OK"));
				} else {
					this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
				}
			} else {
				this.envoyerReponse(
						Utils.envoyerReponseErreur("Cette commande ne peut pas être executé sur un serveur esclave"));
			}
			break;
		case "INCR":
			if (this.serveurActuelle.isEstMaitre()) {
				if (arguments.length >= 2) {
					Boolean resultat = this.donnees.incrementerCle(arguments[1]);
					if (resultat) {
						this.envoyerReponse(Utils.envoyerReponseStandard("OK"));
					} else {
						this.envoyerReponse(Utils
								.envoyerReponseErreur("La valeur que vous souhaitez incrementer n est pas un nombre"));
					}
				} else {
					this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
				}
			} else {
				this.envoyerReponse(
						Utils.envoyerReponseErreur("Cette commande ne peut pas être executé sur un serveur esclave"));
			}
			break;
		case "DECR":
			if (this.serveurActuelle.isEstMaitre()) {
				if (arguments.length >= 2) {
					Boolean resultat = this.donnees.decrementerCle(arguments[1]);
					if (resultat) {
						this.envoyerReponse(Utils.envoyerReponseStandard("OK"));
					} else {
						this.envoyerReponse(Utils
								.envoyerReponseErreur("La valeur que vous souhaitez incrementer n est pas un nombre"));
					}
				} else {
					this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
				}
			} else {
				this.envoyerReponse(
						Utils.envoyerReponseErreur("Cette commande ne peut pas être executé sur un serveur esclave"));
			}
			break;
		case "EXISTS":
			if (arguments.length >= 2) {
				this.envoyerReponse(Utils.envoyerReponseStandard(this.donnees.cleExiste(arguments[1]).toString()));
			} else {
				this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
			}
			break;
		case "DEL":
			if (this.serveurActuelle.isEstMaitre()) {
				if (arguments.length >= 2) {
					Boolean resultat = this.donnees.supprimerCle(arguments[1]);
					if (resultat) {
						this.envoyerReponse(Utils.envoyerReponseStandard("OK"));
					} else {
						this.envoyerReponse(Utils.envoyerReponseErreur("Cle Inexistante"));
					}
				} else {
					this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
				}
			} else {
				this.envoyerReponse(
						Utils.envoyerReponseErreur("Cette commande ne peut pas être executé sur un serveur esclave"));
			}
			break;
		case "EXPIRE":
			if (this.serveurActuelle.isEstMaitre()) {
				if (arguments.length >= 3) {
					Boolean resultat = this.donnees.expireCle(arguments[1], arguments[2]);
					if (resultat) {
						this.envoyerReponse(Utils.envoyerReponseStandard("OK"));
					} else {
						this.envoyerReponse(Utils.envoyerReponseErreur("Cle Inexistante"));
					}
				} else {
					this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
				}
			} else {
				this.envoyerReponse(
						Utils.envoyerReponseErreur("Cette commande ne peut pas être executé sur un serveur esclave"));
			}
			break;
		case "SUBSCRIBE":
			if (this.serveurActuelle.isEstMaitre()) {
				if (arguments.length >= 2) {
					this.listeChaine.add(arguments[1]);
					this.donnees.subscribeChaine(arguments[1], this);
					this.envoyerReponse(Utils.envoyerReponseStandard("OK"));
				} else {
					this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
				}
			} else {
				this.envoyerReponse(
						Utils.envoyerReponseErreur("Cette commande ne peut pas être executé sur un serveur esclave"));
			}
			break;
		case "UNSUBSCRIBE":
			if (this.serveurActuelle.isEstMaitre()) {
				if (arguments.length >= 2) {
					Boolean result = this.listeChaine.remove(arguments[1]);
					if (result) {
						this.donnees.unsubscribeChaine(arguments[1], this);
						this.envoyerReponse(Utils.envoyerReponseStandard("OK"));
					} else {
						this.envoyerReponse(
								Utils.envoyerReponseErreur("Vous n'etes pas abonne a la chaine: " + arguments[1]));
					}
				} else if (arguments.length == 1) {
					this.listeChaine.clear();
					for (String chaine : this.listeChaine) {
						this.donnees.unsubscribeChaine(chaine, this);
					}
					;
					this.envoyerReponse(Utils.envoyerReponseStandard("OK"));
				} else {
					this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
				}
			} else {
				this.envoyerReponse(
						Utils.envoyerReponseErreur("Cette commande ne peut pas être executé sur un serveur esclave"));
			}
			break;
		case "PUBLISH":
			if (this.serveurActuelle.isEstMaitre()) {
				if (arguments.length >= 3) {
					if (this.listeChaine.contains(arguments[1])) {
						this.donnees.publierSurChaineGlobal(arguments[1], arguments[2]);
					} else {
						this.envoyerReponse(
								Utils.envoyerReponseErreur("Vous n'etes pas abonne a la chaine: " + arguments[1]));
					}
				} else {
					this.envoyerReponse(Utils.envoyerReponseErreur("Arguments Insuffisants"));
				}
			} else {
				this.envoyerReponse(
						Utils.envoyerReponseErreur("Cette commande ne peut pas être executé sur un serveur esclave"));
			}
			break;
		default:
			this.envoyerReponse(
					Utils.envoyerReponseErreur("La commande que vous essayer d'executer n'est pas prise en charge"));
		}
	}

	protected void envoyerReponse(String reponse) {
		this.reponseCommande.write(reponse);
		this.reponseCommande.flush();
	}

}
