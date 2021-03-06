
package rummikub.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "GameDoesNotExists", targetNamespace = "http://rummikub.ws/")
public class GameDoesNotExists_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private GameDoesNotExists faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public GameDoesNotExists_Exception(String message, GameDoesNotExists faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public GameDoesNotExists_Exception(String message, GameDoesNotExists faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: rummikub.ws.GameDoesNotExists
     */
    public GameDoesNotExists getFaultInfo() {
        return faultInfo;
    }

}
