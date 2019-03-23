package org.cisecurity.sacm.xmpp.client.endpoint

import com.upokecenter.cbor.CBORObject
import de.hsbremen.tc.tnc.map.cbor.objects.CborOpsDataHelper
import de.hsbremen.tc.tnc.map.data.metadata.MetadataCardinalityEnum
import de.hsbremen.tc.tnc.map.data.metadata.MetadataLifetimeEnum

class ExampleMapData {
	static final class ExampleIds {

		public static final int NAME = 1;

		public static final int MIN_LENGTH = 1;
		public static final int LABEL_TEST = 65535;
		public static final int PEN_TEST = 65535;
		public static final int VERSION = 0;

		public static final int ENDPOINT_IDENTIFIER = 100;
		public static final int POLICY_IDENTIFIER = 200;

		public static final int EXPECTED_METADATA = 100;
		public static final int ACTUAL_METADATA = 201;

		static final int JID = 50
		static final int POLICY = 51
		static final int POLICY_TYPE = 52
	}

	public static final CBORObject getEndpointIdentifier(String jid) {

		long [] qn = [ExampleIds.PEN_TEST, ExampleIds.LABEL_TEST, ExampleIds.ENDPOINT_IDENTIFIER, ExampleIds.VERSION]
		String ad = "";

		CBORObject data = CBORObject.NewMap();
		data.Add(CBORObject.FromObject(ExampleIds.JID),
			CBORObject.FromObject(jid));

		CBORObject object = CborOpsDataHelper.createIdentifier(qn, ad, data);

		return object;
	}

	public static final CBORObject getPolicyIdentifier(String policyType, def policy = [:]) {

		long [] qn = [ExampleIds.PEN_TEST, ExampleIds.LABEL_TEST, ExampleIds.POLICY_IDENTIFIER, ExampleIds.VERSION]
		String ad = "";

		CBORObject data = CBORObject.NewMap();
		CBORObject policyMap = CBORObject.NewMap()
		policy.each { k, v ->
			policyMap.Add(CBORObject.FromObject(k), CBORObject.FromObject(v))
		}
		data.Add(CBORObject.FromObject(ExampleIds.POLICY),
			CBORObject.FromObject(policyMap));

		data.Add(CBORObject.FromObject(ExampleIds.POLICY_TYPE),
			CBORObject.FromObject(policyType));

		CBORObject object = CborOpsDataHelper.createIdentifier(qn, ad, data);

		return object;
	}

	public static final CBORObject getExpectedPolicyMetadata(
		MetadataLifetimeEnum lifetime, MetadataCardinalityEnum cardinality) {

		long [] qn = [ExampleIds.PEN_TEST, ExampleIds.LABEL_TEST, ExampleIds.EXPECTED_METADATA, ExampleIds.VERSION]
		CBORObject object = CborOpsDataHelper.createMetadata(qn, lifetime, cardinality);

		return object;
	}

	public static final CBORObject getActualPolicyMetadata(
		MetadataLifetimeEnum lifetime, MetadataCardinalityEnum cardinality, def policy = [:]) {

		long [] qn = [ExampleIds.PEN_TEST, ExampleIds.LABEL_TEST, ExampleIds.ACTUAL_METADATA, ExampleIds.VERSION]

		CBORObject data = CBORObject.NewMap();
		CBORObject policyMap = CBORObject.NewMap()
		policy.each { k, v ->
			policyMap.Add(CBORObject.FromObject(k), CBORObject.FromObject(v))
		}
		data.Add(CBORObject.FromObject(ExampleIds.POLICY),
			CBORObject.FromObject(policyMap));

		CBORObject object = CborOpsDataHelper.createMetadata(qn, lifetime, cardinality, data);

		return object;
	}
}
