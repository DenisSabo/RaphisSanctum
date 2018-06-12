package vv.assignment.restful.Customer;

import vv.assignment.restful.Contract.Contract;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Entity
// Real primary key
@Table(uniqueConstraints={@UniqueConstraint(columnNames ={"firstname","lastname", "dateOfBirth", "address_id"})})
public class Customer {
    /**
     * artificial primary key of Customer-table is a auto generated ID value
     */
    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    private String firstname;

    @Size(min = 3, max = 30, message = "Last name must be between 3 and 30 characters")
    private String lastname;

    @NotNull(message = "Date of birth cannot be empty")
    // Possible: Own constraint validator, that says a customer must be at least 18 years old
    private LocalDate dateOfBirth;

    /**
     * The entity has a version number that starts at zero and will be incremented, if entity will be updated.
     * With the help of this field, the lost update problem can be solved (See CustomerService.java -> PutMapping)
     */
    private Integer version = 0;

    @NotNull // Customer needs an address
    @OneToOne(targetEntity = Address.class, cascade = {CascadeType.ALL}) // Customer can only have one address.
    @JoinColumn
    private Address address;

    // Customer can have many contracts (Can be null)
    @OneToMany(targetEntity = Contract.class, fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Contract> contracts;



    // Constructors
    public Customer(){
        //default constructor
    }

    /**
     * Constructor that can be used, if there are currently no contracts to pass
     * @param firstname
     * @param lastname
     * @param dateOfBirth
     * @param address
     */
    public Customer(String firstname, String lastname, LocalDate dateOfBirth, Address address) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    /**
     * @param contracts which the customer has to pay for
     */
    public Customer(String firstname, String lastname, LocalDate dateOfBirth,
                    Address address, List<Contract> contracts) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.contracts = contracts;
    }


    /**
     * Increments version number of entity when entity properties got changed
     */
    protected void increment(){
        this.version++;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }

    /**
     *
     * @param customer that updates this customer
     */
    public void setCustomer(Customer customer) {
        this.firstname = customer.getFirstname();
        this.lastname = customer.getLastname();
        this.dateOfBirth = customer.getDateOfBirth();
        this.address = customer.getAddress();
        this.contracts = customer.getContracts();
    }



    // Equals, hashCode and toString

    /**
     * Tests if values in firstname, lastname, dateOfBirth, address are equal
     * -> Real primary key, consists of
     * This method does not compare the ID of two instances.
     * @param o
     * @return true if
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (!firstname.equals(customer.firstname)) return false;
        if (!lastname.equals(customer.lastname)) return false;
        if (!dateOfBirth.equals(customer.dateOfBirth)) return false;
        return address.equals(customer.address);
    }

    @Override
    public int hashCode() {
        int result = firstname.hashCode();
        result = 31 * result + lastname.hashCode();
        result = 31 * result + dateOfBirth.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + (contracts != null ? contracts.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", address=" + address +
                ", contracts=" + contracts +
                '}';
    }

}
