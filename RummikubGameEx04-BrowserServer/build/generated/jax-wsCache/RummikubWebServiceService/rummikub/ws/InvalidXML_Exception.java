
package rummikub.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "InvalidXML", targetNamespace = "http://rummikub.ws/")
public class InvalidXML_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private InvalidXML faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public InvalidXML_Exception(String message, InvalidXML faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public InvalidXML_Exception(String message, InvalidXML faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: rummikub.ws.InvalidXML
     */
    public InvalidXML getFaultInfo() {
        return faultInfo;
    }

}
