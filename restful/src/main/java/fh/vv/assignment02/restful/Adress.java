package fh.vv.assignment02.restful;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

// Specifies a composite primary key class that is mapped to multiple fields or properties of the entity
@IdClass(Adress.class)

@Entity(name = "ADRESS")
public class Adress {
    // Composite primary key, consisting of street, postalcode, place
    @Id @Column(name = "STREET")
    String street;
    @Id @Column(name = "POSTALCODE")
    String postalcode;
    @Id @Column(name = "PLACE")
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
