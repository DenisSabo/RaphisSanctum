package fh.vv.assignment02.restful;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.*;

@Entity(name = "KUNDEN_VERTRAEGE")
public class CustomerContracts {
    @Id
    @GeneratedValue
    private Long id;

    String kindOfContract; // TODO mögliche Werte: Haftpflicht, Rechtsschutz, KFZ
    Double yearlyFee; // TODO double?
    public static final Set<String> ALLOWED_CONTRACTS = new HashSet<String>(Arrays.asList("Haftpflicht", "Rechtsschutz", "KFZ"));

    public CustomerContracts(String kindOfContract, Double yearlyFee) throws IllegalArgumentException {
        // TODO andere elegantere Lösungsmöglichkeit?
        if(!ALLOWED_CONTRACTS.contains((String) kindOfContract))
            throw new IllegalArgumentException("Possible kinds of contracts: Haftpflicht, Rechtsschutz, KFZ");
        this.kindOfContract = kindOfContract;
        this.yearlyFee = yearlyFee;
    }
}
