package org.oliverweber.tools.sps.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.oliverweber.tools.sps.domain.PortStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author oweber - orlosch74@gmail.com
 *
 */
public class NetgearGS116Ev2SwitchConnector
{
  private static Logger       LOG        = LoggerFactory.getLogger( NetgearGS116Ev2SwitchConnector.class );

  private String              deviceHostname;
  private int                 devicePort = 80;
  private String              password;
  private String              proxyHostname;
  private int                 proxyPort  = 8888;

  private CloseableHttpClient httpclient;

  /**
   * Create a new {@link NetgearGS116Ev2SwitchConnector}.
   * 
   * @param deviceHostname
   * @param devicePort
   * @param password
   */
  public NetgearGS116Ev2SwitchConnector( String deviceHostname, int devicePort, String password )
  {
    super();
    this.deviceHostname = deviceHostname;
    this.devicePort = devicePort;
    this.password = password;

    RequestConfig requestConfig = RequestConfig.custom()
                                               .build();
    this.initHttpClient( requestConfig );
  }

  /**
   * Create a new {@link NetgearGS116Ev2SwitchConnector} and add a proxy configuration to the http client.
   * 
   * @param deviceHostname
   * @param devicePort
   * @param password
   * @param proxyHostname
   * @param proxyPort
   */
  public NetgearGS116Ev2SwitchConnector( String deviceHostname, int devicePort, String password, String proxyHostname, int proxyPort )
  {
    super();
    this.deviceHostname = deviceHostname;
    this.devicePort = devicePort;
    this.password = password;
    this.proxyHostname = proxyHostname;
    this.proxyPort = proxyPort;

    HttpHost proxy = new HttpHost( this.proxyHostname, this.proxyPort );
    RequestConfig requestConfig = RequestConfig.custom()
                                               .setProxy( proxy )
                                               .build();

    this.initHttpClient( requestConfig );
  }

  private void initHttpClient( RequestConfig requestConfig )
  {
    CookieStore httpCookieStore = new BasicCookieStore();
    HttpClientBuilder builder = HttpClientBuilder.create()
                                                 .setDefaultCookieStore( httpCookieStore )
                                                 .setDefaultRequestConfig( requestConfig );

    this.httpclient = builder.build();
  }

  public Map< Integer, PortStatistics > getPortStatistics()
  {
    //
    Map< Integer, PortStatistics > result = new HashMap<>();

    //
    this.executeHttpPostLogin();

    //
    String rawStatistics = this.executeHttpGetPortStatistics();

    int firstMarker = StringUtils.indexOf( rawStatistics, "var portList =" );
    String substring1 = StringUtils.substring( rawStatistics, firstMarker );
    String[] split1 = StringUtils.split( substring1, ";" );
    List< String > lines = new ArrayList<>();
    for( int i = 0; i < split1.length; i++ )
    {
      String rawLine = split1[i];
      String line = rawLine.trim();

      if( StringUtils.startsWith( line, "StatisticsEntry[" ) )
      {
        lines.add( line );
      }
    }

    for( String line : lines )
    {
      String[] split2 = StringUtils.split( line, "=" );
      String remove1 = StringUtils.remove( split2[1], "'" );
      String remove2 = StringUtils.remove( remove1, ";" );
      String[] split3 = StringUtils.split( remove2, "?" );
      if( split3.length == 4 )
      {
        PortStatistics portStatistics = new PortStatistics( Integer.valueOf( split3[0].trim() ), Long.valueOf( split3[1].trim() ),
                                                            Long.valueOf( split3[2].trim() ), Long.valueOf( split3[3].trim() ) );

        result.put( portStatistics.getPortNumber(), portStatistics );
      }
    }

    //
    return result;
  }

  private void executeHttpPostLogin()
  {
    HttpPost httpPost = new HttpPost( "http://" + this.deviceHostname + ":" + this.devicePort + "/login.htm" );

    List< NameValuePair > nvps = new ArrayList<>();
    nvps.add( new BasicNameValuePair( "submitId", "pwdLogin" ) );
    nvps.add( new BasicNameValuePair( "password", this.password ) );
    nvps.add( new BasicNameValuePair( "submitEnd", "" ) );

    CloseableHttpResponse response = null;

    try
    {
      httpPost.setEntity( new UrlEncodedFormEntity( nvps ) );

      response = httpclient.execute( httpPost );
      HttpEntity entity = response.getEntity();

      EntityUtils.consume( entity );
    }
    catch( UnsupportedEncodingException e )
    {
      LOG.error( "UnsupportedEncodingException", e );
    }
    catch( ClientProtocolException e )
    {
      LOG.error( "ClientProtocolException", e );
    }
    catch( IOException e )
    {
      LOG.error( "IOException", e );
    }
    finally
    {
      if( response != null )
      {
        try
        {
          response.close();
        }
        catch( IOException e )
        {
          LOG.error( "IOException", e );
        }
      }
    }
  }

  private String executeHttpGetPortStatistics()
  {
    String result = "";

    HttpGet httpGet = new HttpGet( "http://" + this.deviceHostname + ":" + this.devicePort + "/config/monitoring_port_statistics.htm" );

    try
    {
      CloseableHttpResponse response = this.httpclient.execute( httpGet );
      HttpEntity entity = response.getEntity();
      result = EntityUtils.toString( entity );
    }
    catch( IOException e )
    {
      LOG.error( "IOException", e );
    }

    return result;
  }

  public String getDeviceHostname()
  {
    return deviceHostname;
  }

  public int getDevicePort()
  {
    return devicePort;
  }

  public String getPassword()
  {
    return password;
  }

  public String getProxyHostname()
  {
    return proxyHostname;
  }

  public int getProxyPort()
  {
    return proxyPort;
  }

  public CloseableHttpClient getHttpclient()
  {
    return httpclient;
  }

}
