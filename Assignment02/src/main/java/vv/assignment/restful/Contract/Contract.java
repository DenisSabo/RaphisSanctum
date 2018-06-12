package vv.assignment.restful.Contract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import vv.assignment.restful.Customer.Customer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames ={"kindOfContract","yearlyFee"})}) // Real primary key
public class Contract {
    @Id
    @GeneratedValue
    private Long id;

    @JsonProperty("kindOfContract")
    @NotNull
    AllowedContracts kindOfContract;

    @JsonProperty("yearlyFee")
    @Positive // Only positive values, since yearlyFee for customer is profit for this company
    BigDecimal yearlyFee;

    private Integer version = 0;

    /**
     * Enum containing all possible values for field kindOfContracts
     */
    public enum AllowedContracts{
        KRANKENVERSICHERUNG,
        HAFTPFLICHT,
        RECHTSSCHUTZ,
        KFZ;
    }

    // Many contracts can be used by one customer
    @ManyToOne(targetEntity = Customer.class, fetch = FetchType.EAGER, cascade=CascadeType.PERSIST)
    @JoinColumn(name="customer.id") // Foreign key
            Customer customer;

    /**
     * Can be used if a response entity expects a Contract as param
     */
    public Contract(){
        super();
        // default constructor
    }

    /**
     *
     * @param kindOfContract Allowed values : KRANKENVERSICHERUNG, HAFTPFLICHT, RECHTSSCHUTZ, KFZ
     * @param yearlyFee positive value
     */
    @JsonCreator
    public Contract(@JsonProperty("kindOfContract")AllowedContracts kindOfContract, @JsonProperty("yearlyFee")BigDecimal yearlyFee){
        this.kindOfContract = kindOfContract;
        this.yearlyFee = yearlyFee;
    }



    // Basic getter and setter

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

    public Integer getVersion(){
        return this.version;
    }

    public void increment() {
        this.version++;
    }



    // Equals, hashCode and toString

    /**
     * Test if values in kindOfContract and yearlyFee are the same
     * This method does not compare the ID of two instances.
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

    @Override
    public int hashCode() {
        int result = kindOfContract.hashCode();
        result = 31 * result + yearlyFee.hashCode();
        return result;
    }
}
