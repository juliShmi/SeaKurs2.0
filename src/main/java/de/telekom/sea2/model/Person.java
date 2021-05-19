package de.telekom.sea2.model;

import de.telekom.sea2.lookup.Salutation;

public class Person {

	private long id;
	private static long idUpdate = 0;
	private Salutation salutation;
	private String firstname;
	private String lastname;

	public Person(Salutation salutation, String firstname, String lastname) {
		this.id = ++idUpdate;
		this.salutation = salutation;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public Person() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

}
