/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.lenya.ac.shibboleth;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.util.ServletHelper;

public class ShibbolethUtil {

	private ServiceManager manager;

	public ShibbolethUtil(ServiceManager manager) {
		this.manager = manager;
	}

	/**
	 * @return The base URL, either from the proxy or from the current request
	 *         if no proxy is declared for this area.
	 */
	public String getBaseUrl() {

		String baseUrl = null;
		ContextUtility contextUtil = null;
		try {
			contextUtil = (ContextUtility) this.manager
					.lookup(ContextUtility.ROLE);
			Request request = ObjectModelHelper.getRequest(contextUtil
					.getObjectModel());
			String webappUrl = ServletHelper.getWebappURI(request);
			URLInformation info = new URLInformation(webappUrl);
			String pubId = info.getPublicationId();
			String area = info.getArea();

			if (pubId != null && area != null) {
				Context context = ObjectModelHelper.getContext(contextUtil
						.getObjectModel());
				String servletContextPath = context.getRealPath("");
				Publication pub = PublicationFactory.getPublication(pubId,
						servletContextPath);

				Proxy proxy = pub.getProxy(area, isSslProtected(webappUrl));
				if (proxy != null) {
					baseUrl = proxy.getUrl();
				}
			}

			if (baseUrl == null) {
				int port = request.getServerPort();
				String portSuffix = port == 80 ? "" : ":" + port;
				baseUrl = request.getScheme() + "://" + request.getServerName()
						+ portSuffix;
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (contextUtil != null) {
				this.manager.release(contextUtil);
			}
		}

		return baseUrl;
	}

	protected boolean isSslProtected(String webappUrl) {
		DefaultAccessController accessController = null;
		ServiceSelector selector = null;
		AccessControllerResolver resolver = null;

		try {
			selector = (ServiceSelector) manager
					.lookup(AccessControllerResolver.ROLE + "Selector");
			resolver = (AccessControllerResolver) selector
					.select(AccessControllerResolver.DEFAULT_RESOLVER);

			accessController = (DefaultAccessController) resolver
					.resolveAccessController(webappUrl);
			PolicyManager policyManager = accessController.getPolicyManager();
			Policy policy = policyManager.getPolicy(accessController
					.getAccreditableManager(), webappUrl);
			return policy.isSSLProtected();

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (selector != null) {
				if (resolver != null) {
					if (accessController != null) {
						resolver.release(accessController);
					}
					selector.release(resolver);
				}
				manager.release(selector);
			}
		}
	}
}