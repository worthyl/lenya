/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
/*
 * TaskJob.java
 *
 * Created on November 7, 2002, 3:58 PM
 */
package org.apache.lenya.cms.scheduler;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.task.AbstractTask;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.lenya.cms.task.Task;
import org.apache.lenya.cms.task.TaskManager;
import org.apache.lenya.xml.NamespaceHelper;

import org.apache.log4j.Category;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.w3c.dom.Element;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;


/**
 * A TaskJob is a Job that executes a Task. The task ID is obtained from the <code>task.id</code>
 * request parameter.
 *
 * @author <a href="mailto:ah@lenya.org">Andreas Hartmann</a>
 */
public class TaskJob extends ServletJob {
    public static final String TASK_ID = "id";
    public static final String TASK_PREFIX = "task";
    private static Category log = Category.getInstance(TaskJob.class);

    protected Parameters getParameters(String servletContextPath, HttpServletRequest request) {
        String taskId = request.getParameter(JobDataMapWrapper.getFullName(TASK_PREFIX, TASK_ID));

        assert taskId != null;
        assert !"".equals(taskId);

        log.debug("Creating data map for job " + taskId);

        String contextPath = request.getContextPath();
        log.debug("Context path: " + contextPath);

        // the publicationID is fetched from the session
        String publicationId = (String) request.getSession().getAttribute("org.apache.lenya.cms.cocoon.acting.Authenticator.id");

        assert publicationId != null;
        assert !"".equals(publicationId);

        Parameters parameters = new Parameters();

        parameters.setParameter(Task.PARAMETER_SERVLET_CONTEXT, servletContextPath);
        parameters.setParameter(Task.PARAMETER_CONTEXT_PREFIX, request.getContextPath() + "/");
        parameters.setParameter(Task.PARAMETER_SERVER_PORT,
            Integer.toString(request.getServerPort()));
        log.debug("\n-----------------------------------------------" +
            "\n- Server port from request: " + request.getServerPort() +
            "\n-----------------------------------------------");
        parameters.setParameter(Task.PARAMETER_SERVER_URI, "http://" + request.getServerName());
        parameters.setParameter(Task.PARAMETER_PUBLICATION_ID, publicationId);

        // Add Request Parameters
        Parameters requestParameters = new Parameters();

        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();

            if (name.startsWith(TASK_PREFIX + JobDataMapWrapper.SEPARATOR)) {
                String shortName = JobDataMapWrapper.getShortName(TASK_PREFIX, name);
                requestParameters.setParameter(shortName, request.getParameter(name));
            }
        }

        parameters.merge(requestParameters);

