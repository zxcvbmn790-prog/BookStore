<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="cart-page">
    <div class="section-head">
        <span class="section-badge">CART</span>
        <h2>장바구니</h2>
        <p>담아둔 도서를 확인하고 수량을 조정할 수 있어.</p>
    </div>

    <c:choose>
        <c:when test="${not empty cartList}">
            <div class="cart-panel">
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>도서 정보</th>
                            <th>단가</th>
                            <th>수량</th>
                            <th>합계</th>
                            <th>삭제</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cart" items="${cartList}">
                            <tr>
                                <td>
                                    <div class="cart-book-info">
                                        <img src="${cart.image}" class="cart-book-img" alt="${cart.bookname}">
                                        <div>
                                            <div class="cart-book-title">${cart.bookname}</div>
                                            <div class="cart-book-sub">ISBN ${cart.isbn}</div>
                                        </div>
                                    </div>
                                </td>

                                <td>${cart.price}원</td>

                                <td>
                                    <form action="${pageContext.request.contextPath}/cart/update" method="post" class="qty-update-form">
                                        <input type="hidden" name="isbn" value="${cart.isbn}">
                                        <input type="number" name="amount" value="${cart.amount}" min="1" class="qty-input">
                                        <button type="submit" class="small-btn">변경</button>
                                    </form>
                                </td>

                                <td class="strong">${cart.totalPrice}원</td>

                                <td>
                                    <form action="${pageContext.request.contextPath}/cart/delete" method="post">
                                        <input type="hidden" name="isbn" value="${cart.isbn}">
                                        <button type="submit" class="small-btn danger">삭제</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <div class="cart-summary">
                    <div class="summary-card">
                        <span>총 결제 금액</span>
                        <strong>${sumMoney}원</strong>
                    </div>
                </div>

                <div class="cart-actions">
                    <a href="${pageContext.request.contextPath}/book/list" class="action-btn outline">쇼핑 계속하기</a>
                    <a href="${pageContext.request.contextPath}/order/checkout" class="action-btn dark">결제하기</a>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="empty-box">
                <h3>장바구니가 비어 있습니다.</h3>
                <p>관심 있는 도서를 먼저 담아보자.</p>
                <a href="${pageContext.request.contextPath}/book/list" class="action-btn dark">도서 담으러 가기</a>
            </div>
        </c:otherwise>
    </c:choose>
</section>