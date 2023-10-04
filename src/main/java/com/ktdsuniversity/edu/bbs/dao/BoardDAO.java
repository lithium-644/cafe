package com.ktdsuniversity.edu.bbs.dao;

import java.util.List;

import com.ktdsuniversity.edu.bbs.vo.BoardVO;

public interface BoardDAO {
	
	/**
	 * DB에 저장된 모든 게시글의 수 조회
	 * @return 게시글의 수
	 */
	public int getBoardAllCount();
	
	/**
	 * DB에 저장된 모든 게시글의 목록 조회
	 * @return 게시글 목록
	 */
	public List<BoardVO> getAllBoard();
	
	/**
	 * DB에 새로운 게시글 등록
	 * @param boardVO 사용자가 입력한 게시글 정보
	 * @return DB에 Insert한 개수
	 */
	public int createNewBoard(BoardVO boardVO);
	
	/**
	 * 파라미터로 전달받은 게시글 ID의 조회수를 1 증가
	 * @param id 조회할 게시글 ID (번호)
	 * @return DB 업데이트한 개수
	 */
	public int increaseViewCount(int id);
	
	/**
	 * 파라미터로 전달받은 게시글 ID의 게시글 정보 조회
	 * @param id 조회할 게시글의 ID (번호)
	 * @return 조회된 게시글의 정보(내용)
	 */
	public BoardVO getOneBoard(int id);
	
	/**
	 * DB에 게시글 정보 수정
	 * BoardVO의 ID값에 수정할 게시글의 ID값이 있어야 함
	 * @param boardVO 사용자가 수정한 게시글 정보
	 * @return DB에 Update한 게시글 수
	 */
	public int updateOneBoard(BoardVO boardVO);
	
	/**
	 * 파라미터로 전달받은 게시글 ID의 게시글을 삭제
	 * @param id 게시글 ID (번호)
	 * @return DB에 Delete한 게시글 수
	 */
	public int deleteOneBoard(int id);
}
