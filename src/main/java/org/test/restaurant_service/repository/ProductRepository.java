package org.test.restaurant_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.dto.view.ProductIdsView;
import org.test.restaurant_service.dto.view.ProductLocalizedView;
import org.test.restaurant_service.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findAllByTypeId(Integer typeId, Pageable pageable);

    List<Product> findAllByType_Name(String name);

    Optional<Product> findByName(String name);

    @EntityGraph(attributePaths = {"photos", "type"})
    @Query("SELECT DISTINCT p FROM Product  p WHERE  p.id = :id")
    Optional<Product> findByIdWithPhotos(@Param("id") Integer id);

    @Query("SELECT p FROM Product p " +
            "JOIN OrderProduct op ON p.id = op.product.id " +
            "JOIN Order o ON o.id = op.order.id " +
            "WHERE o.createdAt >= CURRENT_DATE - 7 " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(op.id) DESC")
    List<Product> getTopProducts(Pageable pageable);

    boolean existsByName(String name);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchProducts(String searchTerm, Pageable pageable);

    @Query(
            value = """
                    SELECT  p.id,
                            COALESCE(pi.name,   p.name)        AS name,
                            COALESCE(pi.description, p.description) AS description,
                            COALESCE(ptt.name,  pt.name)       AS typeName,
                            p.available                             AS available,
                            p.price,
                            p.cooking_time      AS cookingTime,
                            ( SELECT MIN(ph.url)
                              FROM   restaurant_service.photo ph
                              WHERE  ph.product_id = p.id
                            )                                AS photoUrl
                    FROM    restaurant_service.products p
                    JOIN    restaurant_service.product_types pt
                           ON pt.id = p.type_id
                    LEFT JOIN restaurant_service.product_i18n pi
                           ON pi.product_id = p.id
                          AND pi.lang_id = :langId
                    LEFT JOIN restaurant_service.product_type_i18n ptt
                           ON ptt.product_type_id = pt.id
                          AND ptt.lang_id = :langId
                    WHERE   (:typeId IS NULL OR pt.id = :typeId)
                      AND   LOWER(COALESCE(pi.name, p.name)) LIKE LOWER(CONCAT('%', :term, '%'))
                      ORDER BY p.available DESC
                    """,
            countQuery = """
                    SELECT  COUNT(*)
                    FROM    restaurant_service.products p
                    JOIN    restaurant_service.product_types pt
                           ON pt.id = p.type_id
                    LEFT JOIN restaurant_service.product_i18n pi
                           ON pi.product_id = p.id
                          AND pi.lang_id = :langId
                    WHERE   (:typeId IS NULL OR pt.id = :typeId)
                      AND   LOWER(COALESCE(pi.name, p.name)) LIKE LOWER(CONCAT('%', :term, '%'))
                    """,
            nativeQuery = true)
    Page<ProductLocalizedView> searchLocalized(
            @Param("langId") Integer langId,
            @Param("typeId") Integer typeId,
            @Param("term") String term,
            Pageable pageable
    );

    @Query(
            value = """
                    SELECT  p.id,
                            COALESCE(pi.name, p.name)   AS name,
                            COALESCE(pi.description,p.description) AS description,
                            COALESCE(ptt.name,pt.name)  AS typeName,
                            p.available                             AS available,
                    
                            p.price,
                            p.cooking_time             AS cookingTime,
                            ( SELECT MIN(ph.url)
                              FROM   restaurant_service.photo ph
                              WHERE  ph.product_id = p.id
                            )                            AS photoUrl
                    FROM    restaurant_service.products p
                    JOIN    restaurant_service.product_types pt
                           ON pt.id = p.type_id
                    LEFT JOIN restaurant_service.product_i18n pi
                           ON pi.product_id = p.id
                          AND pi.lang_id = :langId
                    LEFT JOIN restaurant_service.product_type_i18n ptt
                           ON ptt.product_type_id = pt.id
                          AND ptt.lang_id = :langId
                    JOIN    restaurant_service.order_products op
                           ON op.product_id = p.id
                    JOIN    restaurant_service.orders o
                           ON o.id = op.order_id
                    WHERE   o.created_at >= CURRENT_DATE - :days
                      AND   (:typeId IS NULL OR pt.id = :typeId)
                    AND p.available = true
                    GROUP BY p.id, pi.name, pi.description, pt.name, p.price, p.cooking_time, ptt.name
                    ORDER BY SUM(op.quantity) DESC
                    LIMIT   :limit
                    """,
            nativeQuery = true)
    List<ProductLocalizedView> findTopLocalized(
            @Param("langId") Integer langId,
            @Param("typeId") Integer typeId,
            @Param("days") Integer days,
            @Param("limit") Integer limit
    );

    @Query(
            value = """
                    SELECT p.id,
                           COALESCE(pi.name, p.name)               AS name,
                           COALESCE(pi.description, p.description) AS description,
                           COALESCE(ptt.name, pt.name)             AS typeName,
                           p.available                             AS available,
                           p.price,
                           p.cooking_time AS cookingTime,
                           ( SELECT ph.url
                             FROM   restaurant_service.photo ph
                             WHERE  ph.product_id = p.id
                             ORDER  BY ph.id
                             LIMIT 1
                           )                                       AS photoUrl
                    FROM   restaurant_service.products p
                    JOIN   restaurant_service.product_types pt ON pt.id = p.type_id
                    LEFT   JOIN restaurant_service.product_i18n pi
                           ON pi.product_id = p.id AND pi.lang_id = :langId
                    LEFT   JOIN restaurant_service.product_type_i18n ptt
                           ON ptt.product_type_id = pt.id AND ptt.lang_id = :langId
                    WHERE  (:typeId IS NULL OR pt.id = :typeId)
                    ORDER BY p.available DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM   restaurant_service.products p
                    JOIN   restaurant_service.product_types pt ON pt.id = p.type_id
                          LEFT   JOIN restaurant_service.product_i18n pi
                                 ON pi.product_id = p.id
                                AND pi.lang_id = :langId
                    WHERE  (:typeId IS NULL OR pt.id = :typeId)
                    """,
            nativeQuery = true)
    Page<ProductLocalizedView> findAllLocalized(@Param("langId") Integer langId,
                                                @Param("typeId") Integer typeId,
                                                Pageable pageable);

    @Query(
            value = """
                    SELECT p.id,
                           COALESCE(pi.name, p.name)               AS name,
                           COALESCE(pi.description, p.description) AS description,
                           COALESCE(ptt.name, pt.name)             AS typeName,
                           p.available                             AS available,
                           p.price,
                           p.cooking_time AS cookingTime,
                           ( SELECT ph.url
                             FROM   restaurant_service.photo ph
                             WHERE  ph.product_id = p.id
                             ORDER  BY ph.id
                             LIMIT 1
                           )                                       AS photoUrl
                    FROM   restaurant_service.products p
                    JOIN   restaurant_service.product_types pt ON pt.id = p.type_id
                    LEFT   JOIN restaurant_service.product_i18n pi
                           ON pi.product_id = p.id AND pi.lang_id = :langId
                    LEFT   JOIN restaurant_service.product_type_i18n ptt
                           ON ptt.product_type_id = pt.id AND ptt.lang_id = :langId
                    WHERE p.id = :id
                    """,
            nativeQuery = true)
    ProductLocalizedView findOneLocalized(@Param("id") Integer id,
                                          @Param("langId") Integer lang);


    @Query(value = """
                       SELECT p.id as id FROM Product p
            """)
    List<ProductIdsView> getAllProductIds();


    @Modifying
    @Query("UPDATE Product p SET p.available = :available WHERE p.id = :id")
    int updateAvailability(@Param("id") Integer id, @Param("available") boolean available);
}