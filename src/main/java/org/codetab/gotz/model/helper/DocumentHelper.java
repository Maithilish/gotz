package org.codetab.gotz.model.helper;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.DataFormatException;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.codetab.gotz.di.DInjector;
import org.codetab.gotz.exception.ConfigNotFoundException;
import org.codetab.gotz.exception.FieldsNotFoundException;
import org.codetab.gotz.messages.Messages;
import org.codetab.gotz.model.Document;
import org.codetab.gotz.model.Fields;
import org.codetab.gotz.model.Labels;
import org.codetab.gotz.shared.ConfigService;
import org.codetab.gotz.util.CompressionUtil;
import org.codetab.gotz.util.MarkerUtil;
import org.codetab.gotz.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * Helper routines to handle documents.
 * @author Maithilish
 *
 */
public class DocumentHelper {

    /**
     * logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(Document.class);

    /**
     * ConfigService singleton.
     */
    @Inject
    private ConfigService configService;
    /**
     * DI singleton.
     */
    @Inject
    private DInjector dInjector;

    @Inject
    private FieldsHelper fieldsHelper;

    /**
     * private constructor.
     */
    @Inject
    private DocumentHelper() {
    }

    /**
     * Returns id of document where toDate is &gt;= runDateTime config. When,
     * there are more than one matching documents, then the id of last one is
     * returned. If, list is null then returns null.
     * @param documents
     *            list of {@link Document}, not null
     * @return active document id or null when no matching document is found or
     *         input is empty or null.
     */
    public Long getActiveDocumentId(final List<Document> documents) {
        Validate.validState(configService != null,
                Messages.getString("DocumentHelper.0")); //$NON-NLS-1$

        if (documents == null) {
            return null;
        }
        Long activeDocumentId = null;
        for (Document doc : documents) {
            Date toDate = doc.getToDate();
            Date runDateTime = configService.getRunDateTime();
            // toDate > today
            if (toDate.compareTo(runDateTime) >= 0) {
                activeDocumentId = doc.getId();
            }
        }
        return activeDocumentId;
    }

    public Document getDocument(final Long id, final List<Document> documents) {
        for (Document doc : documents) {
            if (doc.getId() == id) {
                return doc;
            }
        }
        throw new NoSuchElementException(
                Util.join("No document with id [", String.valueOf(id), "]"));
    }

    /**
     * <p>
     * Calculates document expire date from live field and from date.
     * <p>
     * Live field can hold duration (ISO-8601 duration format PnDTnHnMn.nS) or
     * date string. When live is duration then it is added to fromDate else
     * string is parsed as to date based on parse pattern provided by
     * ConfigService.
     * <p>
     * In case, live is not defined or it is zero or blank then from date is
     * returned.
     * @param fromDate
     *            document from date, not null
     * @param fields
     *            list of fields, not null
     * @return a Date which is document expire date, not null
     * @throws org.codetab.gotz.exception.FieldsParseException
     * @see java.time.Duration
     */
    public Date getToDate(final Date fromDate, final Fields fields,
            final Labels labels) {

        Validate.notNull(fromDate, Messages.getString("DocumentHelper.1")); //$NON-NLS-1$
        Validate.notNull(fields, Messages.getString("DocumentHelper.2")); //$NON-NLS-1$

        Validate.validState(configService != null,
                Messages.getString("DocumentHelper.3")); //$NON-NLS-1$

        // convert fromDate to DateTime
        ZonedDateTime fromDateTime = ZonedDateTime
                .ofInstant(fromDate.toInstant(), ZoneId.systemDefault());
        ZonedDateTime toDate = null;

        // extract live value
        String live = null;
        try {
            live = fieldsHelper.getLastValue("/xf:fields/xf:tasks/xf:live", //$NON-NLS-1$
                    fields);
        } catch (FieldsNotFoundException e) {
            LOGGER.warn(Messages.getString("DocumentHelper.5"), //$NON-NLS-1$
                    e.getLocalizedMessage(), labels.getLabel());
        }
        if (StringUtils.equals(live, "0") || StringUtils.isBlank(live)) { //$NON-NLS-1$
            live = "PT0S"; // zero second //$NON-NLS-1$
        }

        // calculate toDate
        try {
            TemporalAmount ta = Util.parseTemporalAmount(live);
            toDate = fromDateTime.plus(ta);
        } catch (DateTimeParseException e) {
            // if live is not Duration string then parse it as Date
            try {
                String[] patterns =
                        configService.getConfigArray("gotz.dateParsePattern"); //$NON-NLS-1$
                // multiple patterns so needs DateUtils
                Date td = DateUtils.parseDateStrictly(live, patterns);
                toDate = ZonedDateTime.ofInstant(td.toInstant(),
                        ZoneId.systemDefault());
            } catch (ParseException | ConfigNotFoundException pe) {
                LOGGER.warn(Messages.getString("DocumentHelper.9"), //$NON-NLS-1$
                        labels.getLabel(), live, e);
                TemporalAmount ta = Util.parseTemporalAmount("PT0S"); //$NON-NLS-1$
                toDate = fromDateTime.plus(ta);
            }
        }

        if (LOGGER.isTraceEnabled()) {
            Marker marker =
                    MarkerUtil.getMarker(labels.getName(), labels.getGroup());
            LOGGER.trace(marker, "document.toDate. [live] {} [toDate]", live, //$NON-NLS-1$
                    toDate);
        }
        return Date.from(Instant.from(toDate));
    }

