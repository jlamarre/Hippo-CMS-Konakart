package org.onehippo.forge.konakart.hst.beans.compound;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.hippoecm.hst.content.beans.standard.HippoItem;
import org.onehippo.forge.konakart.common.KKCndConstants;

import java.util.List;

@Node(jcrType = KKCndConstants.DOCUMENT_TYPE)
public class Konakart extends HippoItem {

    private Price standardPrice = null;

    public Long getProductId() {
        return getProperty(KKCndConstants.PRODUCT_ID);
    }

    public Long getLanguageId() {
        return getProperty(KKCndConstants.PRODUCT_LANGUAGE_ID);
    }

    public String getProductSku() {
        return getProperty(KKCndConstants.PRODUCT_SKU);
    }

    public String getProductName() {
        return getProperty(KKCndConstants.PRODUCT_NAME);
    }

    public HippoHtml getProductDescription() {
        return getProperty(KKCndConstants.PRODUCT_DESCRIPTION);
    }

    /**
     * @return the standard compound price values.
     * @throws Exception if the compound price could not be loaded
     */
    public Price getStandardPrice() throws Exception {

        if (standardPrice == null) {
            loadStandardPrice();
        }

        if (standardPrice == null) {
            throw new Exception("Unable to load the compoundPrice child");
        }


        return standardPrice;
    }

    public Long getRating() {
        return getProperty(KKCndConstants.PRODUCT_RATING);
    }


    /**
     * Load the compound price.
     */
    private void loadStandardPrice() {
        // Retrieve Konakart node
        List<Price> priceList = getChildBeans(Price.class);

        // Should not happends
        if ((priceList == null) || (priceList.size() == 0)) {
            return;
        }

        standardPrice = priceList.get(0);
    }
}