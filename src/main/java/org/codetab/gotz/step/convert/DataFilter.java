package org.codetab.gotz.step.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.Validate;
import org.codetab.gotz.exception.DataDefNotFoundException;
import org.codetab.gotz.exception.FieldsNotFoundException;
import org.codetab.gotz.exception.StepRunException;
import org.codetab.gotz.model.Activity.Type;
import org.codetab.gotz.model.Axis;
import org.codetab.gotz.model.AxisName;
import org.codetab.gotz.model.Fields;
import org.codetab.gotz.model.Member;
import org.codetab.gotz.step.IStep;
import org.codetab.gotz.step.StepState;
import org.codetab.gotz.step.base.BaseConverter;
import org.codetab.gotz.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Filters data.
 * @author Maithilish
 *
 */
public final class DataFilter extends BaseConverter {

    /**
     * logger.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DataFilter.class);

    @Override
    public IStep instance() {
        return this;
    }

    /**
     * Filters data. Obtains filter fields from DataDefService and creates a
     * list of members for removal and removes matching members from data.
     */
    @Override
    public boolean process() {
        Validate.validState(getData() != null, "data must not be null");

        List<Member> forRemovalMembers = new ArrayList<Member>();
        Map<AxisName, Fields> filterMap = null;
        try {
            filterMap = dataDefService.getFilterMap(getData().getDataDef());
        } catch (DataDefNotFoundException e) {
            String givenUpMessage = "unable to filter";
            LOGGER.error("{} {}", givenUpMessage, e.getLocalizedMessage());
            activityService.addActivity(Type.GIVENUP, givenUpMessage, e);
            throw new StepRunException(givenUpMessage, e);
        }
        for (Member member : getData().getMembers()) {
            for (Axis axis : member.getAxes()) {
                if (requireFilter(axis, filterMap)) {
                    forRemovalMembers.add(member);
                    break;
                }
            }
        }
        for (Member member : forRemovalMembers) {
            getData().getMembers().remove(member);
        }
        setConvertedData(getData());
        dataDefService.traceDataStructure(getData().getDataDef(), getData());
        setConsistent(true);
        setStepState(StepState.PROCESS);
        return true;
    }

    /**
     * <p>
     * Tells whether axis a candidate for filter. Axis can be filter based on
     * match field or value field - see
     * {@link DataFilter#requireFilter(Axis, List, String)}. Return true if
     * require filter based on Axis match field else checks require filter based
     * on Axis value field otherwise returns false.
     * @param axis
     *            to check
     * @param filterMap
     *            map of filter fields
     * @return true if axis candidate for filter otherwise false
     */
    private boolean requireFilter(final Axis axis,
            final Map<AxisName, Fields> filterMap) {
        Fields filters = filterMap.get(axis.getName());
        if (filters == null) {
            return false;
        }
        if (requireFilter(axis, filters, "match")) {
            return true;
        }
        if (requireFilter(axis, filters, "value")) {
            return true;
        }
        return false;
    }

    /**
     * <p>
     * Checks axis against filter fields and group and return true if axis
     * candidate for filter.
     * @param axis
     *            to filter
     * @param fields
     *            filter fields from datadef
     * @param filterGroup
     *            if "match" then axis.getMatch() is compared with field value
     *            if "value" then axis.getValue() is compared with field value
     * @return true if axis candidate for filter.
     */
    private boolean requireFilter(final Axis axis, final Fields fields,
            final String filterType) {
        String value = "";
        if (filterType.equals("match")) {
            value = axis.getMatch();
        }
        if (filterType.equals("value")) {
            value = axis.getValue();
        }
        if (value == null) {
            return false;
        }
        try {
            String xpath = Util.join("/xf:filters[@type='", filterType,
                    "']/xf:filter/@pattern");
            // include blanks also in patterns
            List<String> patterns = fieldsHelper.getValues(xpath, true, fields);
            for (String pattern : patterns) {
                if (value.equals(pattern)) {
                    return true;
                }
                try {
                    if (Pattern.matches(pattern, value)) {
                        return true;
                    }
                } catch (PatternSyntaxException e) {
                    LOGGER.warn("unable to filter {} {}", pattern, e);
                }
            }
        } catch (FieldsNotFoundException e) {
        }
        return false;
    }
}
