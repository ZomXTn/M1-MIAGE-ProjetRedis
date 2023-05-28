package fr.ul.miage.ProjetRedis;

public class Utils {

	public static String preparerRequete(String entree) {
		return entree.replaceAll("\\*\\d+\r\n", "").replaceAll("\\$\\d+\r\n", "").replace("\r\n", " ");
	}
	
	public static String envoyerReponseErreur(String entree) {
		return new String("-"+entree+"\r\n");
	}
	
	public static String envoyerReponseStandard(String entree) {
		return new String("+"+entree+"\r\n");
	}
	
	public static String envoyerReponseEntier(int entree) {
		return new String(":"+String.valueOf(entree)+"\r\n");
	}
	
	public static Boolean chaineConvertiveEnEntier(String entree) {
		try {
			Integer.parseInt(entree);
		}catch(NumberFormatException e ) {
			return false;
		}
		return true; 
	}
	
	
}
