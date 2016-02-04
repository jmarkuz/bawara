package com.kuziv.onlineshop.bawara.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/user")
public class UserController {

	public UserController() {

	}

	@RequestMapping(value="/login", method=RequestMethod.GET)
	public ModelAndView login() {
	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		
		return modelAndView;
	} 
	
	@RequestMapping(value="/access_denied", method=RequestMethod.GET)
	public ModelAndView accessDenied() {
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("access_denied");
		
		return modelAndView;
	}
	
}
