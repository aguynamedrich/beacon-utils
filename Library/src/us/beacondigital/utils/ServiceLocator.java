package us.beacondigital.utils;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;

/**
 * 
 * Utility class for lazy loading and maintaining single instance objects for 
 * easy reuse and object decoupling
 * @author Rich Stern
 *
 */
public class ServiceLocator {
	
	private static Application appContext;	
	private static HashMap<String, Object> map = new HashMap<String, Object>();
	private static HashMap<String, Object> interfaceTypes = new HashMap<String, Object>();
	
	/**
	 * Before the ServiceLocator can be used, it must be initialized with an Application context 
	 * @param context
	 */
	public static void init(Context context)
	{
		if(context instanceof Application)
			appContext = (Application) context;
		else
			appContext = (Application) context.getApplicationContext();
	}
	
	/**
	 * Simple wrapper around Context.getSystemService so that nobody but ServiceLocator needs a reference to the app context
	 * @param name
	 * @return
	 */
	public static Object getSystemService(String name)
	{
		return appContext.getSystemService(name);
	}
	
	/**
	 * Returns the single registered instance of T, lazy loaded when first requested
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> T resolve(Class<T> type)
	{
		T service = null;
		String name = type.getName();
		try
		{
			if(!map.containsKey(name))
			{
				// Resolve if we are a registered interface
				if(type.isInterface()) {
					if(interfaceTypes.containsKey(name)) {
						service = ((Class<T>) interfaceTypes.get(name)).newInstance();
						map.put(name, service);
					}
				}
				// Else resolve from default constructor of type
				else {
					service = type.newInstance();
					map.put(name, service);
				}
			}
		}
		catch(Exception ex)
		{
			// Log exceptions locally or remotely via service such as Airbrake...
		}
		return (T) map.get(name);
	}

	/**
	 * If somebody really really needs it.  Access to the app context this way should be factored out at some point
	 * @return
	 */
	public static Application getAppContext() { return appContext; }
	
	/**
	 * This is the generic version that is recommended so that casting doesn't need to be done
	 * repeatedly within the app.  There is no error checking around the cast as there should be no 
	 * reason to call this with the improper type.  This is the responsibility of the consumer.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getApp(Class<T> type) {
		return (T) getAppContext();
	}

	/**
	 * Manually register an already instantiated instance.
	 * This is used in the case where the Application context wants to pre-instantiate
	 * and perform setup on an object vs an object that can be lazy loaded using a default constructor.
	 * Example usage of this is in the case of objects that need to bind a service connection at the time of construction
	 * @param <T>
	 * @param type
	 * @param instance
	 */
	public static <T> void registerInstance(Class<T> type, Object instance) {
		String name = type.getName();
		if(map.containsKey(name))
			map.remove(name);
		map.put(name, instance);
	}

	/**
	 * Register an interface type to a concrete type.  This is useful for switching between implementations on an interface
	 * @param interfaceType
	 * @param type
	 */
	public static <T1, T2> void register(Class<T1> interfaceType, Class<T2> type) {
		if(interfaceType.isInterface() && !type.isInterface()) {
			String interfaceName = interfaceType.getName();
			if(interfaceTypes.containsKey(interfaceName))
				interfaceTypes.remove(interfaceName);
			interfaceTypes.put(interfaceName, type);
		}
	}

}
