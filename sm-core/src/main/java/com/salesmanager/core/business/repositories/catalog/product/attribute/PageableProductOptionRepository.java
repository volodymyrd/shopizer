package com.salesmanager.core.business.repositories.catalog.product.attribute;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import org.springframework.data.repository.query.Param;

public interface PageableProductOptionRepository extends PagingAndSortingRepository<ProductOption, Long> {

    @Query(value = "select distinct p from ProductOption p join fetch p.merchantStore pm left join fetch p.descriptions pd where pm.id = :merchantStoreId and (:name is null or pd.name like %:name%)",
            countQuery = "select count(p) from ProductOption p join p.merchantStore pm left join p.descriptions pd where pm.id = :merchantStoreId and (:name is null or pd.name like %:name%)")
    Page<ProductOption> listOptions(@Param("merchantStoreId") int merchantStoreId, @Param("name") String name, Pageable pageable);
}
