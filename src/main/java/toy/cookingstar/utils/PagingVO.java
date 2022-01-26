package toy.cookingstar.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagingVO {

    // 현재 페이지 번호, 시작 페이지 번호, 끝 페이지 번호, 총 포스트 수, 페이지 당 포스트 수, 한 번에 노출할 페이지 수, 마지막 페이지, 쿼리에 사용할 변수(start, end)
    private int currentPageNo, startPageNo, endPageNo, totalPost, postsPerPage, countPages, lastPage, start, end;

    public PagingVO(int totalPost, int currentPageNo, int countPages, int postsPerPage) {
        setTotalPost(totalPost);
        setCurrentPageNo(currentPageNo);
        setCountPages(countPages);
        setPostsPerPage(postsPerPage);
        calcLastPage(getTotalPost(), getCountPages());
        calcStartEndPageNo(getCurrentPageNo(), getCountPages());
        calcStartEnd(getCurrentPageNo(), getPostsPerPage());
    }

    // 마지막 페이지 계산
    public void calcLastPage(int totalPost, int countPages) {
        setLastPage((int) Math.ceil((double) totalPost / (double) countPages));
    }

    // 시작 페이지 번호, 끝 페이지 번호 계산
    public void calcStartEndPageNo(int currentPageNo, int countPages) {
        setEndPageNo(((int) Math.ceil((double) currentPageNo / (double) countPages)) * countPages);
        if (getEndPageNo() > getLastPage()) {
            setEndPageNo(getLastPage());
        }
        setStartPageNo(getEndPageNo() - countPages + 1);
        if (getStartPageNo() < 1) {
            setStartPageNo(1);
        }
    }

    // 조회 쿼리에 사용할 start, end 변수 계산
    public void calcStartEnd(int currentPageNo, int postsPerPage) {
        setEnd(currentPageNo * postsPerPage);
        setStart(getEnd() - postsPerPage);
    }
}
