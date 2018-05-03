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

import fr.paris.lutece.portal.service.security.LuteceUser;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;

import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;

import java.util.ArrayList;


/**
 *
 * Utility class for LuteceUser to UserDetails mappings
 *
 */
public class LuteceUserWrapper
{
    private static final String DEFAULT_PASSWORD = "default";
    private static final String ROLE_NONE = "ROLE_NONE";

    /**
     * Default private constructor
     */
    private LuteceUserWrapper(  )
    {
    }

    /**
     * create a UserDetails form a LuteceUser
     * @param user the LuteceUser
     * @return the wrapped UserDetails
     */
    public static UserDetails wrap( final LuteceUser user )
    {
        //		return new User( user.getName(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities() );
        return new User( user.getName(  ), DEFAULT_PASSWORD, true, true, true, true, getGrantedAuthorities( user ) );
    }

    /**
     * Convert Lutece roles to GrantedAuthorities
     * @param user the LuteceUser
     * @return converted roles
     */
    private static GrantedAuthority[] getGrantedAuthorities( final LuteceUser user )
    {
        ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(  );

        // check if user has at least one role
        if ( ( user.getRoles(  ) == null ) || ( user.getRoles(  ).length == 0 ) )
        {
            authorities.add( new GrantedAuthorityImpl( ROLE_NONE ) );
        }
        else
        {
            for ( String strRole : user.getRoles(  ) )
            {
                authorities.add( new GrantedAuthorityImpl( strRole ) );
            }
        }

        return authorities.toArray( new GrantedAuthorityImpl[] {  } );
    }
}
