package com.anhnhvcoder.ecommerce.repository;

import com.anhnhvcoder.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/*
 * Product lists are serialised with brand, category, sizes and images.
 * Brand and category (to-one) are joined in the same query via @EntityGraph.
 * Sizes and images (collections) are NOT joined: joining two collections would
 * multiply rows (and breaks pagination), so they are loaded by Hibernate batch
 * fetching (hibernate.default_batch_fetch_size) - one IN (...) query per
 * collection type for the whole page instead of one query per product.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"brand", "category"})
    List<Product> findByCategoryId(Long categoryId);

    boolean existsByBrandId(int brandId);

    @EntityGraph(attributePaths = {"brand", "category"})
    List<Product> findAllByOrderByIdDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"brand", "category"})
    @Query("SELECT p FROM Product p WHERE p.name LIKE CONCAT('%', :name, '%')")
    List<Product> findByName(@Param("name") String name);

    @Override
    @EntityGraph(attributePaths = {"brand", "category"})
    List<Product> findAll();

    /** Product detail page: brand, category and sizes in a single query; images are batch-loaded. */
    @EntityGraph(attributePaths = {"brand", "category", "size"})
    Optional<Product> findWithDetailsById(Long id);

    /*
     * The filters use plain JOINs (inner join keeps the old behaviour of excluding
     * products without a brand/category) and the entity graph does the fetching.
     * An explicit count query keeps the fetch out of the COUNT(*) that paging runs.
     */
    @EntityGraph(attributePaths = {"brand", "category"})
    @Query(value = """
            SELECT p
                FROM Product p
                JOIN p.brand b
                JOIN p.category c
                WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
                  AND LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))
                  AND LOWER(b.name) LIKE LOWER(CONCAT('%', :brandName, '%'))
            """,
            countQuery = """
            SELECT COUNT(p)
                FROM Product p
                JOIN p.brand b
                JOIN p.category c
                WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
                  AND LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))
                  AND LOWER(b.name) LIKE LOWER(CONCAT('%', :brandName, '%'))
            """)
    Page<Product> searchProducts(Pageable pageable,
                                 @Param("name") String name,
                                 @Param("categoryName") String categoryName,
                                 @Param("brandName") String brandName);

}
