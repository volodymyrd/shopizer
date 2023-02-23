package com.salesmanager.core.business.repositories.catalog.product.manufacturer;

import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface PageableManufacturerRepository extends PagingAndSortingRepository<Manufacturer, Long> {

    @Query("select m from Manufacturer m left join m.descriptions md inner join m.merchantStore ms where ms.id=:storeId and md.language.id=:languageId and (:name is null or md.name like %:name%)")
    Page<Manufacturer> findByStore(@Param("storeId") Integer storeId, @Param("languageId") Integer languageId, @Param("name") String name, Pageable pageable);

    @Query("select m from Manufacturer m left join m.descriptions md inner join m.merchantStore ms where ms.id=:storeId and (:name is null or md.name like %:name%)")
    Page<Manufacturer> findByStore(@Param("storeId") Integer storeId, @Param("name") String name, Pageable pageable);


}
