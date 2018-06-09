package vv.assignment.restful.Contract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import vv.assignment.restful.Customer.Customer;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Contract {
    @Id
    @GeneratedValue
    private Long id;
    @JsonProperty("kindOfContract")
    AllowedContracts kindOfContract;
    @JsonProperty("yearlyFee")
    BigDecimal yearlyFee;

    /**
     * Enum that restricts possible contracts
     */
    public enum AllowedContracts{
        KRANKENVERSICHERUNG,
        HAFTPFLICHT,
        RECHTSSCHUTZ,
        KFZ;
    }

    // Many contracts can be used by one customer
    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.PERSIST)
    @JoinColumn(name="CUSTOMER_ID") // Foreign key
            Customer customer;

    public Contract(){
        super();
        // default constructor
    }

    @JsonCreator
    public Contract(@JsonProperty("kindOfContract")AllowedContracts kindOfContract, @JsonProperty("yearlyFee")BigDecimal yearlyFee){
        this.kindOfContract = kindOfContract;
        this.yearlyFee = yearlyFee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AllowedContracts getKindOfContract() {
        return kindOfContract;
    }

    public void setKindOfContract(AllowedContracts kindOfContract) {
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
