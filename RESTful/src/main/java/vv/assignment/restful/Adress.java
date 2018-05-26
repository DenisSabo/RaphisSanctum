package vv.assignment.restful;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;

// Specifies a composite primary key class that is mapped to multiple fields or properties of the entity
@Entity
public class
Adress {
    @Id
    @GeneratedValue
    private Long id;
    String street;
    String postalcode;
    String place;

    // Only one constructor since all three fields/variables are part of the composite primary key
    public Adress(String street, String postalcode, String place) {
        this.street = street;
        this.postalcode = postalcode;
        this.place = place;
    }

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
}
