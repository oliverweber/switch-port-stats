package org.oliverweber.tools.sps.domain;

/**
 * 
 * @author oweber - orlosch74@gmail.com
 *
 */
public class PortStatistics
{
  private int  portNumber;
  private Long totalBytesReceived;
  private Long totalBytesSent;
  private Long totalCrcErrorPackets;

  public PortStatistics( int portNumber, Long totalBytesReceived, Long totalBytesSent, Long totalCrcErrorPackets )
  {
    super();
    this.portNumber = portNumber;
    this.totalBytesReceived = totalBytesReceived;
    this.totalBytesSent = totalBytesSent;
    this.totalCrcErrorPackets = totalCrcErrorPackets;
  }

  public int getPortNumber()
  {
    return portNumber;
  }

  public void setPortNumber( int portNumber )
  {
    this.portNumber = portNumber;
  }

  public Long getTotalBytesReceived()
  {
    return totalBytesReceived;
  }

  public void setTotalBytesReceived( Long totalBytesReceived )
  {
    this.totalBytesReceived = totalBytesReceived;
  }

  public Long getTotalBytesSent()
  {
    return totalBytesSent;
  }

  public void setTotalBytesSent( Long totalBytesSent )
  {
    this.totalBytesSent = totalBytesSent;
  }

  public Long getTotalCrcErrorPackets()
  {
    return totalCrcErrorPackets;
  }

  public void setTotalCrcErrorPackets( Long totalCrcErrorPackets )
  {
    this.totalCrcErrorPackets = totalCrcErrorPackets;
  }

  @Override
  public String toString()
  {
    return "PortStatistics [portNumber=" + portNumber + ", totalBytesReceived=" + totalBytesReceived + ", totalBytesSent=" + totalBytesSent
           + ", totalCrcErrorPackets=" + totalCrcErrorPackets + "]";
  }

}
