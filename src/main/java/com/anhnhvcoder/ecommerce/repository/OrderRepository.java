package com.anhnhvcoder.ecommerce.repository;

import com.anhnhvcoder.ecommerce.enums.OrderStatus;
import com.anhnhvcoder.ecommerce.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>{

    /** Not paged, so the whole item tree (items -> size -> product) can be joined in one query. */
    @EntityGraph(attributePaths = {"orderItems", "orderItems.size", "orderItems.size.product"})
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    /*
     * Paged / limited queries only join the customer (to-one). Joining the items
     * collection would make Hibernate paginate in memory, so items, sizes and
     * products are batch-fetched instead (a few IN (...) queries per page).
     * The id tie-breaker keeps paging stable for orders placed on the same day.
     */
    @EntityGraph(attributePaths = {"user"})
    Page<Order> findByStatusOrderByOrderDateDescIdDesc(Pageable pageable, OrderStatus status);

    @EntityGraph(attributePaths = {"user"})
    Page<Order> findAllByOrderByOrderDateDescIdDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    List<Order> findTop3ByOrderByOrderDateDescIdDesc();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = ?1")
    BigDecimal sumTotalAmountByStatus(OrderStatus status);

    Optional<Order> findByOrderCode(Long orderCode);

    boolean existsByUserId(Long userId);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersGroupByStatus();

    /*
     * A date range (instead of YEAR(o.orderDate) = :year) lets MySQL use the
     * (status, order_date) index rather than evaluating a function on every row.
     */
    @Query("""
            SELECT MONTH(o.orderDate), COALESCE(SUM(o.totalAmount), 0), COUNT(o)
            FROM Order o
            WHERE o.status = :status AND o.orderDate >= :from AND o.orderDate < :to
            GROUP BY MONTH(o.orderDate)
            """)
    List<Object[]> sumRevenueByMonth(@Param("status") OrderStatus status,
                                     @Param("from") LocalDate from,
                                     @Param("to") LocalDate to);

}
