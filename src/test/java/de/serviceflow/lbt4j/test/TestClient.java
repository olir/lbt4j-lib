package de.serviceflow.lbt4j.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bluez.Adapter1;
import org.bluez.Agent1;
import org.bluez.AgentManager1;
import org.bluez.Device1;
import org.bluez.GattCharacteristic1;
import org.bluez.GattService1;

import de.serviceflow.codegenj.ObjectManager;
import de.serviceflow.codegenj.ObjectManager.ObjectManagerSignalListener;

public class TestClient {
	

	@Test
	public final void testMain() throws InterruptedException {
	
		ObjectManager m = ObjectManager.getInstance();
		ObjectManager.getLogger().setLevel(Level.FINE); 		
		
		m.addObjectManagerSignalListener(createSignalListener());
		
		String AGENT_PATH = "/org/bluez/agent";
		Agent1 agent = new MyAgent(AGENT_PATH);

		System.out.println(" ==> Agent1: " + agent);
		Thread.sleep(2000);
		System.out.println(" ==> Agent1: " + agent + " name aquired deathline -----");

		System.out.println("*** DUMP ***");
		m.dump();
		System.out.println("*** DUMP ***");
		
		
		List<Adapter1> adapters = m.getAdapters();
		System.out.println(" ==> # = " + adapters.size());

		Adapter1 defaultAdapter = null;
		for (Adapter1 a : adapters) {
			System.out.println(" ==> Adapter: " + a.getName());
			try {
				a.startDiscovery();
			} catch (IOException e) {
				System.out.println(" ... ignored.");
			}
			defaultAdapter = a;
		}

		for (int i = 0; i < 1; i++) {
			System.out.println("*** pause 5s ");
			Thread.sleep(5000);
		}
		
		for (Adapter1 a : adapters) {
			for (Device1 d : a.getDevices()) {

				System.out.println(" --> Device " + d.getName());
			}
		}
		
		System.out.println("*** pause 5s");
		Thread.sleep(5000);

		System.out.println("*** Object Tree:");
		for (Adapter1 a : adapters) {
			System.out.println(" ... adapter "+a.getObjectPath()+"  "+a.getName());
			for (Device1 d : a.getDevices()) {
				System.out.println("  .. device "+d.getObjectPath()+"  "+d.getName());
				for (GattService1 s :  d.getServices()) {
					System.out.println("   . service "+s.getObjectPath()+"  "+s.getUUID());
					for (GattCharacteristic1 c :  s.getCharacteristics()) {
						System.out.println("    . char "+c.getObjectPath()+"  "+c.getUUID());
					}
				}
			}
		}
		
		System.out.println("*** pause 5s");
		Thread.sleep(5000);

		
		
		for (Adapter1 a : adapters) {
			System.out.println(" ==> Adapter: " + a);
			try {
				a.stopDiscovery();
				System.out.println(" ... stopped.");
			} catch (IOException e) {
				System.out.println(" ... ignored.");
			}
		}

		System.out.println("*** TestClient done");

		
	}

	/**
	 * @return
	 */
	private static ObjectManagerSignalListener createSignalListener() {
		return new ObjectManagerSignalListener() {
			
			@Override
			public void objectRemoved(ObjectManager m, String objectpath) {
		        if (ObjectManager.getLogger().isLoggable(Level.FINER))
		        	System.out.println("objectRemoved path="+objectpath);
			}
			
			@Override
			public void objectAdded(ObjectManager m, String objectpath) {
		        if (ObjectManager.getLogger().isLoggable(Level.FINER))
		        	System.out.println("objectAdded path="+objectpath);				
			}
			
			@Override
			public void interfaceRemoved(ObjectManager m, String objectpath, String interfacename) {
		        if (ObjectManager.getLogger().isLoggable(Level.FINER))
		        	System.out.println("interfaceRemoved path="+objectpath+" interface="+interfacename);
			}
			
			@Override
			public void interfaceAdded(ObjectManager m, String objectpath, String interfacename) {
		        if (ObjectManager.getLogger().isLoggable(Level.FINER))
		        	System.out.println("interfaceAdded path="+objectpath+" interface="+interfacename);
			}
		};
	}


	private static final class MyAgent extends Agent1 {
		private MyAgent(String arg0) {
			super(arg0);
		}

