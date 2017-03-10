package in.m.picks.ext;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import in.m.picks.exception.FieldNotFoundException;
import in.m.picks.model.Axis;
import in.m.picks.model.AxisName;
import in.m.picks.model.DataDef;
import in.m.picks.model.FieldsBase;
import in.m.picks.model.Member;
import in.m.picks.shared.ConfigService;
import in.m.picks.step.Parser;
import in.m.picks.util.DataDefUtil;
import in.m.picks.util.FieldsUtil;
import in.m.picks.util.Util;

public abstract class HtmlParser extends Parser {

    static final Logger LOGGER = LoggerFactory.getLogger(HtmlParser.class);

    private Map<Integer, List<?>> nodeMap;
    private ScriptEngine jsEngine;

    public HtmlParser() {
        nodeMap = new HashMap<Integer, List<?>>();
    }

    /*
     * (non-Javadoc)
     *
     * @see in.m.picks.step.Parser#setValue(in.m.picks.model.DataDef,
     * in.m.picks.model.Member)
     */
    @Override
    protected void setValue(final DataDef dataDef, final Member member)
            throws ScriptException, NumberFormatException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        for (AxisName axisName : AxisName.values()) {
            Axis axis = member.getAxis(axisName);
            if (axis == null) {
                continue;
            }
            if (axis.getIndex() == null) {
                Integer startIndex;
                try {
                    startIndex = getStartIndex(axis.getFields());
                } catch (FieldNotFoundException e) {
                    startIndex = 1;
                }
                axis.setIndex(startIndex);
            }
            if (isDocumentLoaded() && axis.getValue() == null) {
                HtmlPage documentObject = (HtmlPage) getDocument()
                        .getDocumentObject();
                String value = getValue(documentObject, dataDef, member, axis);
                axis.setValue(value);
            }
        }
    }

    /*
     *
     */
    private String getValue(final HtmlPage page, final DataDef dataDef,
            final Member member, final Axis axis)
            throws ScriptException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        StringBuilder sb = new StringBuilder(); // to trace query strings
        String value = null;
        List<FieldsBase> list = DataDefUtil.getAxis(dataDef, axis.getName())
                .getFields();
        try {
            List<FieldsBase> scripts = FieldsUtil.getGroupFields(list,
                    "script");
            setTraceString(sb, scripts, "--- Script ---");
            scripts = FieldsUtil.replaceVariables(scripts, member.getAxisMap());
            setTraceString(sb, scripts, "-- Patched --");
            LOGGER.trace("{}", sb);
            Util.logState(LOGGER, "parser-" + getDataDefName(), "", getFields(),
                    sb);
            value = queryByScript(scripts);
        } catch (FieldNotFoundException e) {
        }

        try {
            List<FieldsBase> queries = FieldsUtil.getGroupFields(list, "query");
            setTraceString(sb, queries, "--- Query ---");
            queries = FieldsUtil.replaceVariables(queries, member.getAxisMap());
            setTraceString(sb, queries, "-- Patched --");
            LOGGER.trace("{}", sb);
            Util.logState(LOGGER, "parser-" + getDataDefName(), "", getFields(),
                    sb);
            value = queryByXPath(page, queries);
        } catch (FieldNotFoundException e) {
        }

        try {
            List<FieldsBase> prefix = FieldsUtil.getGroupFields(list, "prefix");
            value = FieldsUtil.prefixFieldValue(prefix, value);
        } catch (FieldNotFoundException e) {
        }

        return value;
    }

    private String queryByScript(final List<FieldsBase> scripts)
            throws ScriptException, FieldNotFoundException {
        // TODO - check whether thread safety is involved
        if (jsEngine == null) {
            initializeScriptEngine();
        }
        LOGGER.trace("------Query Data------");
        LOGGER.trace("Scripts {} ", scripts);
        jsEngine.put("configs", ConfigService.INSTANCE);
        String scriptStr = FieldsUtil.getValue(scripts, "script");
        Object value = jsEngine.eval(scriptStr);
        return ConvertUtils.convert(value);
    }

    private void initializeScriptEngine() {
        LOGGER.debug("{}", "Initializing script engine");
        ScriptEngineManager scriptEngineMgr = new ScriptEngineManager();
        jsEngine = scriptEngineMgr.getEngineByName("JavaScript");
        if (jsEngine == null) {
            throw new NullPointerException(
                    "No script engine found for JavaScript");
        }
    }

    private String queryByXPath(final HtmlPage page,
            final List<FieldsBase> queries) throws FieldNotFoundException {
        if (FieldsUtil.fieldCount(queries) < 2) {
            LOGGER.warn("Insufficient queries in DataDef [{}]",
                    getDataDefName());
            return null;
        }
        LOGGER.trace("------Query Data------");
        LOGGER.trace("Queries {} ", queries);
        String regionXpathExpr = FieldsUtil.getValue(queries, "region");
        String xpathExpr = FieldsUtil.getValue(queries, "field");
        String value = getByXPath(page, regionXpathExpr, xpathExpr);
        return value;
    }

    private String getByXPath(final HtmlPage page, final String regionXpathExpr,
            final String xpathExpr) {
        String value = null;
        List<?> nodes = getRegionNodes(page, regionXpathExpr);
        for (Object o : nodes) {
            DomNode node = (DomNode) o;
            value = getByXPath(node, xpathExpr);
        }
        return value;
    }

    private List<?> getRegionNodes(final HtmlPage page,
            final String xpathExpr) {
        /*
         * regional nodes are cached in HashMap for performance. Map is flushed
         * in loadObject method.
         */
        final int numOfLines = 5;
        Integer hash = xpathExpr.hashCode();
        List<?> nodes = null;
        if (nodeMap.containsKey(hash)) {
            nodes = nodeMap.get(hash);
        } else {
            nodes = page.getByXPath(xpathExpr);
            nodeMap.put(hash, nodes);
        }
        LOGGER.trace(
                "Region Nodes " + nodes.size() + " for XPATH: " + xpathExpr);
        for (Object o : nodes) {
            DomNode node = (DomNode) o;
            String nodeTraceStr = Util.stripe(node.asXml(), numOfLines,
                    "Data Region \n-------------\n", "-------------");
            LOGGER.trace("{}", nodeTraceStr);
            Util.logState(LOGGER, "parser-" + getDataDefName(), "", getFields(),
                    nodeTraceStr);
        }
        return nodes;
    }

    private String getByXPath(final DomNode node, final String xpathExpr) {
        final int numOfLines = 5;
        String value = null;
        List<?> nodes = node.getByXPath(xpathExpr);
        LOGGER.trace("Nodes " + nodes.size() + " for XPATH: " + xpathExpr);
        for (Object o : nodes) {
            DomNode childNode = (DomNode) o;
            value = childNode.getTextContent();
            String nodeTraceStr = Util.stripe(childNode.asXml(), numOfLines,
                    "Data Node \n--------\n", "--------");
            LOGGER.trace("{}", nodeTraceStr);
            Util.logState(LOGGER, "parser-" + getDataDefName(), "", getFields(),
                    nodeTraceStr);
        }
        LOGGER.trace("Text Content of the node: " + value);
        return value;
    }

    private void setTraceString(final StringBuilder sb,
            final List<FieldsBase> fields, final String header) {
        if (!LOGGER.isTraceEnabled()) {
            return;
        }
        String line = "\n";
        sb.append(line);
        sb.append(header);
        sb.append(line);
        for (FieldsBase field : fields) {
            sb.append(field);
            sb.append(line);
        }
    }

}
