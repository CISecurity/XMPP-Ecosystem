package org.cisecurity.sacm.xmpp.trust

import javax.net.ssl.X509TrustManager
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

class TrustAllX509TrustManager implements X509TrustManager {

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.
	 * X509Certificate[], java.lang.String)
	 */
	@Override
	public void checkClientTrusted(final X509Certificate[] aChain,
	                               final String aAuthType) throws CertificateException {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.
	 * X509Certificate[], java.lang.String)
	 */
	@Override
	public void checkServerTrusted(final X509Certificate[] aChain,
	                               final String aAuthType) throws CertificateException {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
	 */
	@Override
	public X509Certificate[] getAcceptedIssuers() {

		return new X509Certificate[0];
	}
}