		/**
		 * Release:
		 *
		 *  This method gets called when the service daemon unregisters the agent. 
				An agent can use it to do cleanup tasks. There is no need to unregister the 
				agent, because when this method gets called it has already been unregistered. 
		 */
		@Override
		public void release() {
			System.out.println("Agent1 callback release()");
		}

		/**
		 * RequestPinCode:
		 *
		 *  This method gets called when the service daemon needs to get the passkey 
				for an authentication. The return value should be a string of 1-16 characters 
				length. The string can be alphanumeric. Possible errors: org.bluez.Error.Rejected 
				org.bluez.Error.Canceled 
		 */
		@Override
		public String requestPinCode(Object device) {
			System.out.println("Agent1 callback requestPinCode()");
			return "0000";
		}

		/**
		 * DisplayPinCode:
		 *
		 *  This method gets called when the service daemon needs to display a 
				pincode for an authentication. An empty reply should be returned. When the 
				pincode needs no longer to be displayed, the Cancel method of the agent will 
				be called. This is used during the pairing process of keyboards that don't 
				support Bluetooth 2.1 Secure Simple Pairing, in contrast to DisplayPasskey 
				which is used for those that do. This method will only ever be called once 
				since older keyboards do not support typing notification. Note that the PIN 
				will always be a 6-digit number, zero-padded to 6 digits. This is for harmony 
				with the later specification. Possible errors: org.bluez.Error.Rejected org.bluez.Error.Canceled 
		 */
		@Override
		public void displayPinCode(Object device, String pincode) {
			System.out.println("Agent1 callback displayPinCode() -> "+pincode);
		}

		/**
		 * RequestPasskey:
		 *
		 *  This method gets called when the service daemon needs to get the passkey 
				for an authentication. The return value should be a numeric value between 
				0-999999. Possible errors: org.bluez.Error.Rejected org.bluez.Error.Canceled 
		 */
		@Override
		public int requestPasskey(Object device) {
			System.out.println("Agent1 callback requestPasskey()");
			return 0;
		}

		/**
		 * DisplayPasskey:
		 *
		 *  This method gets called when the service daemon needs to display a 
				passkey for an authentication. The entered parameter indicates the number 
				of already typed keys on the remote side. An empty reply should be returned. 
				When the passkey needs no longer to be displayed, the Cancel method of the 
				agent will be called. During the pairing process this method might be called 
				multiple times to update the entered value. Note that the passkey will always 
				be a 6-digit number, so the display should be zero-padded at the start if 
				the value contains less than 6 digits. 
		 */
		@Override
		public void displayPasskey(Object device, int passkey, short entered) {
			System.out.println("Agent1 callback displayPasskey() -> "+passkey+" "+entered);
		}

		/**
		 * RequestConfirmation:
		 *
		 *  This method gets called when the service daemon needs to confirm a 
				passkey for an authentication. To confirm the value it should return an empty 
				reply or an error in case the passkey is invalid. Note that the passkey will 
				always be a 6-digit number, so the display should be zero-padded at the start 
				if the value contains less than 6 digits. Possible errors: org.bluez.Error.Rejected 
				org.bluez.Error.Canceled 
		 */
		@Override
		public void requestConfirmation(Object device, int passkey) {
			System.out.println("Agent1 callback requestConfirmation() -> "+passkey);
		}

		/**
		 * RequestAuthorization:
		 *
		 *  This method gets called to request the user to authorize an incoming 
				pairing attempt which would in other circumstances trigger the just-works 
				model. Possible errors: org.bluez.Error.Rejected org.bluez.Error.Canceled 
		 */
		@Override
		public void requestAuthorization(Object device) {
			System.out.println("Agent1 callback requestAuthorization()");
		}

		/**
		 * AuthorizeService:
		 *
		 *  This method gets called when the service daemon needs to authorize 
				a connection/service request. Possible errors: org.bluez.Error.Rejected org.bluez.Error.Canceled 
		 */
		@Override
		public void authorizeService(Object device, String uuid) {
			System.out.println("Agent1 callback authorizeService() --> "+uuid);
		}

		/**
		 * Cancel:
		 *
		 *  This method gets called to indicate that the agent request failed 
				before a reply was returned. 
		 */
		@Override
		public void cancel() {
			System.out.println("Agent1 callback cancel()");
		}
	}
	
}
