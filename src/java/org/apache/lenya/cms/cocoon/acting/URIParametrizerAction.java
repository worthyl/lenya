package org.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.acting.ConfigurableComposerAction;

import org.apache.excalibur.source.Source;
import org.apache.cocoon.xml.AbstractXMLConsumer;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.apache.log4j.Category;

public class URIParametrizerAction extends ConfigurableComposerAction  {
    static Category log = Category.getInstance(URIParametrizerAction.class);

    public class URIParametrizerConsumer extends AbstractXMLConsumer {

	boolean inParamElement = false;
	String parameterValue = null;

	public void startElement(String uri, String loc, String raw, Attributes a) {
	    if (loc.equals("parameter")) {
		log.error("start Element " + uri + ":"+ loc + ":" + raw);
		inParamElement = true;
	    }
	}

	public void endElement(String uri, String loc, String raw, Attributes a) {
	    if (loc.equals("parameter")) {
		log.error("stop Element " + uri + ":"+ loc + ":" + raw);
		inParamElement = false;
	    }
            log.error("processing Element " + uri + ":"+ loc + ":" + raw);
	}

	public void characters(char[] ch, int start, int len) {
	    if (inParamElement) {
		parameterValue = new String(ch, start, len);
		log.error("grab Element " + parameterValue);
	    }
	}
	public String getParameter() {
	    return parameterValue;
	}
    }

    public Map act (Redirector redirector, SourceResolver resolver,
		    Map objectModel, String src, Parameters parameters) throws Exception {
	Source inputSource = null;
	URIParametrizerConsumer xmlConsumer = new URIParametrizerConsumer();

	Map map = new HashMap();
	
	if (this.getLogger().isDebugEnabled()) {
	    this.getLogger().debug("processing file " + src);
	    this.getLogger().debug("file resolved to " + inputSource.getURI());
	}
        
        Request request = ObjectModelHelper.getRequest(objectModel);
        
        String parameterNames[] = parameters.getNames();
        for (int i = 0; i < parameterNames.length; i++) {
	    String parameterSrc = parameters.getParameter(parameterNames[i])
                + request.getRequestURI();
	    inputSource = resolver.resolveURI(parameterSrc);
	    resolver.toSAX(inputSource, xmlConsumer);
	    map.put(parameterNames[i], xmlConsumer.getParameter());
        }

	return map;
    }
}



