package de.hsbremen.tc.tnc.map.client;

import com.upokecenter.cbor.CBORObject;
import de.hsbremen.tc.tnc.map.cbor.objects.CborResponseEventDecoder;
import de.hsbremen.tc.tnc.map.event.response.ResponseEvent;
import de.hsbremen.tc.tnc.map.event.response.ResponseNewAssociationEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

	private Socket socket;
	private String associationId;
	private String publisherId;
	private final CborResponseEventDecoder responseDecoder;

	public Client(CborResponseEventDecoder decoder) {
		this.responseDecoder = decoder;
		this.publisherId = "";
		this.associationId = "";
	}

	public void connect(InetAddress host, int port) throws IOException {

		socket = new Socket();
		socket.connect(new InetSocketAddress(host, port));
	}

	/**
	 * @return the associationId
	 */
	public String getAssociationId() {

		return this.associationId;
	}

	/**
	 * @return the publisherId
	 */
	public String getPublisherId() {
		return this.publisherId;
	}

	/**
	 * @param associationId the associationId to set
	 */
	public void setAssociationId(String associationId) {
		System.out.println("New Association ID: " + associationId);
		this.associationId = associationId;
	}

	/**
	 * @param publisherId the publisherId to set
	 */
	public void setPublisherId(String publisherId) {
		System.out.println("New Publisher ID:" + publisherId);
		this.publisherId = publisherId;
	}

	public synchronized void send(CBORObject object) {
		try {

			byte[] b = object.EncodeToBytes();
			System.out.println("Send CBORObject: " + object.ToJSONString());

			socket.getOutputStream().write(b);
			socket.getOutputStream().flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void receive() {

		try {

			CBORObject o = null;
			o = CBORObject.Read(socket.getInputStream());

			ResponseEvent r = this.responseDecoder.decode(o.EncodeToBytes(), this.publisherId);


			if (r instanceof ResponseNewAssociationEvent) {

				String pub = r.getPublisherId();
				String association = ((ResponseNewAssociationEvent) r).getAssociationId();
				this.setAssociationId(association);
				this.setPublisherId(pub);

			}

			System.out.println(r.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			socket.close();
			socket.getOutputStream().close();
		} catch (IOException e) {
			// ignore
		}

	}
}