package org.oliverweber.tools.sps.service;

/**
 * 
 * @author oweber
 *
 */
public interface SwitchPortService
{
  /**
   * Get the total bytes received on this port of the switch
   * 
   * @param port
   * @return
   */
  Long getTotalBytesReceived( int port );

  /**
   * Get the total bytes sent on this port of the switch
   * 
   * @param port
   * @return
   */
  Long getTotalBytesSend( int port );

  /**
   * Get the total number of to CRC errors on this port
   * 
   * @param port
   * @return
   */
  Long getTotalCrcErrors( int port );
}
