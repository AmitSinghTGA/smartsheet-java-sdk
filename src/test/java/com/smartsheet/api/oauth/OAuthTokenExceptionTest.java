package com.smartsheet.api.oauth;

/*
 * #[license]
 * Smartsheet SDK for Java
 * %%
 * Copyright (C) 2014 Smartsheet
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * %[license]
 */

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.smartsheet.api.AccessTokenExpiredException;
import com.smartsheet.api.models.Error;

public class OAuthTokenExceptionTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testOAuthTokenExceptionString() {
		try{
			throw new OAuthTokenException("message");
		}catch(OAuthTokenException e){
			assertEquals("testing testing",e.getMessage());
			//assertEquals(1, e.getErrorCode());
		}
	}

	@Test
	public void testOAuthTokenExceptionStringThrowable() {
		fail("Not yet implemented");
	}

}
