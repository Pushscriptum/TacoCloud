package tacos.data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tacos.TacoOrder;
import tacos.User;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<TacoOrder, Long> {
    List<TacoOrder> findByUserOrderByPlacedAtDesc(User user, Pageable pageable);
}
