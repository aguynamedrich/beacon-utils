package us.beacondigital.utils.net;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import us.beacondigital.utils.StringUtils;

public class HttpClientProvider {
	
	private static String userAgent = null;
	public static void setUserAgent(String input) {
		userAgent = input;
	}

    // Default connection and socket timeout of 60 seconds. Tweak to taste.
    private static final int SOCKET_OPERATION_TIMEOUT = 60 * 1000;
    public static DefaultHttpClient get()
    {
    	HttpParams params = new BasicHttpParams();
    	
    	HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    	HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
    	HttpProtocolParams.setUseExpectContinue(params, true);
    	
    	HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, SOCKET_OPERATION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_OPERATION_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

        DefaultHttpClient client = new DefaultHttpClient(conMgr, params);
        client.getParams().setParameter(HTTP.USER_AGENT, getUserAgent());
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, getUserAgent());

        return client;
    }
    
    /**
     * Provide a default value for user agent string if not set
     * @return
     */
	public static String getUserAgent() {
		if (StringUtils.isNullOrEmpty(userAgent)) {
			setUserAgent(System.getProperty("http.agent"));
		}
		return userAgent;
	}
    
    
}