package com.salesmanager.shop.mapper.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.product.definition.PersistableProductDefinition;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.utils.DateUtil;

@Component
public class PersistableProductDefinitionMapper implements Mapper<PersistableProductDefinition, Product> {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private LanguageService languageService;
	@Autowired
	private PersistableProductAttributeMapper persistableProductAttributeMapper;
	
	@Autowired
	private ProductTypeService productTypeService;
	@Override
	public Product convert(PersistableProductDefinition source, MerchantStore store, Language language) {
		Product product = new Product();
		return this.merge(source, product, store, language);
	}

	@Override
	public Product merge(PersistableProductDefinition source, Product destination, MerchantStore store,
			Language language) {

		
		  
	    Validate.notNull(destination,"Product must not be null");

		try {

			destination.setSku(source.getIdentifier());
			destination.setAvailable(source.isVisible());
			destination.setRefSku(source.getIdentifier());
			if(source.getId() != null && source.getId().longValue()==0) {
				destination.setId(null);
			} else {
				destination.setId(source.getId());
			}

			
			//PRODUCT TYPE
			if(!StringUtils.isBlank(source.getType())) {
				ProductType type = productTypeService.getByCode(source.getType(), store, language);
				if(type == null) {
					throw new ConversionException("Product type [" + source.getType() + "] does not exist");
				}

				destination.setType(type);
			}

			
			if(!StringUtils.isBlank(source.getDateAvailable())) {
				destination.setDateAvailable(DateUtil.getDate(source.getDateAvailable()));
			}


			
			destination.setMerchantStore(store);
			
			List<Language> languages = new ArrayList<Language>();
			Set<ProductDescription> descriptions = new HashSet<ProductDescription>();
			if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				for(com.salesmanager.shop.model.catalog.product.ProductDescription description : source.getDescriptions()) {
					
				  ProductDescription productDescription = new ProductDescription();
				  Language lang = languageService.getByCode(description.getLanguage());
	              if(lang==null) {
	                    throw new ConversionException("Language code " + description.getLanguage() + " is invalid, use ISO code (en, fr ...)");
	               }
				   if(!CollectionUtils.isEmpty(destination.getDescriptions())) {
				      for(ProductDescription desc : destination.getDescriptions()) {
				        if(desc.getLanguage().getCode().equals(description.getLanguage())) {
				          productDescription = desc;
				          break;
				        }
				      }
				    }

					productDescription.setProduct(destination);
					productDescription.setDescription(description.getDescription());

					productDescription.setProductHighlight(description.getHighlights());

					productDescription.setName(description.getName());
					productDescription.setSeUrl(description.getFriendlyUrl());
					productDescription.setMetatagKeywords(description.getKeyWords());
					productDescription.setMetatagDescription(description.getMetaDescription());
					productDescription.setTitle(description.getTitle());
					
					languages.add(lang);
					productDescription.setLanguage(lang);
					descriptions.add(productDescription);
				}
			}
			
			if(descriptions.size()>0) {
				destination.setDescriptions(descriptions);
			}

			if(source.getRating() != null) {
				destination.setProductReviewAvg(new BigDecimal(source.getRating()));
			}
			destination.setProductReviewCount(source.getRatingCount());
			
/*			if(CollectionUtils.isNotEmpty(source.getProductPrices())) {



			} else { //create 
			  
			    ProductAvailability productAvailability = null;
			    ProductPrice defaultPrice = null;
			    if(!CollectionUtils.isEmpty(destination.getAvailabilities())) {
			      for(ProductAvailability avail : destination.getAvailabilities()) {
    			        Set<ProductPrice> prices = avail.getPrices();
    			        for(ProductPrice p : prices) {
    			          if(p.isDefaultPrice()) {
    			            if(productAvailability == null) {
    			              productAvailability = avail;
    			              defaultPrice = p;
    			              break;
    			            }
    			            p.setDefaultPrice(false);
    			          }
    			        }
			      }
			    }
				
			    if(productAvailability == null) {
			      productAvailability = new ProductAvailability(destination, store);
			      destination.getAvailabilities().add(productAvailability);
			    }

				productAvailability.setProductQuantity(source.getQuantity());
				productAvailability.setProductQuantityOrderMin(1);
				productAvailability.setProductQuantityOrderMax(1);
				productAvailability.setRegion(Constants.ALL_REGIONS);
				productAvailability.setAvailable(Boolean.valueOf(destination.isAvailable()));


				if(defaultPrice != null) {
				  defaultPrice.setProductPriceAmount(source.getPrice());
				} else {
				    defaultPrice = new ProductPrice();
				    defaultPrice.setDefaultPrice(true);
				    defaultPrice.setProductPriceAmount(source.getPrice());
				    defaultPrice.setCode(ProductPriceEntity.DEFAULT_PRICE_CODE);
				    defaultPrice.setProductAvailability(productAvailability);
	                productAvailability.getPrices().add(defaultPrice);
	                for(Language lang : languages) {
	                
                      ProductPriceDescription ppd = new ProductPriceDescription();
                      ppd.setProductPrice(defaultPrice);
                      ppd.setLanguage(lang);
                      ppd.setName(ProductPriceDescription.DEFAULT_PRICE_DESCRIPTION);
                      defaultPrice.getDescriptions().add(ppd);
                    }
				}

				
				
			}*/

			
			//attributes
			if(source.getProperties()!=null) {
				for(com.salesmanager.shop.model.catalog.product.attribute.PersistableProductAttribute attr : source.getProperties()) {
					ProductAttribute attribute = persistableProductAttributeMapper.convert(attr, store, language);
					
					attribute.setProduct(destination);
					destination.getAttributes().add(attribute);

				}
			}

			
			//categories
			if(!CollectionUtils.isEmpty(source.getCategories())) {
				for(com.salesmanager.shop.model.catalog.category.Category categ : source.getCategories()) {
					
					Category c = null;
					if(!StringUtils.isBlank(categ.getCode())) {
						c = categoryService.getByCode(store, categ.getCode());
					} else {
						Validate.notNull(categ.getId(), "Category id nust not be null");
						c = categoryService.getById(categ.getId(), store.getId());
					}
					
					if(c==null) {
						throw new ConversionException("Category id " + categ.getId() + " does not exist");
					}
					if(c.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
						throw new ConversionException("Invalid category id");
					}
					destination.getCategories().add(c);
				}
			}
			return destination;
		
		} catch (Exception e) {
			throw new ConversionRuntimeException("Error converting product mapper",e);
		}
	}

}