package com.project.app.ws.ui.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ResponseHeader;

import com.project.app.ws.ui.model.request.LoginRequestModel;

@RestController
public class AuthenticationController {
	
	@ApiOperation(value="User Login",
			notes="${authenticationController.theLogin.ApiOperation.Notes}")
	@ApiResponses(value = {
	@ApiResponse(code = 200,
					message = "Response Headers",
					responseHeaders = {
							@ResponseHeader(name = "authorization",
									description = "Bearer <JWT Token value here>",
									response = String.class),
							@ResponseHeader(name = "userId",
									description = "<Public User ID value here>",
									response = String.class)
					})
	})
	
	@PostMapping("/users/login")
	public void theLogin(@RequestBody LoginRequestModel loginRequestModel) 
	{
		throw new IllegalStateException("Implemented by Spring Security and should not be called.");
	}

}
