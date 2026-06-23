<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="cart-page">
    <div class="section-head">
        <span class="section-badge">ADMIN</span>
        <h2>배송 관리</h2>
        <p>전체 주문의 배송 상태를 관리할 수 있습니다.</p>
    </div>

    <c:if test="${not empty message}">
        <div class="empty-box">
            <p>${message}</p>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty orderList}">
            <div class="cart-panel">
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>주문번호</th>
                            <th>주문자</th>
                            <th>도서정보</th>
                            <th>배송지</th>
                            <th>수량</th>
                            <th>합계</th>
                            <th>현재상태</th>
                            <th>상태변경</th>
                            <th>주문일</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${orderList}">
                            <tr>
                                <td>${order.orderId}</td>
                                <td>${order.userid}</td>

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
                                            <div class="cart-book-title">${order.bookname}</div>
                                            <div class="cart-book-sub">ISBN : ${order.isbn}</div>
                                        </div>
                                    </div>
                                </td>

                                <td class="order-address">
                                    <div>${order.address}</div>
                                    <div class="cart-book-sub">${order.receiver} / ${order.phone}</div>
                                </td>

                                <td>${order.amount}</td>
                                <td class="strong">${order.totalPrice}원</td>
                                <td>${order.status}</td>

                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/order/status" method="post">
                                        <input type="hidden" name="orderId" value="${order.orderId}">

                                        <select name="status">
                                            <option value="배송전" ${order.status == '배송전' ? 'selected' : ''}>배송전</option>
                                            <option value="배송중" ${order.status == '배송중' ? 'selected' : ''}>배송중</option>
                                            <option value="배송완료" ${order.status == '배송완료' ? 'selected' : ''}>배송완료</option>
                                        </select>

                                        <button type="submit" class="action-btn dark">변경</button>
                                    </form>
                                </td>

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
                <p>아직 등록된 주문이 없습니다.</p>
            </div>
        </c:otherwise>
    </c:choose>
</section>