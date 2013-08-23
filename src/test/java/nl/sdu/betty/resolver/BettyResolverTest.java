package nl.sdu.betty.resolver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;

import net.sf.saxon.lib.OutputURIResolver;
import net.sf.saxon.lib.StandardOutputResolver;
import net.sf.saxon.trans.XPathException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class BettyResolverTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BettyResolverTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( BettyResolverTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws TransformerException 
     * @throws UnsupportedEncodingException 
     */
    public void testResolver() throws TransformerException, UnsupportedEncodingException
    {
    	OutputURIResolver bettyResolver = new BettyOutputResolver();
    	OutputURIResolver stdResolver = new StandardOutputResolver();
    	String href = "result/SDU/JUR/#TRANSVERSE/UC/uc.xml";
    	String base = "file:/Users/peetkes/git/betty-poc/result.xml";
    	Result result = null;
    	try {
    		result = stdResolver.resolve(href, base);
    		assertTrue(result == null);
    	} catch (XPathException xe) {
        	assertTrue( result == null);
    	}
    	result = bettyResolver.resolve(href, base);
        assertTrue( result != null );
        String decodedName = URLDecoder.decode(result.getSystemId(),"UTF-8");
        assertTrue(decodedName.contains("#"));
        
    }
}
