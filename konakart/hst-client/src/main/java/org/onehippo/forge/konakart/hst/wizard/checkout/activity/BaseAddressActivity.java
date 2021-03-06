package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.app.Address;
import com.konakart.app.CustomerRegistration;
import com.konakart.app.KKException;
import com.konakart.appif.AddressIf;
import com.konakart.appif.CustomerRegistrationIf;
import com.konakart.appif.ZoneIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.onehippo.forge.konakart.hst.utils.KKUtil.checkMandatoryField;

public abstract class BaseAddressActivity extends BaseCheckoutActivity {

    private static final String COUNTRIES = "countries";
    private static final String PROVINCES = "provinces";
    private static final String ADDRESSES = "addresses";


    public static final String EMAIL = "email";
    public static final String DATEOFBIRTH = "dateofbirth";
    public static final String ADDRESS = "address";
    public static final String GENDER = "gender";
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String COMPANYNAME = "companyname";
    public static final String STREETADDRESS = "streetaddress";
    public static final String SUBURB = "suburb";
    public static final String POSTALCODE = "postalcode";
    public static final String CITY = "city";
    public static final String STATEPROVINCE = "stateprovince";
    public static final String COUNTRY = "country";
    public static final String PRIMARYTELEPHONE = "primarytelephone";
    public static final String OTHERTELEPHONE = "othertelephone";
    public static final String DAY = "day";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String FAXNUMBER = "faxnumber";
    public static final String SAVEINADDRESSBOOK = "saveinaddressbook";
    public static final String PASSWORD = "password";
    public static final String PASSWORD_CONFIRMATION = "passwordConfirmation";

    @Override
    public void doBeforeRender() throws ActivityException {

        if (!validateCurrentCart()) {
            return;
        }

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        try {
            seedData.getRequest().setAttribute(COUNTRIES,
                    KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getAllCountries());

            // Set the province if has been selected
            FormField countryField = formMap.getField(COUNTRY);

            if (countryField != null) {

                int country = StringUtils.isNotBlank(countryField.getValue()) ? Integer.valueOf(countryField.getValue()) : -1;
                ZoneIf[] zones = KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getEng().getZonesPerCountry(country);

                List<String> stateProvinces = new ArrayList<String>();

                for (ZoneIf zone : zones) {
                    stateProvinces.add(zone.getZoneName());
                }

                seedData.getRequest().setAttribute(PROVINCES, stateProvinces);
            }
        } catch (KKException e) {
            log.error("Failed to retrieve the list of provinces - {} ", e.toString());
        }

        // Initialize the list of addresses already created.
        if (!seedData.getKkBaseHstComponent().isGuestCustomer(hstRequest)) {
            try {
                KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getCustomerMgr().populateCurrentCustomerAddresses(/* force */ false);

                AddressIf[] addresses = KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getCustomerMgr().getCurrentCustomer().getAddresses();

                seedData.getRequest().setAttribute(ADDRESSES, addresses);

            } catch (Exception e) {
                log.warn("Failed to load customer addresses - {}", e.toString());
            }
        }
    }


    /**
     * Create a new address for the customer
     *
     * @return a created address
     */
    protected AddressIf createAddressForCustomer() {

        AddressIf address = new Address();


        FormField formField = formMap.getField(GENDER);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setGender(formField.getValue());
        }

