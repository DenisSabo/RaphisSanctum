package vv.assignment.restful.Contract;

import vv.assignment.restful.Customer.Customer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Contract {
    @Id
    @GeneratedValue
    private Long id;

    String kindOfContract;
    BigDecimal yearlyFee;

    /**
     * Enum that restricts possible contracts
     */
    public enum AllowedContracts{
        KRANKENVERSICHERUNG, HAFTPFLICHT, RECHTSSCHUTZ, KFZ
    }

    // Many contracts can be used by one customer
    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.PERSIST)
    @JoinColumn(name="CUSTOMER_ID") // Foreign key
            Customer customer;

    public Contract(){
        // default constructor
    }

    public Contract(AllowedContracts contract, BigDecimal yearlyFee){
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

    public BigDecimal getYearlyFee() {
        return yearlyFee;
    }

    public void setYearlyFee(BigDecimal yearlyFee) {
        this.yearlyFee = yearlyFee;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Do not compare Id's, because only content is relevant
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contract contract = (Contract) o;

        return kindOfContract.equals(contract.kindOfContract) && yearlyFee.equals(contract.yearlyFee);
    }

    /**
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    */
}
