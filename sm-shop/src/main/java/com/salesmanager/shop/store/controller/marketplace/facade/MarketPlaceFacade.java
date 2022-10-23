package com.salesmanager.shop.store.controller.marketplace.facade;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.system.optin.OptinType;
import com.salesmanager.shop.model.marketplace.ReadableMarketPlace;
import com.salesmanager.shop.model.marketplace.SignupStore;
import com.salesmanager.shop.model.system.ReadableOptin;

/**
 * Builds market places objects for shop and REST api
 * @author c.samson
 *
 */
public interface MarketPlaceFacade {

	void signup(SignupStore store) throws ServiceException;

	/**
	 * Get a MarketPlace from store code
	 * @param store
	 * @param lang
	 * @return
	 * @throws Exception
	 */
	ReadableMarketPlace get(String store, Language lang) ;

	/**
	 * Finds an optin by merchant and type
	 * @param store
	 * @param type
	 * @return
	 * @throws Exception
	 */
	ReadableOptin findByMerchantAndType(MerchantStore store, OptinType type);

}