        formField = formMap.getField(FIRSTNAME);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setFirstName(formField.getValue());
        }

        formField = formMap.getField(LASTNAME);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setLastName(formField.getValue());
        }

        formField = formMap.getField(COMPANYNAME);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setCompany(formField.getValue());
        }

        formField = formMap.getField(STREETADDRESS);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setStreetAddress(formField.getValue());
        }

        formField = formMap.getField(SUBURB);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setSuburb(formField.getValue());
        }

        formField = formMap.getField(POSTALCODE);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setPostcode(formField.getValue());
        }

        formField = formMap.getField(CITY);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setCity(formField.getValue());
        }

        formField = formMap.getField(STATEPROVINCE);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setState(formField.getValue());
        }

        formField = formMap.getField(COUNTRY);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setCountryId(Integer.parseInt(formField.getValue()));
        }

        formField = formMap.getField(PRIMARYTELEPHONE);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setTelephoneNumber(formField.getValue());
        }

        formField = formMap.getField(OTHERTELEPHONE);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setTelephoneNumber1(formField.getValue());
        }

        return address;
    }

    /**
     * Create a customer registration from the onecheckout form
     *
     * @return a created customer registration
     */
    protected CustomerRegistrationIf createCustomerRegistration() {
        CustomerRegistrationIf customerRegistration = new CustomerRegistration();

        FormField formField = formMap.getField(GENDER);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setGender(formField.getValue());
        }

        formField = formMap.getField(FIRSTNAME);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setFirstName(formField.getValue());
        }

        formField = formMap.getField(LASTNAME);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setLastName(formField.getValue());
        }

        formField = formMap.getField(EMAIL);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setEmailAddr(formField.getValue());
        }

        formField = formMap.getField(DATEOFBIRTH);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setBirthDate(computeBirthDate(formField.getValue()));
        }

        formField = formMap.getField(COMPANYNAME);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setCompany(formField.getValue());
        }

        formField = formMap.getField(STREETADDRESS);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setStreetAddress(formField.getValue());
        }

        formField = formMap.getField(SUBURB);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setSuburb(formField.getValue());
        }

        formField = formMap.getField(POSTALCODE);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setPostcode(formField.getValue());
        }

        formField = formMap.getField(CITY);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setCity(formField.getValue());
        }

        formField = formMap.getField(STATEPROVINCE);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setState(formField.getValue());
        }

        formField = formMap.getField(COUNTRY);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setCountryId(Integer.parseInt(formField.getValue()));
        }

        formField = formMap.getField(PRIMARYTELEPHONE);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setTelephoneNumber(formField.getValue());
        }

        formField = formMap.getField(OTHERTELEPHONE);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setTelephoneNumber1(formField.getValue());
        }

        return customerRegistration;
    }

    @Override
    public boolean doValidForm() {
        super.doValidForm();

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        boolean result = true;

        String errorMessage = seedData.getBundleAsString("checkout.mandatory.field");

        if (seedData.getAction().equals(KKCheckoutConstants.ACTIONS.SELECT.name())) {

            String addressId = formMap.getField(ADDRESS).getValue();

            if (StringUtils.isNotEmpty(addressId) && !"-1".equals(addressId)) {
                return result;
            }

            result = checkMandatoryField(formMap, GENDER, errorMessage);
            result = result & checkMandatoryField(formMap, FIRSTNAME, errorMessage);
            result = result & checkMandatoryField(formMap, LASTNAME, errorMessage);
            result = result & checkMandatoryField(formMap, STREETADDRESS, errorMessage);
            result = result & checkMandatoryField(formMap, POSTALCODE, errorMessage);
            result = result & checkMandatoryField(formMap, CITY, errorMessage);
            result = result & checkMandatoryField(formMap, STATEPROVINCE, errorMessage);
            result = result & checkMandatoryField(formMap, COUNTRY, errorMessage);

            if (isCheckoutAsRegister() || isCheckoutAsGuest()) {
                result = result & checkMandatoryField(formMap, EMAIL, errorMessage);
                result = result & checkMandatoryField(formMap, DATEOFBIRTH, errorMessage);

                // Valid if the a customer has already this email
                String emailAddress = formMap.getField(EMAIL).getValue();

                try {
                    // Check if the email already exists
                    if (kkAppEng.getEng().doesCustomerExistForEmail(emailAddress)) {

                        String password = formMap.getField(PASSWORD).getValue();

                        // Try to logged-in
                        boolean isLoggedIn = KKServiceHelper.getKKEngineService().loggedIn(hstRequest, hstResponse, emailAddress, password);

                        if (!isLoggedIn) {
                            // login has failed
                            result = false;
                            addMessage(GLOBALMESSAGE, seedData.getBundleAsString("checkout.email.alreadyexits.field"));
                        }
                    }
                } catch (KKException e) {
                    log.error("Failed to validate if the email set during the checkout process already exists.", e);
                }
            }

            if (isCheckoutAsRegister()) {
                result = result & checkMandatoryField(formMap, PASSWORD, errorMessage);
                result = result & checkMandatoryField(formMap, PASSWORD_CONFIRMATION, errorMessage);
            }

        }

        return result;
    }


    /**
     * This method must be overrided if you implement a different onepagecheckout
     *
     * @return the onePageCheckout formMapFields
     */
    public String[] getCheckoutFormMapFields() {
        return new String[]{GENDER, FIRSTNAME, LASTNAME, EMAIL, DATEOFBIRTH, DAY, MONTH, YEAR, COMPANYNAME
                , STREETADDRESS, SUBURB, POSTALCODE, CITY, STATEPROVINCE, COUNTRY, PRIMARYTELEPHONE
                , OTHERTELEPHONE, FAXNUMBER, SAVEINADDRESSBOOK, ADDRESS, PASSWORD, PASSWORD_CONFIRMATION};
    }

    /**
     * Convert the date of birth to calendar
     *
     * @param birthDate to convert
     * @return the converted date
     */
    protected Calendar computeBirthDate(String birthDate) {
        String DATE_PATTERN = "yyyy/MM/dd";

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        try {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(sdf.parse(birthDate));

            return calendar;
        } catch (ParseException e) {
            log.error("Failed to convert the date. The current date will be set.");
            return GregorianCalendar.getInstance();
        }
    }


}
