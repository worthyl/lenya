/*
 * Copyright  1999-2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
/* $Id: usecases.js 161122 2005-04-12 21:16:09Z andreas $ */
 

/* Helper method to add all request parameters to a usecase */
function passRequestParameters(flowHelper, usecase) {
    var names = cocoon.request.getParameterNames();
    while (names.hasMoreElements()) {
        var name = names.nextElement();
        if (!name.equals("lenya.usecase")
            && !name.equals("lenya.continuation")
            && !name.equals("submit")) {
            
            var value = flowHelper.getRequest(cocoon).get(name);
            
            var string = new Packages.java.lang.String();
            if (string.getClass().isInstance(value)) {
                usecase.setParameter(name, value);
            }
            else {
                usecase.setPart(name, value);
            }
            
        }
    }
}


/* Returns the query string to attach to the target URL. This is used in the site area. */
function getTargetQueryString(usecaseName) {
    var isTabUsecase = new Packages.java.lang.String(usecaseName).startsWith('tab');
    var queryString = "";
    if (isTabUsecase) {
        queryString = "?lenya.usecase=" + usecaseName;
    }
    return queryString;
}


/*
 * Start the editing process.
 */
function startEditing() {
    var usecaseName = cocoon.request.getParameter("lenya.usecase");
    var view;
    var proxy;
    
    var usecaseResolver;
    var usecase;
    
    if (cocoon.log.isDebugEnabled())
       cocoon.log.debug("usecases.js::executeUsecase() called, parameter lenya.usecase = [" + usecaseName + "]");
    
    try {
        usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
        usecase = usecaseResolver.resolve(usecaseName);

        var flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
        var request = flowHelper.getRequest(cocoon);
        var sourceUrl = Packages.org.apache.lenya.util.ServletHelper.getWebappURI(request);
        usecase.setSourceURL(sourceUrl);
        usecase.setName(usecaseName);
        view = usecase.getView();

        passRequestParameters(flowHelper, usecase);
        
        if (request.getRequestMethod().equalsIgnoreCase("GET")) {
        }
        else {
        }
        
        usecase.checkPreconditions();
        usecase.lockInvolvedObjects();
        proxy = new Packages.org.apache.lenya.cms.usecase.UsecaseProxy(usecase);
    }
    finally {
        /* done with usecase component, tell usecaseResolver to release it */
        if (usecaseResolver != null) {
            if (usecase != null) {
                usecaseResolver.release(usecase);
                usecase = undefined;
            }
            cocoon.releaseComponent(usecaseResolver);
        }
    }
    
    try {
        var viewUri = "view/nomenu/" + view.getTemplateURI();
        cocoon.sendPage(viewUri, { "usecase" : proxy });
    }
    catch (exception) {
        /* if an exception was thrown by the view, allow the usecase to rollback the transition */
        try {
            usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
            usecase = usecaseResolver.resolve(usecaseName);
            proxy.setup(usecase);
            usecase.cancel();
            throw exception;
        }
        finally {
            usecaseResolver.release(usecase);
            usecase = undefined;
            cocoon.releaseComponent(usecaseResolver);
        }
    }
}


function save() {

    var usecaseName = cocoon.request.getParameter("lenya.usecase");
    var view;
    
    var usecaseResolver;
    var usecase;

    try {
        usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
        usecase = usecaseResolver.resolve(usecaseName);
        proxy.setup(usecase);
    
        passRequestParameters(flowHelper, usecase);
        usecase.advance();
    
        var view = usecase.getView();
        var viewUri = "view/nomenu/" + view.getTemplateURI();
        cocoon.sendPage(viewUri, { "usecase" : proxy });
    }
    catch (exception) {
        /* if an exception was thrown by the view, allow the usecase to rollback the transition */
        try {
            usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
            usecase = usecaseResolver.resolve(usecaseName);
            proxy.setup(usecase);
            usecase.cancel();
            throw exception;
        }
        finally {
            usecaseResolver.release(usecase);
            usecase = undefined;
            cocoon.releaseComponent(usecaseResolver);
        }
    }
    
}
