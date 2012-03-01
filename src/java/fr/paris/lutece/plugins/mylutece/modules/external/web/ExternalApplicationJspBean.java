/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.mylutece.modules.external.web;

import fr.paris.lutece.plugins.mylutece.authentication.PortalAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.external.business.IExternalApplication;
import fr.paris.lutece.plugins.mylutece.modules.external.service.ExternalApplicationServiceImpl;
import fr.paris.lutece.plugins.mylutece.modules.external.service.IExternalApplicationService;
import fr.paris.lutece.portal.service.init.LuteceInitException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ExternalApplicationJspBean extends PluginAdminPageJspBean
{
    //Right
    public static final String RIGHT_MANAGE_EXTERNAL_APPLICATION = "MYLUTECE_MANAGE_EXTERNAL_APPLICATION";

    //properies
    private static final String PROPERTY_AUTHENTICATION_CLASS = "mylutece.authentication.class";

    //Parameters
    private static final String PARAMETER_MYLUTECE_USER_ID = "mylutece_user_id";
    private static final String PARAMETER_MYLUTECE_EXTERNAL_APPLICATION_NAME = "external_application_name";

    //Marks freemarker
    private static final String MARK_USER = "mylutece_user_id";
    private static final String MARK_EXTERNAL_APPLICATION_LIST = "external_application_list";

    //Templates
    private static final String TEMPLATE_MANAGE_EXTERNAL_APPLICATION = "admin/plugins/mylutece/modules/external/manage_external_application.html";

    //jsp
    private static final String JSP_MANAGE_EXTERNAL_APPLICATION = "ManageExternalApplicationUser.jsp";

    //messages
    private static final String MESSAGE_USER_EXIST = "mylutece.message.user_exist";

    /**
     * Displays list of available applications for a given user
     * @param request
     * @return
     */
    public String getManageApplications( HttpServletRequest request )
    {
        String strUserId = request.getParameter( PARAMETER_MYLUTECE_USER_ID );

        IExternalApplicationService externalApplicationService = ExternalApplicationServiceImpl.getInstance(  );
        Collection<IExternalApplication> allExternalApplication = externalApplicationService.getAvailableApplications(  );

        HashMap model = new HashMap(  );
        model.put( MARK_EXTERNAL_APPLICATION_LIST, allExternalApplication );
        model.put( MARK_USER, strUserId );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_EXTERNAL_APPLICATION, getLocale(  ),
                model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * @param request
     * @return
     */
    public String doCreateUser( HttpServletRequest request, HttpServletResponse response )
    {
        String strAppName = request.getParameter( PARAMETER_MYLUTECE_EXTERNAL_APPLICATION_NAME );
        String strUserId = request.getParameter( PARAMETER_MYLUTECE_USER_ID );

        IExternalApplicationService appService = ExternalApplicationServiceImpl.getInstance(  );

        //TODO Evol core : add getUser( strUserLogin )  method in SecurityService...
        // ! strUserId must contain the username
        LuteceUser user = null;

        try
        {
            PortalAuthentication portalAuth = ( (PortalAuthentication) getPortalAuthentication(  ) );
            user = portalAuth.getUser( strUserId );
        }
        catch ( LuteceInitException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        if ( appService.getApplicationByName( strAppName ).userExists( strUserId, request, response ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_USER_EXIST, AdminMessage.TYPE_STOP );
        }

        appService.getApplicationByName( strAppName ).createRemoteUser( user );

        return JSP_MANAGE_EXTERNAL_APPLICATION + "?" + PARAMETER_MYLUTECE_USER_ID + "=" + strUserId;
    }

    public String doApplicationRedirect( HttpServletRequest request )
    {
        String strAppName = request.getParameter( PARAMETER_MYLUTECE_EXTERNAL_APPLICATION_NAME );

        IExternalApplicationService appService = ExternalApplicationServiceImpl.getInstance(  );

        String strUrl = appService.getApplicationByName( strAppName ).getAdminUrl(  );

        // return the right redirection
        return strUrl;
    }

    /**
    * Retrieves the portal authentication service configured in the config.properties
    * @return A PortalAuthentication object
    */
    private static LuteceAuthentication getPortalAuthentication(  )
        throws LuteceInitException
    {
        String strAuthenticationClass = AppPropertiesService.getProperty( PROPERTY_AUTHENTICATION_CLASS );
        LuteceAuthentication authentication = null;

        try
        {
            authentication = (LuteceAuthentication) Class.forName( strAuthenticationClass ).newInstance(  );
            AppLogService.info( "Authentication service loaded : " + authentication.getAuthServiceName(  ) );
        }
        catch ( InstantiationException e )
        {
            throw new LuteceInitException( "Error instantiating Authentication Class", e );
        }
        catch ( IllegalAccessException e )
        {
            throw new LuteceInitException( "Error instantiating Authentication Class", e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new LuteceInitException( "Error instantiating Authentication Class", e );
        }

        return authentication;
    }
}
