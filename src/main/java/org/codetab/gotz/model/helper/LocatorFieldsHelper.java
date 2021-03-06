package org.codetab.gotz.model.helper;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.Validate;
import org.codetab.gotz.exception.ConfigNotFoundException;
import org.codetab.gotz.exception.CriticalException;
import org.codetab.gotz.exception.FieldsException;
import org.codetab.gotz.exception.FieldsNotFoundException;
import org.codetab.gotz.helper.IOHelper;
import org.codetab.gotz.messages.Messages;
import org.codetab.gotz.model.Fields;
import org.codetab.gotz.shared.BeanService;
import org.codetab.gotz.shared.ConfigService;
import org.codetab.gotz.util.Util;
import org.codetab.gotz.util.XmlUtils;
import org.w3c.dom.Document;

import com.google.inject.Singleton;

@Singleton
public class LocatorFieldsHelper {

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(LocatorFieldsHelper.class);

    /**
     * instance of BeanService.
     */
    @Inject
    private BeanService beanService;

    @Inject
    private ConfigService configService;

    @Inject
    private FieldsHelper fieldsHelper;

    @Inject
    private IOHelper ioHelper;

    private List<Fields> fieldsList;

    /**
     * private constructor.
     */
    @Inject
    private LocatorFieldsHelper() {
    }

    /**
     * initializes the helper by assigning step and class fields to state
     * variables.
     * @return true if able to set step and class fields else returns false
     * @throws ParserConfigurationException
     */
    public boolean init() {
        Validate.validState(beanService != null,
                Messages.getString("LocatorFieldsHelper.0")); //$NON-NLS-1$

        try {
            fieldsList = getFields();
        } catch (FieldsException | FieldsNotFoundException e) {
            throw new CriticalException(e);
        }
        return true;
    }

    public Fields getFields(final String clazz, final String group)
            throws FieldsException {
        // return deep copy
        for (Fields fields : fieldsList) {
            if (fields.getClazz().equals(clazz)
                    && fields.getGroup().equals(group)) {
                return fieldsHelper.deepCopy(fields);
            }
        }
        // if not found, return empty fields
        Fields fields = fieldsHelper.createFields();
        fields.setName("locator"); //$NON-NLS-1$
        fields.setGroup(group);
        fields.setClazz(clazz);
        return fields;
    }

    private List<Fields> getFields()
            throws FieldsException, FieldsNotFoundException {

        List<Fields> flist = new ArrayList<>();

        List<Fields> xBeans = beanService.getBeans(Fields.class);
        for (Fields xBean : xBeans) {
            // merge global steps to tasks steps
            String defaultNs = XmlUtils.getDefaultNs(xBean.getNodes().get(0));
            Document doc;
            try {
                doc = XmlUtils.createDocument(xBean.getNodes(), "fields", null, //$NON-NLS-1$
                        defaultNs);
            } catch (ParserConfigurationException e) {
                throw new FieldsException(
                        Messages.getString("LocatorFieldsHelper.3"), //$NON-NLS-1$
                        e);
            }
            Document tdoc = mergeSteps(doc);
            Document effectiveDoc = prefixNamespace(tdoc);

            // split on tasks to new Fields
            Fields holder = new Fields();
            holder.getNodes().add(effectiveDoc);
            List<Fields> newFields =
                    fieldsHelper.split("/xf:fields/xf:tasks", holder); //$NON-NLS-1$

            // set new fields fields
            for (Fields fields : newFields) {
                fields.setName(xBean.getName());
                fields.setClazz(xBean.getClazz());
                fields.setGroup(getGroupFromNodes(fields));
            }

            flist.addAll(newFields);
        }
        return flist;
    }

    private String getGroupFromNodes(final Fields fields)
            throws FieldsNotFoundException {
        String xpath = "/xf:fields/xf:tasks/@group"; //$NON-NLS-1$
        return fieldsHelper.getLastValue(xpath, fields);
    }

    private Document mergeSteps(final Document doc) throws FieldsException {
        String xslFile = ""; //$NON-NLS-1$
        try {
            xslFile = configService.getConfig("gotz.stepsXslFile"); //$NON-NLS-1$
            return transform(xslFile, doc);
        } catch (ConfigNotFoundException | FileNotFoundException
                | TransformerFactoryConfigurationError
                | TransformerException e) {
            throw new FieldsException(
                    Util.join(Messages.getString("LocatorFieldsHelper.8"), //$NON-NLS-1$
                            xslFile, "]"), //$NON-NLS-1$
                    e);
        }
    }

    private Document prefixNamespace(final Document doc)
            throws FieldsException {
        String xslFile = ""; //$NON-NLS-1$
        try {
            xslFile = configService.getConfig("gotz.fieldsNsXslFile"); //$NON-NLS-1$
            return transform(xslFile, doc);
        } catch (ConfigNotFoundException | FileNotFoundException
                | TransformerFactoryConfigurationError
                | TransformerException e) {
            throw new FieldsException(
                    Util.join(Messages.getString("LocatorFieldsHelper.12"), //$NON-NLS-1$
                            xslFile, "]"), //$NON-NLS-1$
                    e);
        }
    }

    private Document transform(final String xslFile, final Document doc)
            throws FieldsException, FileNotFoundException,
            TransformerFactoryConfigurationError, TransformerException {

        StreamSource xslSource = ioHelper.getStreamSource(xslFile);
        DOMResult domResult = new DOMResult();

        Transformer tr =
                TransformerFactory.newInstance().newTransformer(xslSource);
        tr.transform(new DOMSource(doc), domResult);

        Document tDoc = (Document) domResult.getNode();
        return tDoc;
    }

}
