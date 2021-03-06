package org.onehippo.forge.konakart.hst.tags;

import com.konakart.app.DataDescriptor;
import com.konakart.app.KKException;
import com.konakart.appif.DataDescriptorIf;
import com.konakart.appif.ReviewIf;
import com.konakart.appif.ReviewsIf;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class RatingTag extends KKTagSupport {

    protected String var;
    protected String scope;

    protected KKProductDocument kkProductDocument;

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setProduct(KKProductDocument kkProductDocument) {
        this.kkProductDocument = kkProductDocument;
    }

    /* (non-Javadoc)
    * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
    */
    @Override
    public int doStartTag() throws JspException {
        if (var != null) {
            pageContext.removeAttribute(var, PageContext.PAGE_SCOPE);
        }

        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {

        Double ratingValue = 0D;

        if (kkProductDocument != null) {
            DataDescriptorIf dataDescriptorIf = new DataDescriptor();
            dataDescriptorIf.setShowInvisible(false);

            try {
                ReviewsIf reviewsIf = getKkAppEng().getEng().getReviewsPerProduct(dataDescriptorIf,
                        kkProductDocument.getProductId());

                if (reviewsIf.getTotalNumReviews() == 0) {
                    return 0;
                }

                // Retreive the reviews.
                ReviewIf[] reviews = reviewsIf.getReviewArray();

                // Double check...
                if (reviews != null && reviews.length > 0) {
                    double rating = 0;

                    for (ReviewIf reviewIf : reviews) {
                        rating += reviewIf.getRating();
                    }

                    ratingValue = rating / reviews.length;
                }
            } catch (KKException e) {
                ratingValue = 0D;
            }
        }

        writeOrSetVar(ratingValue);

        return EVAL_PAGE;
    }

    private void writeOrSetVar(Double rating) throws JspException {
        if (var == null) {
            JspWriter writer = pageContext.getOut();
            try {
                writer.write(rating.toString());
            } catch (IOException e) {
                throw new JspException("IOException while trying to write script tag", e);
            }
        } else {
            int varScope = PageContext.PAGE_SCOPE;
            if (this.scope != null) {
                if ("request".equals(this.scope)) {
                    varScope = PageContext.REQUEST_SCOPE;
                } else if ("session".equals(this.scope)) {
                    varScope = PageContext.SESSION_SCOPE;
                } else if ("application".equals(this.scope)) {
                    varScope = PageContext.APPLICATION_SCOPE;
                }
            }
            pageContext.setAttribute(var, rating, varScope);
        }
    }
}
