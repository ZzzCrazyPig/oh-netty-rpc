package com.crazypig.rpc.netty.registry;

/**
 * @author CrazyPig
 *
 */
public class ServerAddress {
    
    private String host;
    private int port;
    
    public ServerAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    @Override
    public int hashCode() {
        return (this.host + ":" + this.port).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return this == null;
        if (obj instanceof ServerAddress) {
            ServerAddress other = (ServerAddress) obj;
            if (other.getHost() == null)
                return false;
            return (other.getHost().equals(this.host) && other.getPort() == this.port);
        }
        return false;
    }

    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    
}