    /**
     * <p>
     * Get uncompressed bytes of the documentObject.
     * @param document
     *            which has the documentObject, not null
     * @return uncompressed bytes of the documentObject, not null
     * @throws IOException
     *             if error closing stream
     * @throws DataFormatException
     *             if error decompress data
     */
    public byte[] getDocumentObject(final Document document)
            throws DataFormatException, IOException {
        Validate.notNull(document, Messages.getString("DocumentHelper.12")); //$NON-NLS-1$
        Validate.notNull(document.getDocumentObject(),
                Messages.getString("DocumentHelper.13")); //$NON-NLS-1$

        final int bufferLength = 4086;
        return CompressionUtil.decompressByteArray(
                (byte[]) document.getDocumentObject(), bufferLength);
    }

    /**
     * <p>
     * Compresses the documentObject and sets it to Document.
     * @param document
     *            document to set, not null
     * @param documentObject
     *            object to compress and set, not null
     * @return true if success
     * @throws IOException
     *             any exception while compression
     */
    public boolean setDocumentObject(final Document document,
            final byte[] documentObject) throws IOException {
        Validate.notNull(document, Messages.getString("DocumentHelper.14")); //$NON-NLS-1$
        Validate.notNull(documentObject,
                Messages.getString("DocumentHelper.15")); //$NON-NLS-1$

        final int bufferLength = 4086;
        byte[] compressedObject =
                CompressionUtil.compressByteArray(documentObject, bufferLength);
        document.setDocumentObject(compressedObject);
        LOGGER.debug(Messages.getString("DocumentHelper.21"), //$NON-NLS-1$
                documentObject.length, compressedObject.length);
        return true;
    }

    /**
     * <p>
     * Factory method to create Document and set its fields.
     * <p>
     * Uses DI to create the Document.
     * @param name
     *            document name, not null
     * @param url
     *            document URL, not null
     * @param fromDate
     *            document start date, not null
     * @param toDate
     *            document expire date, not null
     * @return document, not null
     */
    public Document createDocument(final String name, final String url,
            final Date fromDate, final Date toDate) {
        Validate.notNull(name, Messages.getString("DocumentHelper.16")); //$NON-NLS-1$
        Validate.notNull(url, Messages.getString("DocumentHelper.17")); //$NON-NLS-1$
        Validate.notNull(fromDate, Messages.getString("DocumentHelper.18")); //$NON-NLS-1$
        Validate.notNull(toDate, Messages.getString("DocumentHelper.19")); //$NON-NLS-1$

        Validate.validState(dInjector != null,
                Messages.getString("DocumentHelper.20")); //$NON-NLS-1$

        Document document = dInjector.instance(Document.class);
        document.setName(name);
        document.setUrl(url);
        document.setFromDate(fromDate);
        document.setToDate(toDate);
        return document;
    }

}
