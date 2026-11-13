package com.anhnhvcoder.ecommerce.repository;

import com.anhnhvcoder.ecommerce.dto.LowStockDTO;
import com.anhnhvcoder.ecommerce.model.Size;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface SizeRepository extends JpaRepository<Size, Long> {

    Size findBySizeNameAndProductId(String sizeName, Long productId);

    Size findBySizeName(String sizeName);

    /** Loads every size of an order (and its product, for the price) in one query. */
    @EntityGraph(attributePaths = "product")
    List<Size> findByIdIn(Collection<Long> ids);

    @Query("""
            SELECT new com.anhnhvcoder.ecommerce.dto.LowStockDTO(p.id, p.name, s.id, s.sizeName, s.quantity)
            FROM Size s
            JOIN s.product p
            WHERE s.quantity <= :threshold
            ORDER BY s.quantity ASC
            """)
    List<LowStockDTO> findLowStock(@Param("threshold") int threshold);
}
