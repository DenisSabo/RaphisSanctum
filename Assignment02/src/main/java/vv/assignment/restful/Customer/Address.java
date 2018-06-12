package vv.assignment.restful.Customer;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

// Specifies a composite primary key class that is mapped to multiple fields or properties of the entity
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames ={"street","postalcode", "place"})}) // Real primary key
public class
Address {
    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 5, message = "Street name must be at least 5 characters long")
    String street;

    @NotEmpty
    // possible: own constraint validator to check, if passed number, is really a postal code (or own table plus f. k.)
    String postalcode;

    @Size(min = 3, message = "Place must be at least 3 characters long")
    String place;

    @OneToOne(mappedBy = "address")
    Customer customer;

    /**
     * Can be used for generating empty address (for example in a response entity that expects a address as input)
     */
    public Address(){
        super();
        // default
    }

    /**
     * All three fields/variables are part of the composite primary key
     * @param street
     * @param postalcode
     * @param place
     */
    public Address(String street, String postalcode, String place) {
        this.street = street;
        this.postalcode = postalcode;
        this.place = place;
    }

    // Basic getter and setter
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Equals, hashCode and toString

    /**
     * Tests if values in street, postalcode and place are equal (real primary key -> unique constraint)
     * This method does not compare the ID of two instances.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (!street.equals(address.street)) return false;
        if (!postalcode.equals(address.postalcode)) return false;
        return place.equals(address.place);
    }

    @Override
    public int hashCode() {
        int result = street.hashCode();
        result = 31 * result + postalcode.hashCode();
        result = 31 * result + place.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", postalcode='" + postalcode + '\'' +
                ", place='" + place + '\'' +
                '}';
    }
}
