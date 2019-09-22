package predavanje2;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Scanner;

public class DBProdavnica {

	String connectionString;
	Connection con;

	public DBProdavnica(String conStr) {
		connectionString = conStr;
	}

	public void connect() {
		try {
			con = DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String logovanje(String username, String password) {
		String user = "";
		try {
			PreparedStatement ps = con.prepareStatement("select * from korisnik where username = ? and password = ?");
			ps.setString(1, username);
			ps.setString(2, password);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user = rs.getString(1);
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}

		return user;
	}

	public String registracija(String username, String password, String password2) {
		String user = "";
		if (imaSpaceUStringu(username) || imaSpaceUStringu(password)) {
			System.out.println("Username i password ne smeju da sadrze space.");
		} else if (!this.jesteUnique(username)) {
			System.out.println("Username vec postoji.");
		} else if (!password.equals(password2)) {
			System.out.println("Sifre nisu iste.");
		} else {
			if (dodavanjeNovogKorisnika(username, password)) {
				user = username;
			}
		}

		return user;

	}

	public boolean dodavanjeNovogKorisnika(String username, String password) {
		try {
			PreparedStatement ps = con.prepareStatement("insert into Korisnik(Username, Password) values ( ? , ? )");
			ps.setString(1, username);
			ps.setString(2, password);
			ps.execute();
			ps.close();

			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public static boolean imaSpaceUStringu(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ' ') {

				return true;
			}
		}

		return false;
	}

	public boolean jesteUnique(String value) {
		try {
			PreparedStatement ps = con.prepareStatement("select * from Korisnik where Username = ?");
			ps.setString(1, value);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {

				return true;
			}
			ps.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public boolean uplataNovca(int idAutaZaPlacanje, int uplata) {
		try {
			PreparedStatement ps = con.prepareStatement("insert into Uplata(IdAuto, Iznos) values (?,?)");
			ps.setInt(1, idAutaZaPlacanje);
			ps.setInt(2, uplata);
			ps.execute();
			ps.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} return false;
	}

	public int josZaPlacanje(int idAuta) throws SQLException {
		PreparedStatement ps = con.prepareStatement(
				"select distinct Automobil.Cena - ( select sum(Uplata.Iznos) from Uplata where Uplata.IdAuto = ? ) "
						+ "from Automobil, Uplata " + "where Automobil.IdAuto = Uplata.IdAuto and Uplata.IdAuto = ? ");
		ps.setInt(1, idAuta);
		ps.setInt(2, idAuta);
		ResultSet rs = ps.executeQuery();
		int josZaPlacanje = rs.getInt(1);
		ps.close();
		rs.close();

		return josZaPlacanje;
	}

	public void upitKupljeniAutomobili(String user) {
		System.out.println("Id i model automobila za koje mozete izvrsiti uplatu: ");
		try {
			PreparedStatement ps = con
					.prepareStatement("select Automobil.IdAuto, Model.Naziv, Automobil.Cena  from Model, Automobil, Prodaja "
							+ "where Automobil.IdAuto = Prodaja.IdAuto and Automobil.IdModel = Model.IdModel and Prodaja.Username = ?");
			ps.setString(1, user);

			ResultSet rs = ps.executeQuery();
			ispisAutomobila(rs);
			ps.close();
			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void upitNabavljeniNeprodatiAutomobili() {
		System.out.println("Id i model automobila koje mozete kupiti: ");
			try {
				PreparedStatement ps = con.prepareStatement("select distinct Automobil.IdAuto, Model.Naziv, Automobil.Cena "
						+ "from Model, Automobil, Prodaja, Nabavka "
						+ "where Automobil.IdAuto = Nabavka.IdAuto and Automobil.IdModel = Model.IdModel and Nabavka.IdAuto not in "
						+ " ( select IdAuto from Prodaja)");
				ResultSet rs = ps.executeQuery();
				ispisAutomobila(rs);
				ps.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public boolean kupovinaAutomobila(int idIzabranogAuta, String user) {
		try {
			PreparedStatement ps = con.prepareStatement("insert into Prodaja(IdAuto, Username, Datum) values (?,?,?)");
			ps.setInt(1, idIzabranogAuta);
			ps.setString(2, user);
			LocalDate sad = LocalDate.now();
			Date date = Date.valueOf(sad);
			ps.setString(3, date.toString());
			ps.execute();
			ps.close();
			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} return false;
	}

	public boolean promenaCene(int novaCena, int idZaPromenuCene) {		
		try {
			PreparedStatement ps = con.prepareStatement("update Automobil set Cena = ? where Automobil.IdAuto = ?");
			ps.setInt(1, novaCena);
			ps.setInt(2, idZaPromenuCene);
			ps.execute();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} return false;
	}

	public void upitSviAutomobili() {
		System.out.println("Ispis svih automobila: ");
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(
					"select Automobil.IdAuto, Model.Naziv, Automobil.Cena from Automobil, Model where Automobil.IdModel = Model.IdModel ");
			ResultSet rs = ps.executeQuery();
			ispisAutomobila(rs);
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
			
	public void ispisAutomobila (ResultSet rs) throws SQLException {
			while (rs.next()) {
				int idAuta = rs.getInt(1);
				String model = rs.getString(2);
				int cena = rs.getInt(3);
				System.out.println(idAuta + " - " + model + " - " + cena);
			}
	}

	public boolean nabavka(int idAutaZaNabavku) {
		try {
			PreparedStatement ps = con.prepareStatement(
					"insert into Nabavka(IdAuto, NabavnaCena) values (?, ( select Automobil.Cena from Automobil where Automobil.IdAuto = ?))");
			ps.setInt(1, idAutaZaNabavku);
			ps.setInt(2, idAutaZaNabavku);
			ps.execute();
			ps.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} return false;
	}
}
