/**
 * @author muhammad.ahmed@ihsinformatics@gmail.com
 * and Shakeeb.raza@ihsinformatics.com
 */
package org.opensrp.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import static org.opensrp.web.HttpHeaderFactory.allowOrigin;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.ErrorTrace;
import org.opensrp.service.ErrorTraceService;
import org.opensrp.web.ErrorTraceForm;

import com.google.gson.Gson;

@Controller
@RequestMapping("/errorhandler")
public class ErrorTraceController {

	private String opensrpSiteUrl;
	private ErrorTraceService errorTraceService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, false));
	}
	
	@RequestMapping("/errorhandler")
	public String Redirecting(HttpServletRequest request,
			ErrorTraceForm errorTraceForm, BindingResult errors) {
			return "redirect:/errorhandler/index";
	}
	
	@Autowired
	public ErrorTraceController(ErrorTraceService errorTraceService) {
		this.errorTraceService = errorTraceService;
	}

	@RequestMapping(method = GET, value = "/index")
	public ModelAndView showPage() {
		Map<String, Object> model = new HashMap<String, Object>();
		ErrorTraceForm errorForm=new ErrorTraceForm();
		Gson gson = new Gson();
	    String optionsJson = gson.toJson(errorForm.getStatusOptions());
		model.put("statusOptions",optionsJson);
		model.put("type", "all");
		return new ModelAndView("home_error", model);
	}
	
	@RequestMapping(method = GET, value = "/errortrace")
	@ResponseBody
	public ResponseEntity<List<ErrorTrace>> allErrors(@RequestParam("status") String status) {
		List<ErrorTrace> list=null;
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			if(StringUtils.isBlank(status))
			{
				list = errorTraceService.getAllError();
			}
			else
			{
				list = errorTraceService.getErrorsByStatus(status);
			}
			model.put("errors", list);
			return new ResponseEntity<>(list, HttpStatus.OK);
			} catch (Exception e)
			{
			e.printStackTrace();
			return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
			}

	}
	
	  @RequestMapping(method=GET,value="/viewerror") 
	  @ResponseBody
	  public ResponseEntity<ErrorTraceForm>  showError(@RequestParam("id") String id){
	  try{
	  ErrorTrace error=errorTraceService.getError(id); 
	  
	  
	  ErrorTraceForm errorTraceForm=new ErrorTraceForm();
	  errorTraceForm.setErrorTrace(error);
	  System.out.println("error ID :" +	  errorTraceForm.getErrorTrace().getId());
	  
	
	  return new ResponseEntity<>(errorTraceForm, HttpStatus.OK);
	  }catch (Exception e)
	  {
		e.printStackTrace();
		return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
	  }
	  }
	  
	  
	  @RequestMapping(method=GET,value="/getstatusoptions") 
	  @ResponseBody
	  public ResponseEntity<List<String>>  statusOptions(){
	  try{
	  ErrorTraceForm errorTraceForm=new ErrorTraceForm();
	  return new ResponseEntity<>(errorTraceForm.getStatusOptions(), HttpStatus.OK);
	  }catch (Exception e)
	  {
		e.printStackTrace();
		return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
	  }
	  }
	 
	  /**@authors engrmahmed14@gmail.com and shakeeb.raza@ihsinformatics.com
	   * @return String , value of the view error page
	   * @param ErrorTraceForm 
	   * this method uses spring binding for form update . 
	   * 
	   * 
	   */
	@RequestMapping(value = "/update_errortrace", method = POST)
	public String updateErrorTrace(HttpServletRequest request,
			ErrorTraceForm errorTraceForm, BindingResult errors) {
		if (errors.hasErrors()) {}
		System.out.println(errorTraceForm.getErrorTrace().getId());
		ErrorTrace errorTrace = errorTraceService.getError(errorTraceForm.getErrorTrace().getId());
		errorTrace.setStatus(errorTraceForm.getErrorTrace().getStatus());
		errorTraceService.updateError(errorTrace);
		// System.out.println("page context :: "+request.getContextPath());
		return "redirect:/errorhandler/viewerror?id=" + errorTrace.getId();
	}
	
	@RequestMapping(value="/update_status", method=GET)
	public String UpdateStatus(@RequestParam("id") String id, @RequestParam("status") String status){
		ErrorTrace errorTrace = errorTraceService.getError(id);
		errorTrace.setStatus(status);
		errorTraceService.updateError(errorTrace);
		return "redirect:/errorhandler/index";
	}
	

	@RequestMapping(method = GET, value = "/allerrors")
	@ResponseBody
	public <T> ResponseEntity<T> getAllErrors() {
		List<ErrorTrace> list = errorTraceService.getAllError();
		if (list == null) {
			return (ResponseEntity<T>) new ResponseEntity<>("No Record(s) Found .", allowOrigin(opensrpSiteUrl), OK);
		}
		return (ResponseEntity<T>) new ResponseEntity<>(list,allowOrigin(opensrpSiteUrl), OK);
	}

}
