/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.mylutece.modules.external.service;

import fr.paris.lutece.portal.service.security.LoginRedirectException;
import fr.paris.lutece.portal.service.security.SecurityService;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;

import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;

import javax.security.auth.login.LoginException;

import javax.servlet.http.HttpServletRequest;


/**
 * Implementation of an AuthenticationProvider
 * This class aims to be called by remote Lutece adapter
 */
public class MyLuteceAuthenticationServiceImpl implements MyLuteceAuthenticationService
{
    /* (non-Javadoc)
     * @see org.acegisecurity.providers.AuthenticationProvider#authenticate(org.acegisecurity.Authentication)
     */
    public Authentication authenticate( Authentication authentication )
        throws AuthenticationException
    {
        String strUserName = authentication.getName(  );
        String strPassword = authentication.getCredentials(  ).toString(  );

        UserDetails user = null;

        try
        {
            HttpServletRequest request = new RemoteServletRequest(  );
            user = LuteceUserWrapper.wrap( SecurityService.getInstance(  )
                                                          .remoteLoginUser( request, strUserName, strPassword ) );
        }
        catch ( LoginException e )
        {
            throw new UsernameNotFoundException( strUserName );
        }
        catch ( LoginRedirectException e )
        {
            throw new UsernameNotFoundException( strUserName );
        }

        return new UsernamePasswordAuthenticationToken( user, "", user.getAuthorities(  ) );
    }

    /* (non-Javadoc)
     * @see org.acegisecurity.providers.AuthenticationProvider#supports(java.lang.Class)
     */
    public boolean supports( Class authentication )
    {
        // TODO replace with right class implementation
        return false;
    }
}
