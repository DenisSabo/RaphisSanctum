package vv.assignment.restful;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

@Entity
public class Customer {
    @Id
    @GeneratedValue
    private Long id;
    String firstname;
    String lastname;
    LocalDate dateOfBirth;

    // Customer has an adress
    // optional false, because customer must have an adress TODO maybe not
    @OneToOne(optional=false)
    private Adress adress;

    // Customer can have many contracts
    // TODO "mappedBy" ??
    // TODO rename Kundenverträge to Verträge
    @OneToMany(targetEntity = Contract.class, fetch=FetchType.LAZY)
    private Collection<Contract> contracts;

    // Constructors
    public Customer(){
        //default constructor
    }

    public Customer(String firstname, String lastname, LocalDate dateOfBirth, Adress adress) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
        this.adress = adress;
    }

    public Customer(String firstname, String lastname, LocalDate dateOfBirth, Adress adress, Collection<Contract> contracts) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
        this.adress = adress;
        this.contracts = contracts;
    }

    // Getter and setter
    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Adress getAdress() {
        return adress;
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
    }

    public Collection getContracts() {
        return contracts;
    }

    public void setContracts(Collection contracts) {
        this.contracts = contracts;
    }

    // Equals, HashCode, and toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        return id.equals(customer.id);
    }

    // TODO maybe other fields should be included as well
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", adress=" + adress +
                ", contracts=" + contracts +
                '}';
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
