package de.telekom.sea2.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

import de.telekom.sea2.lookup.Salutation;
import de.telekom.sea2.model.Person;

public class PersonRepository {

	private Person[] groupList;
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private PreparedStatement preparedStatement;

	final static String DRIVER = "org.mariadb.jdbc.Driver";

	public PersonRepository() throws SQLException, ClassNotFoundException {
		try {
			Class.forName(DRIVER);
			this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/seadb", "seauser", "seapass");
			this.statement = connection.createStatement();
			getId();

		} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();

		}
	}

	public boolean create(Person p) throws SQLException, SQLIntegrityConstraintViolationException {
		if (p == null)
			return false;
		if (p instanceof Person) {
			try {
				preparedStatement = connection
						.prepareStatement("INSERT INTO personen (ANREDE, VORNAME, NACHNAME) VALUES ( ?, ?, ? )");
				preparedStatement.setByte(1, toBytes(p.getSalutation()));
				preparedStatement.setString(2, p.getFirstname());
				preparedStatement.setString(3, p.getLastname());
				preparedStatement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				e.getMessage();
			}
		}
		return true;
	}

	public boolean update(Person p) throws SQLException, SQLDataException {
		if (p == null) {
			return false;
		}
		if (get(p.getId()) == null) {
			System.out.println("Check ID, person doesn't exist");
			return false;
		}
		try {
			preparedStatement = connection.prepareStatement("UPDATE personen SET VORNAME =?, NACHNAME =? WHERE ID =?");
			preparedStatement.setString(1, p.getFirstname());
			preparedStatement.setString(2, p.getLastname());
			preparedStatement.setLong(3, p.getId());
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();
		}
		return true;
	}

	public Person get(long id) throws SQLException {
		Person person = new Person();
		preparedStatement = connection.prepareStatement("SELECT * FROM personen WHERE ID = " + id + "");
		resultSet = preparedStatement.executeQuery();
		if (person != null) {
			try {
				while (resultSet.next()) {
					System.out.println("ID: " + resultSet.getLong(1));
					System.out.println("Salutation: " + fromBytes(resultSet.getByte(2)));
					System.out.println("Firstname: " + resultSet.getString(3));
					System.out.println("Lastname: " + resultSet.getString(4));
					person.setId(resultSet.getLong(1));
					String salut = fromBytes(resultSet.getByte(2));
					person.setSalutation(Salutation.fromString(salut));
					person.setFirstname(resultSet.getString(3));
					person.setLastname(resultSet.getString(4));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				e.getMessage();

			}
		}
		return person;

	}

	public Person[] getAll() throws SQLException {
		preparedStatement = connection.prepareStatement("SELECT COUNT (ID) FROM personen");
		resultSet = preparedStatement.executeQuery();
		int peopleCounter = 0;
		while (resultSet.next()) {
			peopleCounter = resultSet.getInt(1);
		}
		System.out.println("People in the table " + peopleCounter);

		groupList = new Person[peopleCounter];
		preparedStatement = connection.prepareStatement("SELECT * FROM personen");
		resultSet = preparedStatement.executeQuery("SELECT * FROM personen");
		int i = 0;
		while (resultSet.next()) {
			Person person = new Person(); // create new Instance every time
			person.setId(resultSet.getLong(1));
			String salut = fromBytes(resultSet.getByte(2));
			person.setSalutation(Salutation.fromString(salut));
			person.setFirstname(resultSet.getString(3));
			person.setLastname(resultSet.getString(4));
			groupList[i] = person;
			i++;
		}
		for (int j = 0; j < peopleCounter; j++) {
			System.out.println("ID: " + groupList[j].getId());
			System.out.println("Anrede: " + groupList[j].getSalutation());
			System.out.println("Vorname: " + groupList[j].getFirstname());
			System.out.println("Nachname: " + groupList[j].getLastname());
		}
		return groupList;

	}

	public Person search(String vorname, String lastname) {
		String searchFN = "%" + vorname + "%";
		String searchLN = "%" + lastname + "%";
		try {
			preparedStatement = connection
					.prepareStatement("SELECT * FROM personen WHERE VORNAME LIKE ? AND NACHNAME LIKE ?");
			preparedStatement.setString(1, searchFN);
			preparedStatement.setString(2, searchLN);
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();
		}
		try {
			while (resultSet.next()) {
				Person person = new Person();
				person.setId(resultSet.getLong(1));
				String salut = fromBytes(resultSet.getByte(2));
				person.setSalutation(Salutation.fromString(salut));
				person.setFirstname(resultSet.getString(3));
				person.setLastname(resultSet.getString(4));
				System.out.println("ID: " + resultSet.getLong(1));
				System.out.println("Salutation: " + fromBytes(resultSet.getByte(2)));
				System.out.println("Firstname: " + resultSet.getString(3));
				System.out.println("Lastname: " + resultSet.getString(4));
				return person;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();
		}
		System.out.println("Person not found");
		return null;

	}

	public boolean deleteAll() {
		if (true) {
			try {
				preparedStatement = connection.prepareStatement("DELETE from personen");
				preparedStatement.execute();
				System.out.println("List deleted");
			} catch (SQLException e) {
				e.printStackTrace();
				e.getMessage();
			}
		}
		return false;

	}

	private void getId() {
		try {
			preparedStatement = connection
					.prepareStatement("ALTER TABLE personen MODIFY ID BIGINT NOT NULL AUTO_INCREMENT");
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();
		}
	}

	public boolean delete(long id) {
		if (true) {
			try {
				preparedStatement = connection.prepareStatement("DELETE FROM personen WHERE ID = " + id + "");
				preparedStatement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				e.getMessage();
			}
		}
		return false;
	}

	public byte toBytes(Salutation salutation) {
		switch (salutation) {
		case MR:
			return 0;
		case MRS:
			return 1;
		case OTHER:
			return 2;
		}
		throw new IllegalArgumentException("Unexpected value");

	}

	public String fromBytes(byte salutation) {

		switch (salutation) {
		case 0:
			return "Mr";
		case 1:
			return "Mrs";
		case 2:
			return "Other";
		}
		throw new IllegalArgumentException("Unexpected value");

	}

	public void close() {
		try {
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();
		}
	}
}
