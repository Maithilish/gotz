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
 * Java class for locator complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="locator">
 *   &lt;complexContent>
 *     &lt;extension base="{http://codetab.org/picks}base">
 *       &lt;sequence>
 *         &lt;group ref="{http://codetab.org/picks}fields"/>
 *         &lt;element name="documents" type="{http://codetab.org/picks}document" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="group" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "locator", propOrder = { "fields", "documents" })
public class Locator extends Base implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElements({ @XmlElement(name = "field", type = Field.class),
			@XmlElement(type = Fields.class) })
	protected List<FieldsBase> fields;
	@XmlElement
	protected List<Document> documents;
	@XmlAttribute(name = "url")
	protected String url;
	@XmlAttribute(name = "group")
	protected String group;

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
	 * Gets the value of the documents property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the documents property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDocuments().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Document
	 * }
	 * 
	 * 
	 */
	public List<Document> getDocuments() {
		if (documents == null) {
			documents = new ArrayList<Document>();
		}
		return this.documents;
	}

	/**
	 * Gets the value of the url property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the value of the url property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUrl(String value) {
		this.url = value;
	}

	/**
	 * Gets the value of the group property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Sets the value of the group property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setGroup(String value) {
		this.group = value;
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
