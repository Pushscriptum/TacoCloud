package tacos.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import tacos.Taco;
import tacos.TacoOrder;

import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private JdbcOperations jdbcOperations;

    @Autowired
    public JdbcOrderRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public TacoOrder save(TacoOrder order) {
        PreparedStatementCreatorFactory psCreatorFactory = getPreparedStatementCreatorFactory();

        order.setPlaceAt(new Date());

        PreparedStatementCreator psCreator = psCreatorFactory.newPreparedStatementCreator(
                Arrays.asList(
                        order.getDeliveryName(),
                        order.getDeliveryStreet(),
                        order.getDeliveryCity(),
                        order.getDeliveryState(),
                        order.getDeliveryZip(),
                        order.getCcNumber(),
                        order.getCcExpiration(),
                        order.getCcCVV(),
                        order.getPlaceAt()
                )
        );

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psCreator, keyHolder);
        long orderId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        order.setId(orderId);

        List<Taco> tacos = order.getTacos();
        int i = 0;

        for (Taco taco : tacos) {
            saveTaco(orderId, i++, taco);
        }

        return order;
    }

    private PreparedStatementCreatorFactory getPreparedStatementCreatorFactory() {
        PreparedStatementCreatorFactory psCreatorFactory = new PreparedStatementCreatorFactory(
                "INSERT INTO Taco_Order "
                        + "(delivery_name, delivery_street, delivery_city, "
                        + "delivery_state, delivery_zip, cc_number, "
                        + "cc_expiration, cc_cvv, placed_at) "
                        + "VALUES (?,?,?,?,?,?,?,?,?)",
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP
        );

        psCreatorFactory.setReturnGeneratedKeys(true);
        return psCreatorFactory;
    }
}
