package com.spring.app;



import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.bean.Loginbean;
import com.spring.app.bean.UserBean;
import com.spring.app.model.Complaint;
import com.spring.app.model.Occupied_apartment;
import com.spring.app.model.Otp;
import com.spring.app.model.User;
import com.spring.app.service.ApartmentService;
import com.spring.app.service.ComplaintService;
import com.spring.app.service.Occupied_apartmentService;
import com.spring.app.service.OtpService;
import com.spring.app.service.UserService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	OtpService otpService;
	
	@Autowired
	ApartmentService apartmentService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ComplaintService complainService;
	@Autowired
	Occupied_apartmentService occService;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Insertin complain", locale);
		 Complaint c = new Complaint();
		 c.setDescription("test");
		 c.setSeverity(1);
		 c.setTime(456456654);
		 c.setType(1);
		 c.setUnit("9545F");
		 complainService.addUser(c);
		return "home";
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signupPage(Model model) {
		UserBean signupBean=new UserBean();
		model.addAttribute("signupBean", signupBean);
		return "signup";
	}
	@RequestMapping(value="/signup.submit", method = RequestMethod.POST)
	public ModelAndView executeSignup(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("signupBean")UserBean signupBean)
	{
		ModelAndView model= null;
		List<Otp> otpList; 
		otpList=otpService.listOtp();
		for(Otp o:otpList){
			if(signupBean.getOtp() == o.getOtp()){
				System.out.println("Otp matched for APT No: "+ o.getUnit());
				this.DeleteApartment(o.getUnit());
				System.out.println("In controller again");
				User user=new User();
				user.setFirstname(signupBean.getFirstname());
				user.setLastname(signupBean.getLastname());
				user.setEmail(signupBean.getEmail());
				user.setPassword(signupBean.getPassword());
				user.setType(2);
				user.setUnit(o.getUnit());
				System.out.println("calling addUser from controller");
				this.userService.addUser(user);
				this.OccupyApartment(o.getUnit(), signupBean.getEmail());
				model=new ModelAndView("login");
				Loginbean loginBean=new Loginbean();
				model.addObject("loginBean",loginBean);
				return model;
			}
		}
		request.setAttribute("SignUpmessage", "Incorrect otp credential");
		model=new ModelAndView("signup");
		return model;
		
	}
	
protected void DeleteApartment(String unit){
	apartmentService.deleteAvailableApartment(unit);
}

protected void OccupyApartment(String unit,String email){
	occService.addOccupiedApartment(unit,email);
}
	
}
