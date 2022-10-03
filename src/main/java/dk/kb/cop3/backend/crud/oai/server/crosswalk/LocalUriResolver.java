package dk.kb.cop3.backend.crud.oai.server.crosswalk;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.Source;
import java.lang.String;
import java.net.URL;
import org.apache.log4j.Logger;

public class LocalUriResolver implements URIResolver
{

    private static Logger logger = Logger.getLogger(MetadataCreator.class);

    public Source resolve(String fileName, String base) throws TransformerException
    {
	logger.info("Resolving " + fileName + " against base " + base);
        URL url = getClass().getClassLoader().getResource(fileName);
        StreamSource jarFileSS = new StreamSource();

        try
        {
            InputStream jarfileIS = url.openStream();
            jarFileSS.setInputStream(jarfileIS);
        }
        catch(IOException ioExp)
        {
	    logger.error("Error " + ioExp.getMessage());
            throw new TransformerException(ioExp);
        }
        return jarFileSS;
    }
}
