package vv.assignment.restful;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Contract {
    @Id
    @GeneratedValue
    private Long id;

    String kindOfContract;
    Currency yearlyFee; // TODO double?
    private static final Set<String> ALLOWED_CONTRACTS = new HashSet<String>(Arrays.asList("Haftpflicht", "Rechtsschutz", "KFZ"));

    public Contract(){
        // default constructor
    }

    public Contract(String kindOfContract, Currency yearlyFee) throws IllegalArgumentException {
        if(!ALLOWED_CONTRACTS.contains((String) kindOfContract))
            throw new IllegalArgumentException
                    ("Illegal kind of contract. Allowed values: Haftpflicht, Rechtsschutz, KFZ");
        this.kindOfContract = kindOfContract;
        this.yearlyFee = yearlyFee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKindOfContract() {
        return kindOfContract;
    }

    public void setKindOfContract(String kindOfContract) {
        this.kindOfContract = kindOfContract;
    }

    public Currency getYearlyFee() {
        return yearlyFee;
    }

    public void setYearlyFee(Currency yearlyFee) {
        this.yearlyFee = yearlyFee;
    }
}
