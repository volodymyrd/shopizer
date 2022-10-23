package com.salesmanager.shop.store.api.v1.marketplace;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.marketplace.ReadableMarketPlace;
import com.salesmanager.shop.model.marketplace.SignupStore;
import com.salesmanager.shop.store.controller.marketplace.facade.MarketPlaceFacade;
import com.salesmanager.shop.utils.LanguageUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1")
public class MarketPlaceApi {

    private final MarketPlaceFacade marketPlaceFacade;

    private final LanguageUtils languageUtils;

    public MarketPlaceApi(MarketPlaceFacade marketPlaceFacade, LanguageUtils languageUtils) {
        this.marketPlaceFacade = marketPlaceFacade;
        this.languageUtils = languageUtils;
    }

    /**
     * Get a marketplace from storeCode returns market place details and
     * merchant store
     */
    @GetMapping("/private/marketplace/{store}")
    @ApiOperation(httpMethod = "GET", value = "Get market place meta-data", notes = "", produces = "application/json", response = ReadableMarketPlace.class)
    public ReadableMarketPlace marketPlace(@PathVariable String store,
                                           @RequestParam(value = "lang", required = false) String lang) {

        Language language = languageUtils.getServiceLanguage(lang);
        return marketPlaceFacade.get(store, language);
    }

    // signup new merchant
    @PostMapping("/store/signup")
    @ApiOperation(httpMethod = "POST", value = "Signup store", notes = "", produces = "application/json", response = Void.class)
    public void signup(@RequestBody SignupStore store) throws ServiceException {
        marketPlaceFacade.signup(store);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = {"/store/{store}/signup/{token}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "Validate store signup token", notes = "", response = Void.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
            @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")})
    public void storeSignupVerify(@PathVariable String store, @PathVariable String token,
                                  @ApiIgnore MerchantStore merchantStore,
                                  @ApiIgnore Language language) {

        /**
         * Receives signup token. Needs to validate if a store
         * to validate if token has expired
         *
         * If no problem void is returned otherwise throw OperationNotAllowed
         */

        //TBD

    }
}
