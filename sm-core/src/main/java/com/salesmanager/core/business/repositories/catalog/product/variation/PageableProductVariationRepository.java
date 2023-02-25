package com.salesmanager.core.business.repositories.catalog.product.variation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.salesmanager.core.model.catalog.product.variation.ProductVariation;
import org.springframework.data.repository.query.Param;

public interface PageableProductVariationRepository extends PagingAndSortingRepository<ProductVariation, Long> {


	@Query(value = "select distinct p from ProductVariation p join fetch p.merchantStore pm "
			+ "left join fetch p.productOption po left join fetch po.descriptions "
			+ "left join fetch p.productOptionValue pp left join fetch pp.descriptions "
			+ "where pm.id = :merchantStoreId and (:code is null or p.code like %:code%)",
		    countQuery = "select count(p) from ProductVariation p join p.merchantStore pm where pm.id = :merchantStoreId and (:code is null or p.code like %:code%)")
	Page<ProductVariation> list(@Param("merchantStoreId") int merchantStoreId, @Param("code") String code, Pageable pageable);


}
