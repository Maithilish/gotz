
package org.codetab.gotz.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * <p>
 * Java class for dFilter complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="dFilter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://codetab.org/xfields}fields" minOccurs="0"/&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="axis" use="required" type="{http://codetab.org/gotz}nonEmptyString" /&gt;
 *       &lt;attribute name="name" type="{http://codetab.org/gotz}nonEmptyString" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "dFilter", propOrder = {"fields", "id"})
public class DFilter implements Serializable {

    private final static long serialVersionUID = 1L;
    @XmlElement(namespace = "http://codetab.org/xfields")
    private Fields fields;
    @XmlElement
    private Long id;
    @XmlAttribute(name = "axis", required = true)
    private String axis;
    @XmlAttribute(name = "name")
    private String name;

    /**
     * Gets the value of the fields property.
     *
     * @return possible object is {@link Fields }
     *
     */
    public Fields getFields() {
        return fields;
    }

    /**
     * Sets the value of the fields property.
     *
     * @param value
     *            allowed object is {@link Fields }
     *
     */
    public void setFields(Fields value) {
        this.fields = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link Long }
     *
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *            allowed object is {@link Long }
     *
     */
    public void setId(Long value) {
        this.id = value;
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
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    @Override
    public boolean equals(final Object obj) {
        String[] excludes =
                {"id", "dnDetachedState", "dnFlags", "dnStateManager"};
        return EqualsBuilder.reflectionEquals(this, obj, excludes);
    }

    @Override
    public int hashCode() {
        String[] excludes =
                {"id", "dnDetachedState", "dnFlags", "dnStateManager"};
        return HashCodeBuilder.reflectionHashCode(this, excludes);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }

}
