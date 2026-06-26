<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
.order-table { width: 100%; border-collapse: collapse; table-layout: fixed; }
.order-table th,
.order-table td { border-bottom: 1px solid var(--line); padding: 16px 10px; text-align: center; vertical-align: middle; font-size: 14px; }
.order-table th { color: var(--sub); font-weight: 700; background: #fafbfc; white-space: nowrap; }

.order-table colgroup .col-no { width: 60px; }
.order-table colgroup .col-book { width: 30%; }
.order-table colgroup .col-qty { width: 50px; }
.order-table colgroup .col-pay { width: 100px; }
.order-table colgroup .col-status { width: 80px; }
.order-table colgroup .col-tracking { width: 90px; }
.order-table colgroup .col-date { width: 100px; }
.order-table colgroup .col-mileage { width: 80px; }

.order-book-info { display: flex; align-items: center; gap: 12px; text-align: left; }
.order-book-img { width: 50px; height: 68px; object-fit: cover; border-radius: 6px; background: #f8fafc; flex-shrink: 0; }
.order-book-title { font-weight: 600; max-width: 180px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-bottom: 3px; }
.order-book-title a { color: inherit; text-decoration: none; }
.order-book-title a:hover { color: #008bcc; }
.order-book-sub { font-size: 12px; color: #999; }
.order-address { text-align: left; font-size: 13px; }

.order-tracking-btn { display: inline-block; padding: 4px 10px; background: #e8f4fa; color: #008bcc; border-radius: 20px; font-size: 12px; font-weight: 700; text-decoration: none; cursor: pointer; border: 1px solid #c4e3f3; }
.order-tracking-btn:hover { background: #008bcc; color: #fff; }

.order-detail-row { background: #f9fafb; }
.order-detail-row td { padding: 10px; border-bottom: 2px solid var(--line); }
.order-detail-inner { display: flex; justify-content: center; gap: 32px; font-size: 13px; color: #666; }
.order-detail-inner span { white-space: nowrap; }
.order-detail-inner .label { color: #999; margin-right: 4px; }
.order-detail-inner .value { font-weight: 600; color: #333; }
</style>

<section class="cart-page">
    <div class="section-head">
        <span class="section-badge">ORDER</span>
        <h2>주문내역</h2>
        <p>내가 주문한 도서와 배송 정보를 확인할 수 있어.</p>
    </div>

    <c:choose>
        <c:when test="${not empty orderList}">
            <div class="cart-panel">
                <table class="order-table">
                    <colgroup>
                        <col class="col-no">
                        <col class="col-book">
                        <col>
                        <col class="col-qty">
                        <col class="col-pay">
                        <col class="col-status">
                        <col class="col-tracking">
                        <col class="col-date">
                    </colgroup>
                    <thead>
                        <tr>
                            <th>No</th>
                            <th>도서정보</th>
                            <th>배송지</th>
                            <th>수량</th>
                            <th>결제금액</th>
                            <th>상태</th>
                            <th>배송</th>
                            <th>주문일</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${orderList}">
                            <tr>
                                <td>${order.orderId}</td>

                                <td>
                                    <div class="order-book-info">
                                        <c:choose>
                                            <c:when test="${not empty order.image}">
                                                <img src="${order.image}" alt="${order.bookname}" class="order-book-img">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="order-book-img no-cover" style="display:flex;align-items:center;justify-content:center;font-size:10px;color:#aaa;">NO IMG</div>
                                            </c:otherwise>
                                        </c:choose>

                                        <div style="min-width:0;">
                                            <div class="order-book-title" title="${order.bookname}">
                                                <a href="${pageContext.request.contextPath}/book/view?isbn=${order.isbn}">
                                                    ${order.bookname}
                                                </a>
                                            </div>
                                            <div class="order-book-sub">ISBN ${order.isbn}</div>
                                        </div>
                                    </div>
                                </td>

                                <td class="order-address">
                                    <div>${order.receiver} / ${order.phone}</div>
                                    <div class="order-book-sub">${order.address}</div>
                                </td>

                                <td>${order.amount}</td>
                                <td class="strong">${order.finalPayment > 0 ? order.finalPayment : order.totalPrice}원</td>
                                <td>${order.status}</td>

                                <td>
                                    <a class="order-tracking-btn" href="#" onclick="openTracking('${order.orderId}'); return false;">
                                        ${order.trakingstatus}
                                    </a>
                                </td>

                                <td><fmt:formatDate value="${order.orderDate}" pattern="yy.MM.dd"/></td>
                            </tr>
                            <tr class="order-detail-row">
                                <td colspan="8">
                                    <div class="order-detail-inner">
                                        <span><span class="label">단가</span><span class="value">${order.price}원</span></span>
                                        <span><span class="label">합계</span><span class="value">${order.totalPrice}원</span></span>
                                        <span><span class="label">사용 마일리지</span><span class="value">${order.usedMileage}P</span></span>
                                        <span><span class="label">적립 마일리지</span><span class="value">${order.earnedMileage}P</span></span>
                                    </div>
                                </td>
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

<script>
function openTracking(orderId) {
    var url = "${pageContext.request.contextPath}/order/trackingDetail?orderId=" + orderId;
    window.open(url, "배송현황", "width=850,height=500,top=100,left=200,location=no,status=no,scrollbars=no");
}
</script>