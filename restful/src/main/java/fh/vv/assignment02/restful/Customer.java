package fh.vv.assignment02.restful;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity(name = "CUSTOMER")
public class Customer {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "FIRSTNAME")
    String firstname;
    @Column(name = "LASTNAME")
    String lastname;
    @Column(name = "DATEOFBIRTH")
    Date dateOfBirth;

    // Customer has an adress
    // optional false, because customer must have an adress TODO maybe not
    @OneToOne(optional=false)
    // All three columns in table "Adresse" are part of the composite primary key
    @JoinColumns({
            @JoinColumn(name="ADDR_STREET", referencedColumnName="STREET"),
            @JoinColumn(name="ADDR_POSTALCODE", referencedColumnName="POSTALCODE"),
            @JoinColumn(name="ADDR_PLACE", referencedColumnName="PLACE")
    })
    private Adress adress;

    // Customer can have many contracts
    // TODO "mappedBy" ??
    // TODO rename Kundenverträge to Verträge
    @OneToMany(mappedBy = "Customer", targetEntity = CustomerContracts.class, fetch=FetchType.LAZY)
    private Collection contracts;

    // Constructors
    public Customer(){
        //default constructor
    }

    public Customer(String firstname, String lastname, Date dateOfBirth, Adress adress) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
        this.adress = adress;
    }

    public Customer(String firstname, String lastname, Date dateOfBirth, Adress adress, Collection contracts) {
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
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
