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

                                <td>
                                <c:choose>
                                    <c:when test="${cart.discountRate > 0}">
                                        <div style="text-decoration:line-through; color:#999; font-size:12px;">${cart.originalPrice}원</div>
                                        <div><span style="background:#d9534f; color:#fff; font-size:11px; font-weight:700; padding:1px 5px; border-radius:3px; margin-right:3px;">${cart.discountRate}%</span>${cart.price}원</div>
                                    </c:when>
                                    <c:otherwise>${cart.price}원</c:otherwise>
                                </c:choose>
                            </td>

                                <td>
                                    <input type="number" value="${cart.amount}" min="1" class="qty-input"
                                           data-isbn="${cart.isbn}"
                                           data-price="${cart.price}"
                                           onchange="updateQty(this)">
                                </td>

                                <td class="strong cart-subtotal" data-isbn="${cart.isbn}">${cart.totalPrice}원</td>

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
                    <a href="${pageContext.request.contextPath}/order/checkout2" class="action-btn dark">결제하기</a> 
                    <%-- <a href="${pageContext.request.contextPath}/order/confirm" class="action-btn dark">결제하기</a> --%>
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

<script>
function updateQty(input) {
    var amount = parseInt(input.value) || 1;
    if (amount < 1) { amount = 1; input.value = 1; }

    var isbn = input.getAttribute('data-isbn');
    var unitPrice = parseInt(input.getAttribute('data-price').replace(/[^0-9]/g, '')) || 0;

    var subtotalCell = document.querySelector('.cart-subtotal[data-isbn="' + isbn + '"]');
    subtotalCell.textContent = (unitPrice * amount).toLocaleString() + '원';

    var allSubtotals = document.querySelectorAll('.cart-subtotal');
    var total = 0;
    for (var i = 0; i < allSubtotals.length; i++) {
        total += parseInt(allSubtotals[i].textContent.replace(/[^0-9]/g, '')) || 0;
    }
    var summaryEl = document.querySelector('.summary-card strong');
    if (summaryEl) summaryEl.textContent = total.toLocaleString() + '원';

    fetch('${pageContext.request.contextPath}/cart/update', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'isbn=' + isbn + '&amount=' + amount
    });
}
</script>