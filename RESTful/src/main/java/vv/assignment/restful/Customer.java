package vv.assignment.restful;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
public class Customer {
    @Id
    @GeneratedValue
    private Long id;
    String firstname;
    String lastname;
    LocalDate dateOfBirth;

    // Customer has an adress
    @OneToOne(
            cascade = {CascadeType.ALL}
            )
    private Adress adress;

    // Customer can have many contracts
    @OneToMany(targetEntity = Contract.class, fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Contract> contracts;

    /**
     *  bidirectional associations should always provide
     *  methods, which are used to synchronize both sides
     *  of the relationship
     *  Source: https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/
     */
    public void addContract(Contract contract){
        contracts.add(contract);
        contract.setCustomer(this);
    }

    public void removeContract(Contract contract){
        contracts.remove(contract);
        contract.setCustomer(null);
    }

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

    public Customer(String firstname, String lastname, LocalDate dateOfBirth, Adress adress, List<Contract> contracts) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
        this.adress = adress;
        this.contracts = contracts;
    }

    // Basic getter and setter
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

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
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
