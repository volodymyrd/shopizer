package com.salesmanager.shop.store.controller.marketplace.facade;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.system.optin.OptinService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.system.optin.Optin;
import com.salesmanager.core.model.system.optin.OptinType;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.GroupType;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.marketplace.ReadableMarketPlace;
import com.salesmanager.shop.model.marketplace.SignupStore;
import com.salesmanager.shop.model.references.PersistableAddress;
import com.salesmanager.shop.model.security.PersistableGroup;
import com.salesmanager.shop.model.store.PersistableMerchantStore;
import com.salesmanager.shop.model.store.ReadableMerchantStore;
import com.salesmanager.shop.model.system.ReadableOptin;
import com.salesmanager.shop.model.user.PersistableUser;
import com.salesmanager.shop.model.user.ReadableUser;
import com.salesmanager.shop.populator.system.ReadableOptinPopulator;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.store.facade.StoreFacade;
import com.salesmanager.shop.store.controller.user.facade.UserFacade;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class MarketPlaceFacadeImpl implements MarketPlaceFacade {

    private final UserFacade userFacade;
    private final StoreFacade storeFacade;

    private final GroupService groupService;
    private final OptinService optinService;

    public MarketPlaceFacadeImpl(UserFacade userFacade,
                                 StoreFacade storeFacade,
                                 GroupService groupService,
                                 OptinService optinService) {
        this.userFacade = userFacade;
        this.storeFacade = storeFacade;
        this.groupService = groupService;
        this.optinService = optinService;
    }

    @Override
    @Transactional
    public void signup(SignupStore store) throws ServiceException {
        ReadableUser user = null;
        try {
            // check if user exists
            user = userFacade.findByUserName(store.getEmail());

        } catch (ResourceNotFoundException ignore) {//that is what will happen if user does not exists
        }

        if (user != null) {
            throw new OperationNotAllowedException(
                    "User [" + store.getEmail() + "] already exist and cannot be registered");
        }

        // check if store exists
        if (storeFacade.existByCode(store.getCode())) {
            throw new OperationNotAllowedException(
                    "Store [" + store.getCode() + "] already exist and cannot be registered");
        }

        // create store
        PersistableAddress address = new PersistableAddress();
        address.setAddress(store.getAddress());
        address.setCountry(store.getCountry());
        address.setCity(store.getCity());
        address.setPostalCode(store.getPostalCode());
        address.setStateProvince(store.getStateProvince());
        address.setActive(true);
        PersistableMerchantStore persistableMerchantStore = new PersistableMerchantStore();
        persistableMerchantStore.setCode(store.getCode());
        persistableMerchantStore.setName(store.getName());
        persistableMerchantStore.setEmail(store.getEmail());
        persistableMerchantStore.setAddress(address);
        persistableMerchantStore.setDefaultLanguage(store.getDefaultLang());
        persistableMerchantStore.setSupportedLanguages(List.of(store.getDefaultLang()));
        storeFacade.create(persistableMerchantStore);

        // create user
        PersistableUser persistableUser = new PersistableUser();
        persistableUser.setUserName(store.getEmail());
        persistableUser.setEmailAddress(store.getEmail());
        persistableUser.setFirstName(store.getFirstName());
        persistableUser.setLastName(store.getLastName());
        persistableUser.setPassword(store.getPassword());
        persistableUser.setRepeatPassword(store.getRepeatPassword());
        persistableUser.setActive(true);
        for (Group group : groupService.listGroup(GroupType.ADMIN)) {
            if (group.getGroupName().equals(Constants.GROUP_ADMIN)) {
                PersistableGroup persistableGroup = new PersistableGroup();
                persistableGroup.setName(group.getGroupName());
                persistableGroup.setType(group.getGroupType().name());
                persistableUser.getGroups().add(persistableGroup);
                break;
            }
        }
        userFacade.create(persistableUser, storeFacade.get(persistableMerchantStore.getCode()));

        // send notification
    }

    @Override
    public ReadableMarketPlace get(String store, Language lang) {
        ReadableMerchantStore readableStore = storeFacade.getByCode(store, lang);
        return createReadableMarketPlace(readableStore);
    }

    private ReadableMarketPlace createReadableMarketPlace(ReadableMerchantStore readableStore) {
        //TODO add info from Entity
        ReadableMarketPlace marketPlace = new ReadableMarketPlace();
        marketPlace.setStore(readableStore);
        return marketPlace;
    }

    @Override
    public ReadableOptin findByMerchantAndType(MerchantStore store, OptinType type) {
        Optin optin = getOptinByMerchantAndType(store, type);
        return convertOptinToReadableOptin(store, optin);
    }

    private Optin getOptinByMerchantAndType(MerchantStore store, OptinType type) {
        try {
            return Optional.ofNullable(optinService.getOptinByMerchantAndType(store, type))
                    .orElseThrow(() -> new ResourceNotFoundException("Option not found"));
        } catch (ServiceException e) {
            throw new ServiceRuntimeException(e);
        }

    }

    private ReadableOptin convertOptinToReadableOptin(MerchantStore store, Optin optin) {
        try {
            ReadableOptinPopulator populator = new ReadableOptinPopulator();
            return populator.populate(optin, null, store, null);
        } catch (ConversionException e) {
            throw new ConversionRuntimeException(e);
        }

    }

}
