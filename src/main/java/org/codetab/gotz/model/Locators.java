//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.11
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2017.03.12 at 11:35:17 AM IST
//

package org.codetab.gotz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
 * Java class for locators complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this
 * class.
 *
 * <pre>
 * &lt;complexType name="locators"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="locator" type="{http://codetab.org/gotz}locator" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://codetab.org/gotz}locators" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="group" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "locators", propOrder = {"locator", "locators"})
public class Locators implements Serializable {

    private final static long serialVersionUID = 1L;
    @XmlElement
    private List<Locator> locator;
    @XmlElement
    private List<Locators> locators;
    @XmlAttribute(name = "group")
    private String group;

    /**
     * Gets the value of the locator property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot.
     * Therefore any modification you make to the returned list will be present inside the
     * JAXB object. This is why there is not a <CODE>set</CODE> method for the locator
     * property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getLocator().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Locator }
     *
     *
     */
    public List<Locator> getLocator() {
        if (locator == null) {
            locator = new ArrayList<Locator>();
        }
        return this.locator;
    }

    /**
     * Gets the value of the locators property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot.
     * Therefore any modification you make to the returned list will be present inside the
     * JAXB object. This is why there is not a <CODE>set</CODE> method for the locators
     * property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getLocators().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Locators }
     *
     *
     */
    public List<Locators> getLocators() {
        if (locators == null) {
            locators = new ArrayList<Locators>();
        }
        return this.locators;
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
    public boolean equals(final Object obj) {
        String[] excludes = {"id", "dnDetachedState", "dnFlags", "dnStateManager"};
        return EqualsBuilder.reflectionEquals(this, obj, excludes);
    }

    @Override
    public int hashCode() {
        String[] excludes = {"id", "dnDetachedState", "dnFlags", "dnStateManager"};
        return HashCodeBuilder.reflectionHashCode(this, excludes);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}