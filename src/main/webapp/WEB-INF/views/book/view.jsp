<!DOCTYPE style PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
.detail-shell{max-width:1220px;margin:0 auto}.detail-page-v2{display:grid;grid-template-columns:420px 1fr;gap:34px;background:#fff;border:1px solid #e8ebf2;border-radius:30px;padding:32px;box-shadow:0 12px 40px rgba(15,23,42,.06)}.detail-cover-v2{background:#f6f7fb;border-radius:26px;padding:18px;display:flex;align-items:center;justify-content:center;min-height:620px}.detail-cover-v2 img{max-width:100%;max-height:580px;object-fit:contain;border-radius:18px;box-shadow:0 12px 28px rgba(0,0,0,.12)}.detail-top-label{font-size:13px;font-weight:800;letter-spacing:.12em;color:#6366f1;margin-bottom:16px;display:block}.detail-title-v2{font-size:30px;line-height:1.35;font-weight:800;letter-spacing:-.03em;margin:0 0 12px;color:#111827}.detail-sub-v2{font-size:15px;color:#667085;margin-bottom:22px}.detail-stat-row{display:flex;gap:12px;flex-wrap:wrap;margin-bottom:22px}.detail-stat{padding:12px 16px;border-radius:14px;border:1px solid #e5e7eb;background:#fafbff;font-size:15px;color:#111827}.detail-stat em{font-style:normal;color:#6b7280;margin-right:6px}.detail-grid{display:grid;gap:12px;margin-bottom:18px}.detail-grid .meta-row{display:grid;grid-template-columns:110px 1fr;align-items:center;background:#f4f6fb;border-radius:16px;padding:16px 18px}.detail-grid .meta-label{font-weight:700;color:#111827}.detail-grid .meta-value{color:#667085;word-break:break-all}.price-panel{background:#eef2ff;border-radius:20px;padding:20px 22px;margin:24px 0 22px}.price-panel .caption{font-size:14px;color:#6b7280;margin-bottom:8px}.price-panel .price{font-size:42px;font-weight:900;color:#3730a3;letter-spacing:-.03em}.price-panel .price span{font-size:20px;font-weight:700;margin-left:4px}.action-row,.purchase-row{display:flex;gap:12px;flex-wrap:wrap;align-items:center}.qty-box-v2{display:flex;align-items:center;gap:12px;margin:10px 0 18px}.qty-box-v2 label{font-weight:700;color:#111827}.qty-box-v2 input{width:110px;height:46px;border:1px solid #d7dce5;border-radius:14px;padding:0 16px;background:#fff}.action-btn-v2{height:52px;padding:0 24px;border-radius:16px;border:1px solid #d7dce5;background:#fff;color:#111827;font-weight:700;cursor:pointer;display:inline-flex;align-items:center;justify-content:center}.action-btn-v2.primary{background:#c7d2fe;border-color:#c7d2fe;color:#312e81}.action-btn-v2.dark{background:#111827;border-color:#111827;color:#fff}.action-btn-v2.danger{background:#fee2e2;border-color:#fecaca;color:#b91c1c}.feedback-tools{display:flex;align-items:center;gap:14px;flex-wrap:wrap;margin-bottom:22px}.heart-btn{width:54px;height:54px;border-radius:18px;border:1px solid #e5e7eb;background:#fff;font-size:26px;cursor:pointer;display:flex;align-items:center;justify-content:center;transition:.2s}.heart-btn.liked{color:#ef4444;background:#fff1f2;border-color:#fecdd3}.rating-wrap{display:flex;align-items:center;gap:10px;flex-wrap:wrap;background:#fafbff;border:1px solid #e5e7eb;border-radius:18px;padding:12px 16px}.star-picker{display:flex;flex-direction:row-reverse;gap:4px}.star-picker input{display:none}.star-picker label{font-size:28px;color:#d1d5db;cursor:pointer;line-height:1}.star-picker input:checked ~ label,.star-picker label:hover,.star-picker label:hover ~ label{color:#f59e0b}.rating-text{font-size:14px;color:#6b7280}.feedback-message{margin-bottom:18px;padding:14px 16px;border-radius:14px;background:#eef2ff;color:#3730a3;border:1px solid #c7d2fe}.empty-box{background:#fff;border:1px solid #e5e7eb;border-radius:24px;padding:48px 32px;text-align:center}
</style>

<c:choose>
    <c:when test="${not empty book}">
        <section class="detail-shell">
            <div class="detail-page-v2">
                <div class="detail-cover-v2">
                    <c:choose>
                        <c:when test="${not empty book.image}"><img src="${book.image}" alt="${book.bookname}"></c:when>
                        <c:otherwise><div class="no-cover">표지 준비중</div></c:otherwise>
                    </c:choose>
                </div>
                <div>
                    <span class="detail-top-label">BOOK DETAIL</span>
                    <h1 class="detail-title-v2">${book.bookname}</h1>
                    <div class="detail-sub-v2">${book.author} 저</div>

                    <c:if test="${not empty feedbackMessage}"><div class="feedback-message">${feedbackMessage}</div></c:if>

                    <div class="detail-stat-row">
                        <div class="detail-stat"><em>좋아요</em> ${feedback.likeCount}</div>
                        <div class="detail-stat"><em>평점</em> <fmt:formatNumber value="${feedback.averageRating}" pattern="0.0"/> / 5.0 (${feedback.ratingCount}명)</div>
                    </div>

                    <div class="detail-grid">
                        <div class="meta-row"><span class="meta-label">출판사</span><span class="meta-value">${book.publisher}</span></div>
                        <div class="meta-row"><span class="meta-label">ISBN</span><span class="meta-value">${book.isbn}</span></div>
                    </div>

                    <div class="price-panel">
                        <c:choose>
                            <c:when test="${book.discountRate > 0}">
                                <div class="caption">정가</div>
                                <div style="text-decoration:line-through; color:#9ca3af; font-size:20px; font-weight:600; margin-bottom:6px;">
                                    <fmt:parseNumber var="origPrice" value="${book.price}" type="number"/><fmt:formatNumber value="${origPrice}" type="number"/>원
                                </div>
                                <div class="caption">할인가 <span style="background:#ef4444;color:#fff;padding:2px 8px;border-radius:4px;font-weight:700;font-size:13px;margin-left:6px;">${book.discountRate}% OFF</span></div>
                                <div class="price"><fmt:formatNumber value="${book.discountedPrice}" type="number"/><span>원</span></div>
                            </c:when>
                            <c:otherwise>
                                <div class="caption">판매가</div><div class="price">${book.price}<span>원</span></div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <c:if test="${not empty sessionScope.loginUser and sessionScope.loginUser ne 'admin'}">
                        <div class="feedback-tools">
                            <form action="${pageContext.request.contextPath}/book/like" method="post" style="margin:0;">
                                <input type="hidden" name="isbn" value="${book.isbn}">
                                <button type="submit" class="heart-btn ${feedback.liked ? 'liked' : ''}" title="좋아요">${feedback.liked ? '♥' : '♡'}</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/book/rate" method="post" class="rating-wrap" style="margin:0;">
                                <input type="hidden" name="isbn" value="${book.isbn}">
                                <div class="star-picker">
                                    <input type="radio" id="star5" name="rating" value="5" ${feedback.myRating == 5 ? 'checked' : ''}><label for="star5">★</label>
                                    <input type="radio" id="star4" name="rating" value="4" ${feedback.myRating == 4 ? 'checked' : ''}><label for="star4">★</label>
                                    <input type="radio" id="star3" name="rating" value="3" ${feedback.myRating == 3 ? 'checked' : ''}><label for="star3">★</label>
                                    <input type="radio" id="star2" name="rating" value="2" ${feedback.myRating == 2 ? 'checked' : ''}><label for="star2">★</label>
                                    <input type="radio" id="star1" name="rating" value="1" ${feedback.myRating == 1 ? 'checked' : ''}><label for="star1">★</label>
                                </div>
                                <div class="rating-text">${empty feedback.myRating ? '별점을 선택해주세요' : '내 별점 '}${feedback.myRating}</div>
                                <button type="submit" class="action-btn-v2 primary">별점 저장</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/book/rate/cancel" method="post" style="margin:0;">
                                <input type="hidden" name="isbn" value="${book.isbn}">
                                <button type="submit" class="action-btn-v2">별점 취소</button>
                            </form>
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${sessionScope.loginUser eq 'admin'}">
                            <div class="purchase-row">
                                <a href="${pageContext.request.contextPath}/book/list" class="action-btn-v2">목록으로</a>
                                <a href="${pageContext.request.contextPath}/admin/updateform?isbn=${book.isbn}" class="action-btn-v2 primary">도서 수정</a>
                                <a href="${pageContext.request.contextPath}/admin/delete?isbn=${book.isbn}" class="action-btn-v2 danger" onclick="return confirm('정말 이 책을 삭제하시겠습니까?');">도서 삭제</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <form action="${pageContext.request.contextPath}/cart/insert" method="post">
                                <input type="hidden" name="isbn" value="${book.isbn}">
                                <div class="qty-box-v2"><label for="amount">수량</label><input type="number" id="amount" name="amount" value="1" min="1"></div>
                                <div class="purchase-row"><a href="${pageContext.request.contextPath}/book/list" class="action-btn-v2">목록으로</a><button type="submit" class="action-btn-v2 primary">장바구니 담기</button></div>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </section>
    </c:when>
    <c:otherwise>
        <div class="empty-box"><h3>해당 도서를 찾을 수 없습니다.</h3><a href="${pageContext.request.contextPath}/book/list" class="action-btn-v2 dark">목록으로 이동</a></div>
    </c:otherwise>
</c:choose>
