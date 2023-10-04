package com.ktdsuniversity.edu.bbs.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ktdsuniversity.edu.bbs.dao.BoardDAO;
import com.ktdsuniversity.edu.bbs.vo.BoardListVO;
import com.ktdsuniversity.edu.bbs.vo.BoardVO;
import com.ktdsuniversity.edu.beans.FileHandler;
import com.ktdsuniversity.edu.beans.FileHandler.StoredFile;

import io.github.seccoding.web.mimetype.ExtFilter;
import io.github.seccoding.web.mimetype.MimeType;
import io.github.seccoding.web.mimetype.abst.ExtensionFilter;
import io.github.seccoding.web.mimetype.factory.ExtensionFilterFactory;

@Service
public class BoardServiceImpl implements BoardService {
	
	@Autowired
	private FileHandler fileHandler;
	
	@Autowired
	private BoardDAO boardDAO;
	
	@Override
	public BoardListVO getAllBoard() {
		
		System.out.println("---------------");
		System.out.println(boardDAO);
		System.out.println(boardDAO.getBoardAllCount());
		System.out.println(boardDAO.getAllBoard());
		
		BoardListVO boardListVO = new BoardListVO();
		boardListVO.setBoardCnt(boardDAO.getBoardAllCount());
		boardListVO.setBoardList(boardDAO.getAllBoard());
		
		return boardListVO;
	}

	@Override
	public boolean createNewBoard(BoardVO boardVO, MultipartFile file) {
		// 파일을 업로드하고 결과를 받아온다.
		StoredFile storedFile = fileHandler.storeFile(file);
		
		// 업로드에 성공했다면
		if (storedFile != null) {
			System.out.println("FileName: " + storedFile.getFileName());
			System.out.println("RealFileName: " + storedFile.getRealFileName());
			System.out.println("FileSize: " + storedFile.getFileSize());
			System.out.println("RealFilePath: " + storedFile.getRealFilePath());
			// 사용자가 입력한 정보에 파일 정보 할당
			boardVO.setFileName(storedFile.getRealFileName());
			boardVO.setOriginFileName(storedFile.getFileName());
			
			// 이미지 파일만 업로드 가능
			ExtensionFilter filter = ExtensionFilterFactory.getFilter(ExtFilter.APACHE_TIKA);
			boolean isImageFile = filter.doFilter(storedFile.getRealFilePath(),
													MimeType.JPEG, MimeType.JPG,
													MimeType.GIF, MimeType.PNG);
			if (!isImageFile) {
				// 이미지 파일이 아니라면
				// 서버에 업로드한 파일을 삭제하고
				File uploadFile = new File(storedFile.getRealFilePath());
				uploadFile.delete();
				// boardVO에 정의한 파일 정보도 삭제
				boardVO.setOriginFileName(null);
				boardVO.setFileName(null);
			}
		}
				
		// DB에 게시글 등록
		// createCount에는 DB에 등록한 게시글의 개수 반환
		int createCount = boardDAO.createNewBoard(boardVO);
		// DB에 등록된 개수가 0보다 크면 성공, 아니라면 실패
		return createCount > 0;
	}

	@Override
	public BoardVO getOneBoard(int id, boolean isIncrease) {
		if (isIncrease) {
			// 파라미터로 전달받은 게시글의 조회수 증가
			// updateCount에는 DB에 업데이트한 게시글 수 반환
			int updateCount = boardDAO.increaseViewCount(id);
			if (updateCount == 0) {
				// updateCount의 값이 0이라는 것은
				// 파라미터로 전달받은 id의 값이 DB에 존재하지 않는다는 의미
				// 이럴 때, "잘못된 접근입니다" 라고 사용자에게 예외 메시지를 보내줌
				throw new IllegalArgumentException("잘못된 접근입니다");
			}
		}
		
		// 예외가 발생하지 않았다면, 게시글 정보를 조회해 반환
		BoardVO boardVO = boardDAO.getOneBoard(id);
		if (boardVO == null) {
			// ID로 조회된 게시글이 없으면
			// "잘못된 접근입니다" 라고 사용자에게 예외 메시지 보냄
			throw new IllegalArgumentException("잘못된 접근입니다");
		}
		
		return boardVO;			
	}

	@Override
	public boolean updateOneBoard(BoardVO boardVO, MultipartFile file) {
		
		// 파일을 업로드했는지 확인
		if (file != null && !file.isEmpty()) {
			// 변경되기 전의 게시글 정보를 가져옴
			BoardVO originBoardVO = boardDAO.getOneBoard(boardVO.getId());
			if (originBoardVO != null && originBoardVO.getFileName() != null) {
				// 변경되기 전 게시글이 파일이 업로드된 게시글일 경우
				// 서버에 파일이 있는지 확인하고 삭제
				File originFile = fileHandler.getStoredFile(originBoardVO.getFileName());
				if (originFile.exists() && originFile.isFile()) {
					originFile.delete();
				}
			}
			
			// 파일을 업로드하고 결과를 받아옴
			StoredFile storedFile = fileHandler.storeFile(file);
			boardVO.setFileName(storedFile.getRealFileName());
			boardVO.setOriginFileName(storedFile.getFileName());
		}
		
		// 파라미터로 전달받은 수정된 게시글의 정보로 DB 수정
		// updateCount에는 DB에 업데이트한 게시글의 수 반환
		int updateCount = boardDAO.updateOneBoard(boardVO);
		return updateCount > 0;
	}

	@Override
	public boolean deleteOneBoard(int id) {
		
		// 삭제되기 전 게시글 정보 가져오기
		BoardVO originBoardVO = boardDAO.getOneBoard(id);
		if (originBoardVO != null && originBoardVO.getFileName() != null) {
			// 삭제되기 전 게시글이 파일로 업로드된 게시글일 경우
			// 파일이 있는지 확인 후 삭제
			File originFile = 
					fileHandler.getStoredFile(originBoardVO.getFileName());
			
			if (originFile.exists() && originFile.isFile()) {
				originFile.delete();
			}
		}	
	
		// 파라미터로 전달받은 ID로 게시글 삭제
		// deleteCount 변수에는 DB에서 삭제한 게시글의 수 반환
		int deleteCount = boardDAO.deleteOneBoard(id);
		return deleteCount > 0;
	}
	
}