package fileManager;

import ServerConfig.Server;
import java.io.File;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

public class FileLoader {
    public static Server loadServerConfigXMLFile(String filePath) throws Exception {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File("Resource\\ConfigurationOfServer.xsd"));
        
        JAXBContext context = JAXBContext.newInstance(Server.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schema);
        return (Server)unmarshaller.unmarshal(new File(filePath));
    }
    
    public static void saveServerConfigXMLFile(String filePath, Server server) throws JAXBException, SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
        Schema schema = sf.newSchema(new File("Resource\\ConfigurationOfServer.xsd")); 
        
        JAXBContext jaxbContext = JAXBContext.newInstance(Server.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(schema);
        File file = new File(filePath);
        marshaller.marshal(server, file); 
    }
}
