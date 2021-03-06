//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.14 at 09:59:14 AM EDT 
//


package org.cisecurity.sacm.xmpp.extensions.repo.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * TBD
 * 
 * <p>Java class for SacmRepositoryContentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SacmRepositoryContentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="item" type="{http://cisecurity.org/sacm/repository}SacmRepositoryItemType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="requestedType" type="{http://cisecurity.org/sacm/repository}SacmRepositoryContentTypeCodeType" />
 *       &lt;attribute name="requestedItem" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SacmRepositoryContentType", propOrder = {
    "item"
})
public class SacmRepositoryContentType {

    protected List<SacmRepositoryItemType> item;
    @XmlAttribute(name = "requestedType")
    protected SacmRepositoryContentTypeCodeType requestedType;
    @XmlAttribute(name = "requestedItem")
    protected String requestedItem;

    /**
     * Gets the value of the item property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the item property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SacmRepositoryItemType }
     * 
     * 
     */
    public List<SacmRepositoryItemType> getItem() {
        if (item == null) {
            item = new ArrayList<SacmRepositoryItemType>();
        }
        return this.item;
    }

    /**
     * Gets the value of the requestedType property.
     * 
     * @return
     *     possible object is
     *     {@link SacmRepositoryContentTypeCodeType }
     *     
     */
    public SacmRepositoryContentTypeCodeType getRequestedType() {
        return requestedType;
    }

    /**
     * Sets the value of the requestedType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SacmRepositoryContentTypeCodeType }
     *     
     */
    public void setRequestedType(SacmRepositoryContentTypeCodeType value) {
        this.requestedType = value;
    }

    /**
     * Gets the value of the requestedItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestedItem() {
        return requestedItem;
    }

    /**
     * Sets the value of the requestedItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestedItem(String value) {
        this.requestedItem = value;
    }

}
