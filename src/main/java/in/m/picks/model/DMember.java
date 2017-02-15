//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.15 at 12:30:34 PM IST 
//

package in.m.picks.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * <p>
 * Java class for dMember complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="dMember">
 *   &lt;complexContent>
 *     &lt;extension base="{http://codetab.org/picks}base">
 *       &lt;sequence>
 *         &lt;group ref="{http://codetab.org/picks}fields"/>
 *       &lt;/sequence>
 *       &lt;attribute name="axis" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="match" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="order" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "dMember", propOrder = { "fields" })
public class DMember extends Base implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElements({ @XmlElement(name = "field", type = Field.class),
			@XmlElement(type = Fields.class) })
	protected List<FieldsBase> fields;
	@XmlAttribute(name = "axis")
	protected String axis;
	@XmlAttribute(name = "index")
	protected Integer index;
	@XmlAttribute(name = "match")
	protected String match;
	@XmlAttribute(name = "order")
	protected Integer order;

	/**
	 * Gets the value of the fields property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the fields property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getFields().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Field }
	 * {@link Fields }
	 * 
	 * 
	 */
	public List<FieldsBase> getFields() {
		if (fields == null) {
			fields = new ArrayList<FieldsBase>();
		}
		return this.fields;
	}

	/**
	 * Gets the value of the axis property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAxis() {
		return axis;
	}

	/**
	 * Sets the value of the axis property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAxis(String value) {
		this.axis = value;
	}

	/**
	 * Gets the value of the index property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * Sets the value of the index property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setIndex(Integer value) {
		this.index = value;
	}

	/**
	 * Gets the value of the match property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMatch() {
		return match;
	}

	/**
	 * Sets the value of the match property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMatch(String value) {
		this.match = value;
	}

	/**
	 * Gets the value of the order property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * Sets the value of the order property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setOrder(Integer value) {
		this.order = value;
	}

	@Override
	public boolean equals(Object obj) {
		String[] excludes = { "id", "fields", "dnDetachedState", "dnFlags",
				"dnStateManager" };
		return EqualsBuilder.reflectionEquals(this, obj, excludes);
	}

	@Override
	public int hashCode() {
		String[] excludes = { "id", "fields", "dnDetachedState", "dnFlags",
				"dnStateManager" };
		return HashCodeBuilder.reflectionHashCode(this, excludes);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

}
