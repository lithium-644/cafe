package com.ktdsuniversity.edu.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloBootController {
	
	@GetMapping("/hello") // 이 데이터 들어오면 이하를 반환
	public ResponseEntity<String> hello() {
		return new ResponseEntity<>("Hello Spring Boot Controller", HttpStatus.OK); // (보내줄데이터, 상태코드)
	}
	
	@GetMapping("/hello2")
	public ResponseEntity<String> hello2() {
		StringBuffer html = new StringBuffer();
		html.append("<!DOCTYPE html>");
		html.append("<html>");
		html.append("<head><title>Hello, Boot!</title></head>");
		html.append("<body>");
		html.append("<div>안녕하세요?</div>");
		html.append("<div>Spring Boot에서 응답되었습니다</div>");
		html.append("</body>");
		html.append("</html>");
		return new ResponseEntity<>(html.toString(), HttpStatus.OK);
	}
	
	// html 코드 반환
	@GetMapping("/hello3")
	public String HelloJsp() {
		return "helloboot"; // 파일명과 완전히 동일하게 적기
	}
	
	@GetMapping("/hello4")
	public ModelAndView helloModelAndView() {
		ModelAndView view = new ModelAndView();
		view.setViewName("helloboot");
		view.addObject("myname", "Spring Boot~!"); // 컨트롤러와 키 이름이 완전히 동일해야 바인딩
		return view;
	}
	
	@GetMapping("/hello5")
	public String helloModel(Model model) {
		model.addAttribute("myname", "Cafe Demo"); // (키, 값)
		return "helloboot";
	}

}