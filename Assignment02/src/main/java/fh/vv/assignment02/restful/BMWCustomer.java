package fh.vv.assignment02.restful;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Collection;
import java.util.Date;

@Entity(name = "BMW_CUSTOMER")
public class BMWCustomer extends Customer {
    @Column
    String extraField;

    public BMWCustomer() {
    }

    public BMWCustomer(String firstname, String lastname, Date dateOfBirth, Adress adress) {
        super(firstname, lastname, dateOfBirth, adress);
    }

    public BMWCustomer(String firstname, String lastname, Date dateOfBirth, Adress adress, String extraField) {
        super(firstname, lastname, dateOfBirth, adress);
        this.extraField = extraField;
    }

    public BMWCustomer(String firstname, String lastname, Date dateOfBirth, Adress adress, Collection contracts) {
        super(firstname, lastname, dateOfBirth, adress, contracts);
    }

    public BMWCustomer(String firstname, String lastname, Date dateOfBirth, Adress adress, Collection contracts, String extraField) {
        super(firstname, lastname, dateOfBirth, adress, contracts);
        this.extraField = extraField;
    }

    public String getExtraField() {
        return extraField;
    }

    public void setExtraField(String extraField) {
        this.extraField = extraField;
    }
}
