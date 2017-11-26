package org.codetab.gotz.step.load.encoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.codetab.gotz.exception.FieldsException;
import org.codetab.gotz.exception.StepRunException;
import org.codetab.gotz.model.Activity.Type;
import org.codetab.gotz.model.AxisName;
import org.codetab.gotz.model.Data;
import org.codetab.gotz.model.Fields;
import org.codetab.gotz.model.Member;
import org.codetab.gotz.model.helper.FieldsHelper;
import org.codetab.gotz.shared.ActivityService;
import org.codetab.gotz.step.load.encoder.helper.EncoderHelper;
import org.codetab.gotz.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvRecordEncoder implements IEncoder<List<String>> {

    /**
     * logger.
     */
    static final Logger LOGGER =
            LoggerFactory.getLogger(CsvRecordEncoder.class);

    /**
     * col padding.
     */
    static final int ITEM_COL_SIZE = 30;

    /**
     * fact padding.
     */
    static final int FACT_COL_SIZE = 10;

    private Fields fields;

    @Inject
    private EncoderHelper encoderHelper;
    @Inject
    private FieldsHelper fieldsHelper;
    @Inject
    private ActivityService activityService;

    @Override
    public List<String> encode(final Data data) throws Exception {
        Validate.validState(fields != null, "fields must not be null");
        Validate.validState(data != null, "data must not be null");

        encoderHelper.sort(data, fields);

        // add header
        List<String> encodedData = new ArrayList<>();
        String delimiter = encoderHelper.getDelimiter(fields);
        encodedData.add(getLocatorInfo(delimiter));
        encodedData.add(getColumnHeader(data, delimiter));

        // TODO check is it possible to encode when no sort
        // encode data
        StringBuilder sb = new StringBuilder();
        String prevRow = null;

        for (Member member : data.getMembers()) {
            String row = member.getValue(AxisName.ROW);
            String fact = member.getValue(AxisName.FACT);

            if (prevRow == null) {
                sb.append(StringUtils.rightPad(row, ITEM_COL_SIZE));
                sb.append(" |");
            } else {
                // row break
                if (!prevRow.equals(row)) {
                    encodedData.add(sb.toString()); // output row
                    sb = new StringBuilder();
                    sb.append(StringUtils.rightPad(row, ITEM_COL_SIZE));
                    sb.append(delimiter);
                } else {
                    sb.append(delimiter);
                }
            }
            sb.append(StringUtils.leftPad(fact, FACT_COL_SIZE));
            prevRow = row;
        }
        return encodedData;
    }

    /**
     * <p>
     * Get locator name and group.
     *
     */
    private String getLocatorInfo(final String delimiter) {
        try {
            String locatorName =
                    fieldsHelper.getLastValue("/:fields/:locatorName", fields);
            String locatorGroup =
                    fieldsHelper.getLastValue("/:fields/:locatorGroup", fields);
            return Util.buildString(locatorName, delimiter, locatorGroup);
        } catch (FieldsException e) {
            String message = "unable to get locator name and group";
            LOGGER.error("{} {}", message, Util.getMessage(e));
            LOGGER.debug("{}", e);
            activityService.addActivity(Type.GIVENUP, message, e);
            throw new StepRunException(message, e);
        }
    }

    /**
     * <p>
     * Get column header.
     *
     */
    private String getColumnHeader(final Data data, final String delimiter) {
        String header = StringUtils.rightPad("item", ITEM_COL_SIZE);
        int colCount = getColCount(data);
        for (int c = 0; c < colCount; c++) {
            header += delimiter;
            String col = data.getMembers().get(c).getValue(AxisName.COL);
            header += StringUtils.leftPad(col, FACT_COL_SIZE);
        }
        return header;
    }

    /**
     * <p>
     * Get column count.
     * @return column count.
     */
    private int getColCount(final Data data) {
        Set<String> cols = new HashSet<String>();
        for (Member member : data.getMembers()) {
            cols.add(member.getValue(AxisName.COL));
        }
        return cols.size();
    }

    @Override
    public void setFields(final Fields fields) {
        this.fields = fields;
    }

}