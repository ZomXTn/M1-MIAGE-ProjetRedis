package fr.ul.miage.ProjetRedis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DonneesRedis implements Runnable {
	private Map<String, String> donnees;
	private Map<String, Date> expirationMap;
	private Map<String, ArrayList<ClientRedis>> SubscribeMap;
	private boolean estMaitre;
	
	public DonneesRedis(boolean estMaitre) {
		super();
		// TODO Auto-generated constructor stub
		this.donnees = new HashMap<>();
		this.expirationMap = new HashMap<>();
		this.SubscribeMap = new HashMap<>();
		this.estMaitre = estMaitre;
	}

	protected synchronized Boolean cleExiste(String cle) {
		return this.donnees.containsKey(cle);
	}

	protected synchronized Boolean miseAJourDonnes(String cle, String valeur) {
		this.donnees.put(cle, valeur);
		return true;
	}

	protected synchronized Boolean supprimerCle(String cle) {
		if (this.donnees.containsKey(cle)) {
			this.donnees.remove(cle);
			return true;
		} else {
			return false;
		}
	}

	protected synchronized Boolean concatenerValeur(String cle, String valeur) {
		if (this.donnees.containsKey(cle)) {
			String ancienneValeur = this.donnees.get(cle);
			this.donnees.put(cle, ancienneValeur + valeur);
		} else {
			this.donnees.put(cle, valeur);
		}
		return true;
	}

	protected synchronized String trouverValeur(String cle) {
		return this.donnees.get(cle);
	}

	protected synchronized int longeurCle(String cle) {
		int longueur = this.donnees.containsKey(cle) ? this.donnees.get(cle).length() : -1;
		return longueur;
	}

	protected synchronized Boolean expireCle(String cle, String valeur) {
		if (this.donnees.containsKey(cle)) {
			Date dateActuelle = new Date();
			Date dateExpiration = new Date(dateActuelle.getTime() + Integer.parseInt(valeur) * 1000);
			this.expirationMap.put(cle, dateExpiration);
			return true;
		}
		return false;
	}

	protected synchronized Boolean cleExpire(String cle) {
		System.out.println(this.expirationMap.get(cle).compareTo(new Date()));
		Boolean resultat = this.expirationMap.get(cle).compareTo(new Date()) > 0 ? false : true;
		return resultat;
	}

	protected synchronized Boolean incrementerCle(String cle) {
		if (this.cleExiste(cle)) {
			if (Utils.chaineConvertiveEnEntier(this.donnees.get(cle))) {
				this.donnees.put(cle, String.valueOf(Integer.parseInt(this.donnees.get(cle)) + 1));
			} else {
				return false;
			}
		} else {
			this.donnees.put(cle, "0");
		}
		return true;
	}

	protected synchronized Boolean decrementerCle(String cle) {
		if (this.cleExiste(cle)) {
			if (Utils.chaineConvertiveEnEntier(this.donnees.get(cle))) {
				this.donnees.put(cle, String.valueOf(Integer.parseInt(this.donnees.get(cle)) - 1));
			} else {
				return false;
			}
		} else {
			this.donnees.put(cle, "0");
		}
		return true;
	}

	protected synchronized Boolean subscribeChaine(String chaine, ClientRedis client) {
		ArrayList<ClientRedis> listeAbonnes = this.SubscribeMap.containsKey(chaine) ? this.SubscribeMap.get(chaine)
				: new ArrayList<ClientRedis>();
		listeAbonnes.add(client);
		this.SubscribeMap.put(chaine, listeAbonnes);
		return true;
	}

	protected synchronized Boolean unsubscribeChaine(String chaine, ClientRedis client) {
		if (this.SubscribeMap.containsKey(chaine)) {
			this.SubscribeMap.get(chaine).remove(client);
			return true;
		}
		return false;
	}

	protected Boolean publierSurChaineGlobal(String chaine, String msg) {
		if (!this.SubscribeMap.containsKey(chaine) || this.SubscribeMap.get(chaine).isEmpty()) {
			return false;
		}
		for (ClientRedis client : this.SubscribeMap.get(chaine)) {
			System.out.println("aaaa");
			client.envoyerReponse(Utils.envoyerReponseStandard("chaine "+chaine+": "+msg));
		}
		return true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Thread.sleep(1000);
				for (String cleExpire : this.expirationMap.keySet()) {
					if (this.cleExpire(cleExpire)) {
						synchronized (donnees) {
							this.donnees.remove(cleExpire);
							this.expirationMap.remove(cleExpire);
						}
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
