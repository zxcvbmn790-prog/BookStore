<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
    <c:when test="${not empty book}">
        <section class="detail-page">
            <div class="detail-card">
                <div class="detail-cover">
                    <c:choose>
                        <c:when test="${not empty book.image}">
                            <img src="${book.image}" alt="${book.bookname}">
                        </c:when>
                        <c:otherwise>
                            <div class="no-cover">NO IMAGE</div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="detail-info">
                    <span class="detail-badge">BOOK DETAIL</span>
                    <h1 class="detail-title">${book.bookname}</h1>
                    <div class="detail-author">${book.author} 저</div>

                    <div class="detail-meta">
                        <div class="meta-row">
                            <span class="meta-label">출판사</span>
                            <span class="meta-value">${book.publisher}</span>
                        </div>
                        <div class="meta-row">
                            <span class="meta-label">ISBN</span>
                            <span class="meta-value">${book.isbn}</span>
                        </div>
                    </div>

                    <div class="detail-price-box">
                        <span class="detail-price-label">판매가</span>
                        <div class="detail-price">
                            <c:choose>
                                <c:when test="${not empty book.price}">
                                    ${book.price}<span class="price-unit">원</span>
                                </c:when>
                                <c:otherwise>가격 미정</c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${sessionScope.loginUser eq 'admin'}">
                            <div class="detail-actions">
                                <a href="${pageContext.request.contextPath}/book/list" class="action-btn outline">목록으로</a>
                                <a href="${pageContext.request.contextPath}/admin/updateform?isbn=${book.isbn}" class="action-btn soft">도서 수정</a>
                                <a href="${pageContext.request.contextPath}/admin/delete?isbn=${book.isbn}"
                                   class="action-btn danger"
                                   onclick="return confirm('정말 이 책을 삭제하시겠습니까?');">도서 삭제</a>
                            </div>
                        </c:when>

                        <c:otherwise>
                            <form action="${pageContext.request.contextPath}/cart/insert" method="post" class="detail-form">
							    <input type="hidden" name="isbn" value="${book.isbn}">
							
							    <div class="qty-box">
							        <label for="amount">수량</label>
							        <input type="number" id="amount" name="amount" value="1" min="1">
							    </div>
							
							    <div class="detail-actions">
							        <a href="${pageContext.request.contextPath}/book/list" class="action-btn outline">목록으로</a>
							        <button type="submit" class="action-btn soft">장바구니 담기</button>
							    </div>
							</form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </section>
    </c:when>

    <c:otherwise>
        <div class="empty-box">
            <h3>해당 도서를 찾을 수 없습니다.</h3>
            <a href="${pageContext.request.contextPath}/book/list" class="action-btn dark">목록으로 이동</a>
        </div>
    </c:otherwise>
</c:choose>