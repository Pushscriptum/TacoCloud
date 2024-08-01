package tacos.data;

import org.springframework.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import tacos.Ingredient;
import tacos.IngredientRef;
import tacos.Taco;
import tacos.TacoOrder;

import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private final JdbcOperations jdbcOperations;

    @Autowired
    public JdbcOrderRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public TacoOrder save(TacoOrder order) {
        PreparedStatementCreatorFactory psCreatorFactory = getPreparedStatementCreatorFactory();

        order.setPlacedAt(new Date());

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
                        order.getPlacedAt()
                )
        );

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psCreator, keyHolder);
        long orderId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        order.setId(orderId);

        List<Taco> tacos = order.getTacos();
        int i = 1;
        for (Taco taco : tacos) {
            saveTaco(orderId, i++, taco);
        }

        return order;
    }

    private void saveTaco(Long orderId, int i, Taco taco) {
        taco.setCreatedAt(new Date());
        PreparedStatementCreatorFactory psCreatorFactory =
                new PreparedStatementCreatorFactory(
                        "INSERT INTO Taco (name, created_at, taco_order, taco_order_key) VALUES (?, ?, ?, ?)",
                        Types.VARCHAR, Types.TIMESTAMP, Type.LONG, Type.LONG
                );

        psCreatorFactory.setReturnGeneratedKeys(true);

        PreparedStatementCreator psCreator = psCreatorFactory.newPreparedStatementCreator(
                Arrays.asList(
                        taco.getName(),
                        taco.getCreatedAt(),
                        i,
                        orderId
                )
        );

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psCreator, keyHolder);
        long tacoId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        taco.setId(tacoId);

        saveIngredientRefs(tacoId, taco.getIngredients());

    }

    private void saveIngredientRefs(long tacoId, List<IngredientRef> ingredients) {

        int key = 1;
        for (IngredientRef ingredient : ingredients) {
            jdbcOperations.update("INSERT INTO Ingredient_Ref (ingredient, taco, taco_key) VALUES (?, ?, ?)",
                    ingredient.getIngredient(), tacoId, key++);
        }
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