        // /Add Request Parameters
        return parameters;
    }

    /**
     * DOCUMENT ME!
     *
     * @param servletContextPath DOCUMENT ME!
     * @param request DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JobDataMap createJobData(String servletContextPath, HttpServletRequest request) {
        Parameters parameters = getParameters(servletContextPath, request);

        log.debug("Creating job data map:");

        try {
            JobDataMapWrapper map = new JobDataMapWrapper(TASK_PREFIX);
            String[] names = parameters.getNames();

            for (int i = 0; i < names.length; i++) {
                map.put(names[i], parameters.getParameter(names[i]));
            }

            return map.getMap();
        } catch (Exception e) {
            log.error("Cannot create job data map: ", e);

            return null;
        }
    }

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link
     * org.quartz.Trigger}</code> fires that is associated with the <code>Job</code>.
     * </p>
     *
     * @param context DOCUMENT ME!
     *
     * @throws JobExecutionException if there is an exception while executing the job.
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMapWrapper map = new JobDataMapWrapper(jobDetail.getJobDataMap(), TASK_PREFIX);

        //------------------------------------------------------------
        // execute task
        //------------------------------------------------------------
        String taskId = map.get(TASK_ID);

        log.debug("\n-----------------------------------" + "\n Executing task '" + taskId + "'" +
            "\n-----------------------------------");

        String contextPath = map.get(Task.PARAMETER_SERVLET_CONTEXT);
        String publicationId = map.get(Task.PARAMETER_PUBLICATION_ID);

        Publication publication = PublicationFactory.getPublication(publicationId, contextPath);
        TaskManager manager = new TaskManager(publication.getDirectory().getAbsolutePath());

        try {
            Task task = manager.getTask(taskId);
            task.parameterize(map.getParameters());
            task.execute(contextPath);
        } catch (ParameterException e) {
            log.debug("Initializing task failed: ", e);
        } catch (ExecutionException e) {
            log.debug("Executing task failed: ", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param jobElement DOCUMENT ME!
     * @param servletContext DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JobDetail load(Element jobElement, String servletContext) {
        JobDataMap map = new JobDataMap();
        NamespaceHelper helper = SchedulerWrapper.getNamespaceHelper();
        Element taskElement = helper.getFirstChild(jobElement, "task");

        JobDataMapWrapper taskMap = new JobDataMapWrapper(map, TASK_PREFIX);

        String debugString = "\n----------------------------------" + "\nRestoring tasks:" +
            "\n----------------------------------" + "\nTask parameters:";

        Element[] parameterElements = helper.getChildren(taskElement, "parameter");

        for (int i = 0; i < parameterElements.length; i++) {
            String key = parameterElements[i].getAttribute("name");
            String value = parameterElements[i].getAttribute("value");
            taskMap.put(key, value);
            debugString = debugString + "\n" + key + " = " + value;
        }

        // replace servlet-context parameter with actual servlet context
        taskMap.put(AbstractTask.PARAMETER_SERVLET_CONTEXT, servletContext);
        debugString = debugString + "\nReplacing: " + AbstractTask.PARAMETER_SERVLET_CONTEXT +
            " = " + servletContext;

        debugString = debugString + "\nJob parameters:";

        JobDataMapWrapper jobMap = new JobDataMapWrapper(map, SchedulerWrapper.JOB_PREFIX);

        parameterElements = helper.getChildren(jobElement, "parameter");

        for (int i = 0; i < parameterElements.length; i++) {
            String key = parameterElements[i].getAttribute("name");
            String value = parameterElements[i].getAttribute("value");
            jobMap.put(key, value);
            debugString = debugString + "\n" + key + " = " + value;
        }

        log.debug(debugString);

        Class cl = null;
        String jobId = jobMap.get(SchedulerWrapper.JOB_ID);
        String jobGroup = jobMap.get(SchedulerWrapper.JOB_GROUP);

        try {
            cl = Class.forName(jobMap.get(SchedulerWrapper.JOB_CLASS));
        } catch (Exception e) {
            log.error("Cannot load job: ", e);
        }

        JobDetail jobDetail = new JobDetail(jobId, jobGroup, cl);
        jobDetail.setJobDataMap(map);

        return jobDetail;
    }

    /**
     * DOCUMENT ME!
     *
     * @param jobDetail DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element save(NamespaceHelper helper, JobDetail jobDetail) {
        Element jobElement = helper.createElement("job");
        JobDataMap map = jobDetail.getJobDataMap();

        JobDataMapWrapper jobMap = new JobDataMapWrapper(map, SchedulerWrapper.JOB_PREFIX);
        JobDataMapWrapper taskMap = new JobDataMapWrapper(map, TASK_PREFIX);

        // task parameters
        Element taskElement = helper.createElement("task");
        jobElement.appendChild(taskElement);

        Parameters taskParameters = taskMap.getParameters();
        String[] names = taskParameters.getNames();

        for (int i = 0; i < names.length; i++) {
            Element parameterElement = helper.createElement("parameter");
            taskElement.appendChild(parameterElement);
            parameterElement.setAttribute("name", names[i]);
            parameterElement.setAttribute("value", taskMap.get(names[i]));
        }

        // job parameters
        Parameters jobParameters = jobMap.getParameters();
        names = jobParameters.getNames();

        for (int i = 0; i < names.length; i++) {
            Element parameterElement = helper.createElement("parameter");
            jobElement.appendChild(parameterElement);
            parameterElement.setAttribute("name", names[i]);
            parameterElement.setAttribute("value", jobMap.get(names[i]));
        }

        return jobElement;
    }
}
