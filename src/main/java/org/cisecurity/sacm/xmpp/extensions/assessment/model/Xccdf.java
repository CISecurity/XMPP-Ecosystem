//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.19 at 05:48:29 PM EDT 
//


package org.cisecurity.sacm.xmpp.extensions.assessment.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="content_path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="benchmark" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="profile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "contentPath",
    "benchmark",
    "profile"
})
@XmlRootElement(name = "xccdf")
public class Xccdf {

    @XmlElement(name = "content_path", required = true)
    protected String contentPath;
    @XmlElement(required = true)
    protected String benchmark;
    protected String profile;

    /**
     * Gets the value of the contentPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentPath() {
        return contentPath;
    }

    /**
     * Sets the value of the contentPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentPath(String value) {
        this.contentPath = value;
    }

    /**
     * Gets the value of the benchmark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBenchmark() {
        return benchmark;
    }

    /**
     * Sets the value of the benchmark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBenchmark(String value) {
        this.benchmark = value;
    }

    /**
     * Gets the value of the profile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfile(String value) {
        this.profile = value;
    }

}
