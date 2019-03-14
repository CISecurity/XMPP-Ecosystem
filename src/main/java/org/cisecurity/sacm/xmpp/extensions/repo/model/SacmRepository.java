package org.cisecurity.sacm.xmpp.extensions.repo.model;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlTransient
@XmlSeeAlso({SacmRepository.SacmRepositoryContentTypeType.class, SacmRepository.SacmRepositoryContentType.class, SacmRepository.SacmRepositoryContentRequestType.class})
public abstract class SacmRepository {

	public static final String NAMESPACE = "http://cisecurity.org/sacm/repository";

	protected List<SacmRepositoryItemType> item;

	@XmlAttribute(name = "requestedType")
	protected SacmRepositoryContentTypeCodeType requestedType;

	@XmlAttribute(name = "requestedItem")
	protected String requestedItem;

	/**
	 * Gets the value of the item property.
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





	@XmlRootElement(name = "content_type")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class SacmRepositoryContentTypeType extends SacmRepository {

		protected List<SacmRepositoryContentTypeValueType> value;

		/**
		 * Gets the value of the value property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list,
		 * not a snapshot. Therefore any modification you make to the
		 * returned list will be present inside the JAXB object.
		 * This is why there is not a <CODE>set</CODE> method for the value property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 * <pre>
		 *    getValue().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link SacmRepositoryContentTypeValueType }
		 *
		 *
		 */
		public List<SacmRepositoryContentTypeValueType> getValue() {
			if (value == null) {
				value = new ArrayList<SacmRepositoryContentTypeValueType>();
			}
			return this.value;
		}

	}

	@XmlRootElement(name = "content")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class SacmRepositoryContentType extends SacmRepository {}

	@XmlRootElement(name = "content_request")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class SacmRepositoryContentRequestType extends SacmRepository {

		@XmlAttribute(name = "toJid")
		protected String toJid;
		@XmlAttribute(name = "success")
		protected Boolean success;

		/**
		 * Gets the value of the toJid property.
		 *
		 * @return
		 *     possible object is
		 *     {@link String }
		 *
		 */
		public String getToJid() {
			return toJid;
		}

		/**
		 * Sets the value of the toJid property.
		 *
		 * @param value
		 *     allowed object is
		 *     {@link String }
		 *
		 */
		public void setToJid(String value) {
			this.toJid = value;
		}

		/**
		 * Gets the value of the success property.
		 *
		 * @return
		 *     possible object is
		 *     {@link Boolean }
		 *
		 */
		public Boolean isSuccess() {
			return success;
		}

		/**
		 * Sets the value of the success property.
		 *
		 * @param value
		 *     allowed object is
		 *     {@link Boolean }
		 *
		 */
		public void setSuccess(Boolean value) {
			this.success = value;
		}

	}
}
