package fh.vv.assignment02.restful;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.*;

@Entity
public class Contract {
    @Id
    @GeneratedValue
    private Long id;

    String kindOfContract; // TODO mögliche Werte: Haftpflicht, Rechtsschutz, KFZ -> Oder egal?
    Double yearlyFee; // TODO double?
    private static final Set<String> ALLOWED_CONTRACTS = new HashSet<String>(Arrays.asList("Haftpflicht", "Rechtsschutz", "KFZ"));

    public Contract(){
        // default constructor
    }

    public Contract(String kindOfContract, Double yearlyFee) throws IllegalArgumentException {
        // TODO andere elegantere Lösungsmöglichkeit?
        if(!ALLOWED_CONTRACTS.contains((String) kindOfContract))
            throw new IllegalArgumentException("Possible kinds of contracts: Haftpflicht, Rechtsschutz, KFZ");
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

    public Double getYearlyFee() {
        return yearlyFee;
    }

    public void setYearlyFee(Double yearlyFee) {
        this.yearlyFee = yearlyFee;
    }
}
