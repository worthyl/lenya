package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.wyona.cms.ac.Identity;
import org.wyona.cms.ac.Password;

/**
 * @author Michael Wechner
 * @created 1.12.29
 * @version 2.1.6
 */
public class IMLAuthenticatorAction extends AbstractUsernamePasswordAuthenticatorAction{
  private String domain=null;
  private String port=null;
  private String context=null;
  private String passwd=null;
  private String id=null;
/**
 *
 */
  public void configure(Configuration conf) throws ConfigurationException{
    super.configure(conf);

    Configuration domainConf=conf.getChild("domain");
    domain=domainConf.getValue("127.0.0.1");
    getLogger().debug("CONFIGURATION: domain="+domain);

    Configuration portConf=conf.getChild("port");
    port=portConf.getValue(null);
    getLogger().debug("CONFIGURATION: port="+port);

    Configuration contextConf=conf.getChild("context");
    context=contextConf.getValue(null);
    getLogger().debug("CONFIGURATION: context="+context);

    Configuration passwdConf=conf.getChild("passwd");
    passwd=passwdConf.getValue(null);
    getLogger().debug("CONFIGURATION: passwd="+passwd);
/*
    if(passwd == null){
      throw new ConfigurationException("No passwd path set");
      }
*/

    Configuration idConf=conf.getChild("id");
    id=idConf.getValue(null);
    getLogger().debug("CONFIGURATION: id="+id);
/*
    if(id == null){
      throw new ConfigurationException("No id set");
      }
*/
    }
/**
 *
 */
  public boolean authenticate(String username,String password,Request request,Map map) throws Exception{
    if((username != null) && (password != null)){
      String passwordString=null;
      Document idoc=null;
      try{
        String context=request.getContextPath();
        int port=request.getServerPort();
        idoc=getIdentityDoc(username,port,context);
        passwordString=Identity.getPassword(idoc);
        }
      catch(Exception e){
        getLogger().error(".authenticate(): "+e);
        return false;
        }
      if(Password.encrypt(password).equals(passwordString)){
        Session session=request.getSession(true);
        if(session == null){
          return false;
          }
        Identity identity=new Identity(username,idoc);
        getLogger().debug("IDENTITY: "+identity);
        session.setAttribute("org.wyona.cms.ac.Identity",identity);
        session.setAttribute("org.wyona.cms.cocoon.acting.IMLAuthenticator.id",id);
        return true;
        }
      }
    return false;
    }
/**
 *
 */
  private Document getIdentityDoc(String username,int port,String context) throws Exception{
    DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
    DocumentBuilder db=dbf.newDocumentBuilder();
    String imlURLString="http://"+domain;
    if(this.port != null){
      imlURLString=imlURLString+":"+this.port;
      }
    else{
      imlURLString=imlURLString+":"+port;
      }
    if(this.context != null){
      imlURLString=imlURLString+this.context;
      }
    else{
      imlURLString=imlURLString+context;
      }
    imlURLString=imlURLString+"/"+passwd+username+".iml";
    getLogger().error(imlURLString);
    return db.parse(new URL(imlURLString).openStream());
    }
  }
