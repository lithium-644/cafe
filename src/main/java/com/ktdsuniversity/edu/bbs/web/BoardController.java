package com.ktdsuniversity.edu.bbs.web;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ktdsuniversity.edu.bbs.service.BoardService;
import com.ktdsuniversity.edu.bbs.vo.BoardListVO;
import com.ktdsuniversity.edu.bbs.vo.BoardVO;
import com.ktdsuniversity.edu.beans.FileHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class BoardController {
	
	@Autowired
	private FileHandler fileHandler;
	
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/board/list")
	public ModelAndView viewBoardList() {
		BoardListVO boardListVO = boardService.getAllBoard();

		ModelAndView modelAndView = new ModelAndView();
		//	/WEB-INF/views/board/boardlist.jsp
		modelAndView.setViewName("board/boardlist");
		modelAndView.addObject("boardList", boardListVO);
		
		return modelAndView;
	}
	
	@GetMapping("/board/write")
	public String viewBoardWritePage() {
		return "board/boardwrite";
	}
	
	@PostMapping("/board/write")
	public ModelAndView doBoardWrite(@Valid @ModelAttribute BoardVO boardVO
									// Validation 실패 결과를 갖고있음
									// @Valid 바로 다음에 있어야 함
									, BindingResult bindingResult 
									, @RequestParam MultipartFile file
									, HttpServletRequest request) {
		
		// 요청자의 IP 정보를 ipAddr 변수에 할당
		boardVO.setIpAddr(request.getRemoteAddr());
		
		System.out.println("첨부파일명: " + file.getOriginalFilename());
		System.out.println("제목: " + boardVO.getSubject());
		System.out.println("이메일: " + boardVO.getEmail());
		System.out.println("내용: " + boardVO.getContent());
		System.out.println("등록일: " + boardVO.getCrtDt());
		System.out.println("수정일: " + boardVO.getMdfyDt());
		System.out.println("FileName: " + boardVO.getFileName());
		System.out.println("OriginFileName: " + boardVO.getOriginFileName());
		
		ModelAndView modelAndView = new ModelAndView();
		
		// Validation 체크한 것 중 실패한 것이 있다면
		if (bindingResult.hasErrors()) {
			// 화면을 보여줌
			// 게시글 등록은 하지 않음
			modelAndView.setViewName("board/boardwrite");
			modelAndView.addObject("boardVO", boardVO);
			return modelAndView;
		}
		
		// 게시글 등록
		boolean isSuccess = boardService.createNewBoard(boardVO, file);
		if (isSuccess) {
			// 게시글 등록 결과가 성공이라면
			// /board/list URL로 이동
			// redirect: 는 화면을 이동시키는 View
			// redirect로 화면을 전환시킬 때는 addObject를 사용할 수 없음
			modelAndView.setViewName("redirect:/board/list");
			return modelAndView;
		}
		else {
			// 게시글 등록 결과가 실패하면
			// 게시글 등록(작성) 화면으로 데이터를 보내줌
			// 게시글 등록(작성) 화면에서 boardVO 값으로 등록값 설정해야함
			modelAndView.setViewName("board/boardwrite");
			modelAndView.addObject("boardVO", boardVO);
			return modelAndView;
		}
				
	}
	
	// http://localhost:8080/board/view?id=1 // 쿼리 스트링
	// @RequestParam 파라미터를 받아오는 방법 중 하나
	//		==> 전달되는 파라미터 1개 내지 2개 정도일 때 유용
	// 			==> 파라미터의 이름과 변수의 이름이 동일
	// @ModelAttribute 파라미터를 받아오는 방법 중 하나
	// 		==> Command Object : 전달되는 파라미터들이 여러개일 때 유용
	//					==> 파라미터의 이름과 VO클래스 멤버변수들의 이름을 동일하게 부여
	@GetMapping("/board/view")
	public ModelAndView viewOneBoard(@RequestParam int id) {
		System.out.println("조회할 게시글의 번호: " + id);
		
		BoardVO boardVO = boardService.getOneBoard(id, true);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("board/boardview");
		modelAndView.addObject("boardVO", boardVO);
		
		return modelAndView;
	}
	
	@GetMapping("/board/file/download/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable int id) {
		// 파일 정보를 얻어오기 위해 게시글 조회
		BoardVO boardVO = boardService.getOneBoard(id, false);
		if (boardVO == null) {
			throw new IllegalArgumentException("잘못된 접근입니다");
		}
		
		// 서버에 등록되어있는 파일을 가져옴
		File storedFile = fileHandler.getStoredFile(boardVO.getFileName());
		
		// 다운로드 ㄱㄱ
		return fileHandler.getResponseEntity(storedFile, boardVO.getOriginFileName());
	}
	
	// http://localhost:8080/board/modify/1
	@GetMapping("/board/modify/{id}")
	public String viewBoardModifyPage(@PathVariable int id, Model model) {
		System.out.println("PathVariable: " + id);
		
		// 수정할 게시글 조회
		BoardVO boardVO = boardService.getOneBoard(id, false);
		
		// View에게 boardVO라는 이름으로 boardVO 전달
		model.addAttribute("boardVO", boardVO);
		return "board/boardmodify";
	}
	
	@PostMapping("/board/modify")
	public ModelAndView doBoardUpdate(
			@ModelAttribute BoardVO boardVO,
			@RequestParam MultipartFile file) {
		System.out.println("제목: " + boardVO.getSubject());
		System.out.println("이메일: " + boardVO.getEmail());
		System.out.println("내용: " + boardVO.getContent());
		System.out.println("등록일: " + boardVO.getCrtDt());
		System.out.println("수정일: " + boardVO.getMdfyDt());
		System.out.println("FileName: " + boardVO.getFileName());
		System.out.println("OriginFileName: " + boardVO.getOriginFileName());	
		
		ModelAndView modelAndView = new ModelAndView();
		
		// 게시글 수정
		boolean isSuccess = boardService.updateOneBoard(boardVO, file);
		
		if (isSuccess) {
			// 게시글 수정 결과가 성공이라면
			// /board/view?id=1 URL로 이동
			modelAndView.setViewName("redirect:/board/view?id=" + boardVO.getId());
			return modelAndView;
		}
		else {
			// 게시글 수정 결과가 실패라면
			// 게시글 수정 화면으로 데이터를 보내줌
			modelAndView.setViewName("board/boardmodify");
			modelAndView.addObject("boardVO", boardVO);
			return modelAndView;
		}
	}
	
	@GetMapping("/board/delete/{id}")
	public String doDeleteBoard(@PathVariable int id) {
		// 게시글 삭제
		boolean isSuccess = boardService.deleteOneBoard(id);
		if (isSuccess) {
			// 삭제 결과가 성공이라면
			// /board/list로 이동
			return "redirect:/board/list";
		}
		else {
			// 삭제 결과가 실패라면
			// /board/view?id=id 주소로 이동
			return "redirect:/board/view?id=" + id;
		}
	}

}