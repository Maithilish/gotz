package in.m.picks.ext;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.m.picks.appender.Appender;
import in.m.picks.exception.FieldNotFoundException;
import in.m.picks.model.AxisName;
import in.m.picks.model.ColComparator;
import in.m.picks.model.FieldsBase;
import in.m.picks.model.Member;
import in.m.picks.model.RowComparator;
import in.m.picks.shared.AppenderService;
import in.m.picks.step.IStep;
import in.m.picks.step.Transformer;
import in.m.picks.util.FieldsUtil;

public class CsvTransformer extends Transformer {

	final Logger log = LoggerFactory.getLogger(CsvTransformer.class);

	final static int itemColSize = 30;
	final static int factColSize = 10;
	final static String lineBreak = System.getProperty("line.separator");

	StringBuilder sb;

	@Override
	public IStep instance() {
		return new CsvTransformer();
	}

	@Override
	protected void transform() {
		processStep();
	}

	public void processStep() {
		sb = new StringBuilder();

		ColComparator cc = new ColComparator();
		Collections.sort(data.getMembers(), cc);
		RowComparator rc = new RowComparator();
		Collections.sort(data.getMembers(), rc);
		String prevRow = null;

		sb.append(getHeader());
		for (Member member : data.getMembers()) {
			String row = member.getValue(AxisName.ROW);
			String fact = member.getValue(AxisName.FACT);

			if (prevRow == null) {
				sb.append(StringUtils.rightPad(row, itemColSize));
				sb.append(" |");
			} else {
				if (!prevRow.equals(row)) {
					sb.append(lineBreak);
					sb.append(StringUtils.rightPad(row, itemColSize));
					sb.append(" |");
				} else {
					sb.append(" |");
				}
			}
			sb.append(StringUtils.leftPad(fact, factColSize));
			prevRow = row;
		}
		sb.append(lineBreak);
	}

	private int getColCount() {
		Set<String> cols = new HashSet<String>();
		for (Member member : data.getMembers()) {
			cols.add(member.getValue(AxisName.COL));
		}
		return cols.size();
	}

	private String getHeader() {
		String header = StringUtils.rightPad("item", itemColSize);
		int colCount = getColCount();
		for (int c = 0; c < colCount; c++) {
			header += " |";
			String col = data.getMembers().get(c).getValue(AxisName.COL);
			header += StringUtils.leftPad(col, factColSize);
		}
		header += lineBreak;
		return header;
	}

	@Override
	public void handover() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, FieldNotFoundException {
		List<FieldsBase> appenders = FieldsUtil.getFieldList(fields, "appender");
		for (FieldsBase field : appenders) {
			AppenderService.INSTANCE.createAppender(field.getName(),
					field.getValue());
			Appender appender = AppenderService.INSTANCE
					.getAppender(field.getName());
			appender.append(sb);
		}
	}
}
