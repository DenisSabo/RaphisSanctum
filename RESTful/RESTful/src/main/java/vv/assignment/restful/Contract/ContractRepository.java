package vv.assignment.restful.Contract;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContractRepository extends CrudRepository<Contract, Long>{
}
