package epfl.sweng.comm;


/**
 * A proxy with a connectivity state that can be notified.
 * 
 * @author Melody Lucid
 * 
 */
public interface ConnectivityProxy {

	/**
	 * Notifies the proxy of a change of the state of connectivity.
	 * 
	 * @param  newState the new connectivity state
	 * @return A code (generally HTTP) that indicates a success or a failure in
	 *         the current change.
	 */
	int notifyConnectivityChange(ConnectivityState newState);
}
