//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.24 at 06:17:58 AM EDT 
//


package org.cisecurity.sacm.xmpp.extensions.policy.model;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.cisecurity.sacm.xmpp.extensions.policy.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.cisecurity.sacm.xmpp.extensions.policy.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Actual }
     * 
     */
    public Actual createActual() {
        return new Actual();
    }

    /**
     * Create an instance of {@link PolicyCollection }
     * 
     */
    public PolicyCollection createPolicyCollection() {
        return new PolicyCollection();
    }

    /**
     * Create an instance of {@link Publisher }
     * 
     */
    public Publisher createPublisher() {
        return new Publisher();
    }

    /**
     * Create an instance of {@link Policy }
     * 
     */
    public Policy createPolicy() {
        return new Policy();
    }

    /**
     * Create an instance of {@link Expected }
     * 
     */
    public Expected createExpected() {
        return new Expected();
    }

}