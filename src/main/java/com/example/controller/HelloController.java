package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.domain.ec2.Ec2Info;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HelloController {
	@Autowired
	private Ec2Info ec2Info;

	@GetMapping(value = {"/", "/hello"})
	public String getHello(Model model) {
		log.info(ec2Info.toString());
		model.addAttribute("ec2Info", ec2Info);
		
		return "Hello";
	}
}
