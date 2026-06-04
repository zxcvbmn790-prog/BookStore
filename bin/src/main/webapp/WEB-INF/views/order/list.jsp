<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="cart-page">
    <div class="section-head">
        <span class="section-badge">ORDER</span>
        <h2>주문내역</h2>
        <p>내가 주문한 도서와 배송 정보를 확인할 수 있어.</p>
    </div>

    <c:choose>
        <c:when test="${not empty orderList}">
            <div class="cart-panel">
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>주문번호</th>
                            <th>도서정보</th>
                            <th>배송지</th>
                            <th>단가</th>
                            <th>수량</th>
                            <th>합계</th>
                            <th>상태</th>
                            <th>주문일</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${orderList}">
                            <tr>
                                <td>${order.orderId}</td>

                                <td>
                                    <div class="cart-book-info">
                                        <c:choose>
                                            <c:when test="${not empty order.image}">
                                                <img src="${order.image}" alt="${order.bookname}" class="cart-book-img">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="cart-book-img no-cover">NO IMAGE</div>
                                            </c:otherwise>
                                        </c:choose>

                                        <div>
                                            <div class="cart-book-title">
                                                <a href="${pageContext.request.contextPath}/book/view?isbn=${order.isbn}" class="book-title-link">
                                                    ${order.bookname}
                                                </a>
                                            </div>
                                            <div class="cart-book-sub">ISBN : ${order.isbn}</div>
                                        </div>
                                    </div>
                                </td>

                                <td class="order-address">
                                    <div>${order.address}</div>
                                    <div class="cart-book-sub">${order.receiver} / ${order.phone}</div>
                                </td>

                                <td>${order.price}원</td>
                                <td>${order.amount}</td>
                                <td class="strong">${order.totalPrice}원</td>
                                <td>${order.status}</td>
                                <td>${order.orderDate}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>

        <c:otherwise>
            <div class="empty-box">
                <h3>주문내역이 없습니다.</h3>
                <p>도서를 구매하면 여기에서 확인할 수 있어.</p>
                <a href="${pageContext.request.contextPath}/book/list" class="action-btn dark">도서 보러가기</a>
            </div>
        </c:otherwise>
    </c:choose>
</section>