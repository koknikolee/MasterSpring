package com.project.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.ws.exceptions.UserServiceException;
import com.project.app.ws.service.AddressService;
import com.project.app.ws.service.UserService;
import com.project.app.ws.shared.Roles;
import com.project.app.ws.shared.dto.AddressDTO;
import com.project.app.ws.shared.dto.UserDto;
import com.project.app.ws.ui.model.request.UserDetailsRequestModel;
import com.project.app.ws.ui.model.response.AddressesRest;
import com.project.app.ws.ui.model.response.ErrorMessages;
import com.project.app.ws.ui.model.response.OperationStatusModel;
import com.project.app.ws.ui.model.response.RequestOperationName;
import com.project.app.ws.ui.model.response.RequestOperationStatus;
import com.project.app.ws.ui.model.response.UserRest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController

@RequestMapping("users") // http://localhost:8080/mobile-app-ws/users
public class UserController {

	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressService;

	@Autowired
	AddressService addressesService;

	@ApiOperation(value="Get User Details",
			notes="${userController.GetUser.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String id) {
		UserRest returnValue = new UserRest();

		UserDto userDto = userService.getUserByUserId(id);
		ModelMapper modelMapper = new ModelMapper();
		returnValue = modelMapper.map(userDto, UserRest.class);

		return returnValue;
	}

	@ApiOperation(value="Create a New User",
			notes="${userController.CreateUser.ApiOperation.Notes}")
	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserRest returnValue = new UserRest();

		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		// UserDto userDto = new UserDto();
		// BeanUtils.copyProperties(userDetails, userDto);
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));

		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;
	}

	@ApiOperation(value="Update an existing User",
			notes="${userController.UpdateUser.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
	produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnValue = new UserRest();

		UserDto userDto = new UserDto();
		userDto = new ModelMapper().map(userDetails, UserDto.class);
		
		UserDto updateUser = userService.updateUser(id, userDto);
		returnValue = new ModelMapper().map(updateUser, UserRest.class);

		return returnValue;
	}

	@ApiOperation(value="Delete User",
			notes="${userController.DeleteUser.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@Secured("ROLE_ADMIN")
	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());

		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

		return returnValue;
	}

	@ApiOperation(value="Get a List of Users by pages",
			notes="${userController.GetUsers.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {
		List<UserRest> returnValue = new ArrayList<>();

		List<UserDto> users = userService.getUsers(page, limit);
		
		Type listType = new TypeToken<List<UserRest>>() {
		}.getType();
		returnValue = new ModelMapper().map(users, listType);

		return returnValue;
	}

	// http://localhost:8080/users/UserID(laj234mladsf4knmxgh)/addresses
	
	@ApiOperation(value="Get a List of User's Addresses",
			notes="${userController.GetUserAddresses.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {
		List<AddressesRest> returnValue = new ArrayList<>();

		List<AddressDTO> addressesDTO = addressesService.getAddresses(id);

		if (addressesDTO != null && !addressesDTO.isEmpty()) {
			java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {
			}.getType();

			returnValue = new ModelMapper().map(addressesDTO, listType);
			
			for(AddressesRest addressRest : returnValue) {
				Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
						.getUserAddress(id, addressRest.getAddressId()))
						.withSelfRel();
				addressRest.add(selfLink);
			}
		}
		
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
				.getUserAddresses(id))
				.withSelfRel();

		return CollectionModel.of(returnValue, userLink, selfLink);
	}

	@ApiOperation(value="Get a specific Address of User",
			notes="${userController.GetUserAddress.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		
		AddressDTO addressesDto = addressService.getAddress(addressId);

		ModelMapper modelMapper = new ModelMapper();
		AddressesRest returnValue = modelMapper.map(addressesDto, AddressesRest.class);
		
		// http://localhost:8080/users/<userId>/addresses/{addressId}
		
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
		Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
				.getUserAddresses(userId))
				.withRel("addresses");
		
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
				.getUserAddress(userId,addressId))
				.withSelfRel();
		
		return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink)); 
	}
}
