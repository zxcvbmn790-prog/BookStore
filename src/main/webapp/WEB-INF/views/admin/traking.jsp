<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="cart-page">
    <div class="section-head">
        <span class="section-badge">DELIVERY</span>
        <h2>배송 관리</h2>
        <p>고객들의 주문별 배송 상태를 조회하고 업데이트할 수 있어.</p>
    </div>

    <c:choose>
        <c:when test="${not empty deliveryList}">
            <div class="cart-panel">
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>주문번호</th>
                            <th>수령인/연락처</th>
                            <th>배송지</th>
                            <th>도서정보</th>
                            <th>수량</th>
                            <th>주문일</th>
                            <th>결제 상태</th>
                            <th>배송 상태 변경</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="delivery" items="${deliveryList}">
                            <tr>
                                <td>${delivery.orderId}</td>

                                <td>
                                    <div class="strong">${delivery.receiver}</div>
                                    <div class="cart-book-sub">${delivery.phone}</div>
                                </td>

                                <td class="order-address" style="text-align: left; max-width: 250px;">
                                    <div>${delivery.address}</div>
                                </td>

                                <td>
                                    <div class="cart-book-info">
                                        <c:choose>
                                            <c:when test="${not empty delivery.image}">
                                                <img src="${delivery.image}" alt="${delivery.bookname}" class="cart-book-img">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="cart-book-img no-cover">NO IMAGE</div>
                                            </c:otherwise>
                                        </c:choose>
                                        <div>
                                            <div class="cart-book-title">${delivery.bookname}</div>
                                            <div class="cart-book-sub">ISBN: ${delivery.isbn}</div>
                                        </div>
                                    </div>
                                </td>

                                <td>${delivery.amount}개</td>

                                <td>${delivery.orderDate}</td>

                                <td>${delivery.status}</td>

                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/updateTracking" method="post" style="display: flex; gap: 5px; justify-content: center; align-items: center;">
                                        <input type="hidden" name="orderId" value="${delivery.orderId}">
                                        
                                        <select name="trakingstatus" class="search-select" style="padding: 5px; border-radius: 4px; border: 1px solid #ccc;">
                                            <option value="배송준비중" ${delivery.trakingstatus eq '배송준비중' ? 'selected' : ''}>배송준비중</option>
                                            <option value="배송중" ${delivery.trakingstatus eq '배송중' ? 'selected' : ''}>배송중</option>
                                            <option value="배송완료" ${delivery.trakingstatus eq '배송완료' ? 'selected' : ''}>배송완료</option>
                                            <option value="배송취소" ${delivery.trakingstatus eq '배송취소' ? 'selected' : ''}>배송취소</option>
                                        </select>
                                        
                                        <button type="submit" class="action-btn dark" style="padding: 5px 10px; font-size: 12px; min-width: auto; margin: 0;">
                                            변경
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>

        <c:otherwise>
            <div class="empty-box">
                <h3>처리할 배송 내역이 없습니다.</h3>
                <p>새로운 고객 주문이 발생하면 여기에 표시될 거야.</p>
            </div>
        </c:otherwise>
    </c:choose>
</section>