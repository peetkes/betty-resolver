/**
 * 
 */
package nl.sdu.betty.resolver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.UnknownServiceException;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.lib.StandardOutputResolver;
import net.sf.saxon.trans.XPathException;

/**
 * This class defines a specific OutputURIResolver to handle file related URIs containing the pound sign. 
 * It is used to map the URI of a secondary result document to a Result object
 * which acts as the destination for the new document.
 * When the URI has a file scheme (starts with file:) and contains a fragment, it will generate the file.
 * The original StandardOutputResolver will fail with a message stating: 
 * Cannot write to URI file:/location#otherlocation
 * (URI has a fragment component)
 *
 * @author peetkes
 *
 */
public class BettyOutputResolver extends StandardOutputResolver {

	@Override
	public Result resolve(String href, String base) throws XPathException {
//		System.err.println(String.format("Output URI Resolver(href='%s', base='%s'",href, base));
		try {
			URI absoluteURI;
			if (href.length() == 0) {
				if (base == null) {
					throw new XPathException("The system identifier of the principal output file is unknown");
				}
				absoluteURI = new URI(base);
			} else {
				absoluteURI = new URI(href);
			}
			if (!absoluteURI.isAbsolute()) {
				if (base == null) {
					throw new XPathException("The system identifier of the principal output file is unknown");
				}
				URI baseURI = new URI(base);
				absoluteURI = baseURI.resolve(href);
			}
			if (("file".equals(absoluteURI.getScheme())) && (absoluteURI.getFragment() != null)) {
				return makeFragmentedOutputFile(absoluteURI.toASCIIString().replace("file:",""));
			} else if ("file".equals(absoluteURI.getScheme())) {
				return makeOutputFile(absoluteURI);
			} else {
				URLConnection connection = absoluteURI.toURL().openConnection();
				connection.setDoInput(false);
				connection.setDoOutput(true);
				connection.connect();
				OutputStream os = connection.getOutputStream();
				StreamResult result = new StreamResult(os);
				result.setSystemId(absoluteURI.toASCIIString());
				return result;
			}
		} catch (URISyntaxException err) {
			throw new XPathException("Invalid syntax for base URI", err);
        } catch (IllegalArgumentException err2) {
               throw new XPathException("Invalid URI syntax", err2);
        } catch (MalformedURLException err3) {
           throw new XPathException("Resolved URL is malformed", err3);
        } catch (UnknownServiceException err5) {
            throw new XPathException("Specified protocol does not allow output", err5);
        } catch (IOException err4) {
            throw new XPathException("Cannot open connection to specified URL", err4);
		}
	}

	@Override
	public void close(Result result) throws XPathException {
//		System.err.println(String.format("** End %s **", result.getSystemId()));
		super.close(result);
	}

	public static synchronized Result makeFragmentedOutputFile(String fileName) throws XPathException {
		System.err.println(String.format("makeFragmentedOutputFile %s", fileName));
		File newFile = new File(fileName);
		
        try {
            if (!newFile.exists()) {
                File directory = newFile.getParentFile();
                if (directory != null && !directory.exists()) {
                    directory.mkdirs();
                }
                newFile.createNewFile();
            }
            return new StreamResult(newFile);
        } catch (IOException err) {
            throw new XPathException("Failed to create output file " + fileName, err);
        }
	}
}
