package predavanje2;

import java.sql.SQLException;
import java.util.Scanner;

public class Program {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Dobrodosli!");

		DBProdavnica db = new DBProdavnica(
				"jdbc:sqlite:C:\\Users\\tribu\\Desktop\\ProdavnicaAutomobila\\Prodavnica.db");

		db.connect();

		String user = "";

		System.out.println("Unesite 1 za log in ili 2 za registraciju: ");
		int izborLR = sc.nextInt();

		System.out.println("Username: ");
		String username = sc.next();
		System.out.println("Password: ");
		String password = sc.next();

		switch (izborLR) {
		case 1:
			user = db.logovanje(username, password);
			if (user.isEmpty()) {
				System.out.println("Neuspesno logovanje.");
				user = db.logovanje(username, password);
			} else {
				System.out.println("Uspesno logovanje.");
			}
			break;
		case 2:
			System.out.println("Jos jednom password: ");
			String password2 = sc.next();
			user = db.registracija(username, password, password2);
			if (user.isEmpty()) {
				System.out.println("Neuspesna registracija.");
				user = db.registracija(username, password, password2);
			} else {
				System.out.println("Uspesna registracija i logovanje.");
			}
			break;
		default:
			System.out.println("Neispravan unos.");
		}

		if (user.equals("admin")) {
			System.out.println("Unesite 1 za promenu cene, 2 za nabavku, 3 za izlaz: ");
			int izborPN = sc.nextInt();
			while (izborPN != 3) {
				switch (izborPN) {
				case 1:
					db.upitSviAutomobili();
					System.out.println("Za koji model zelite da vrsite korekciju cene: ");
					int idZaPromenuCene = sc.nextInt();
					System.out.println("Nova cena: ");
					int novaCena = sc.nextInt();
					if (db.promenaCene(idZaPromenuCene, novaCena)) {
						System.out.println("Promena cene izvrsena.");
					}
					break;
				case 2:
					db.upitSviAutomobili();
					System.out.println("Koji automobil zelite da nabavite: ");
					int idAutaZaNabavku = sc.nextInt();

					if (db.nabavka(idAutaZaNabavku)) {
						System.out.println("Uspesna nabavka.");
					}
					;
					break;
				default:
					System.out.println("Neispravan unos.");
				}
				System.out.println("Unesite 1 za promenu cene, 2 za nabavku, 3 za izlaz: ");
				izborPN = sc.nextInt();
			}
		}

		if (!user.equals("admin") && !user.equals("")) {
			System.out.println("Unesite 1 za kupovinu automobila, 2 za uplatu novca, 3 za izlaz: ");
			int izborKU = sc.nextInt();
			while (izborKU != 3) {
				switch (izborKU) {
				case 1:
					db.upitNabavljeniNeprodatiAutomobili();
					System.out.println("Koji auto zelite da kupite: ");
					int idIzabranogAuta = sc.nextInt();
					if (db.kupovinaAutomobila(idIzabranogAuta, user)) {
						System.out.println("Uspesno ste kupili automobil.");
					}
					break;
				case 2:
					db.upitKupljeniAutomobili(user);
					System.out.println("Uplata novca za model: ");
					int idAutaZaPlacanje = sc.nextInt();
					int josZaPlacanje = 0;
					try {
						josZaPlacanje = db.josZaPlacanje(idAutaZaPlacanje);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Koliko novca zelite da uplatite: ");
					int uplata = sc.nextInt();
					while (josZaPlacanje < uplata && uplata < 0) {
						System.out.println("Dajete vise nego sto treba. Ili ne dajete nista.");
						System.out.println("Koliko novca zelite da uplatite: ");
						uplata = sc.nextInt();
					}
					if (db.uplataNovca(idAutaZaPlacanje, uplata)) {
						System.out.println("Uplata izvrsena.");
					}
					break;
				default:
					System.out.println("Neispravan unos.");
				}
				System.out.println("Unesite 1 za kupovinu automobila, 2 za uplatu novca, 3 za izlaz: ");
				izborKU = sc.nextInt();
				
			}
		}

		db.disconnect();
	}
}
