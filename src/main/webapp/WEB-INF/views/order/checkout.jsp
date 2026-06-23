<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="cart-page">
    <div class="section-head">
        <span class="section-badge">CHECKOUT</span>
        <h2>주문 / 결제</h2>
        <p>주문 정보를 확인하고 배송정보를 입력해.</p>
    </div>

    <div class="cart-panel">
        <table class="cart-table">
            <thead>
                <tr>
                    <th>도서명</th>
                    <th>단가</th>
                    <th>수량</th>
                    <th>합계</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="cart" items="${cartList}">
                    <tr>
                        <td>${cart.bookname}</td>
                        <td>${cart.price}원</td>
                        <td>${cart.amount}</td>
                        <td class="strong">${cart.totalPrice}원</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div class="checkout-bottom">
            <div class="shipping-box">
                <h3>배송 정보</h3>

                <form action="${pageContext.request.contextPath}/order/pay" method="post" class="shipping-form">
                    <div class="shipping-row">
                        <label for="receiver">받는 사람</label>
                        <input type="text"
						       id="receiver"
						       name="receiver"
						       class="shipping-input"
						       value="${member.defaultReceiver}"
						       placeholder="받는 사람을 입력해주세요"
						       required>
                    </div>

                    <div class="shipping-row">
                        <label for="phone">연락처</label>
                        <input type="text"
						       id="phone"
						       name="phone"
						       class="shipping-input"
						       value="${not empty member.defaultPhone ? member.defaultPhone : member.phone}"
						       placeholder="연락처를 입력해주세요"
						       required>
                    </div>

                    <div class="shipping-row">
                        <label for="address">배송 주소</label>
                        <input type="text"
						       id="address"
						       name="address"
						       class="shipping-input"
						       value="${member.defaultAddress}"
						       placeholder="배송 주소를 입력해주세요"
						       required>
                    </div>
                    
                    <c:choose>
					    <c:when test="${isMember}">
					        <div class="shipping-row">
					            <label>회원 등급</label>
					            <input type="text"
					                   class="shipping-input"
					                   value="${member.grade} / 적립률 ${mileageRate}%"
					                   readonly>
					        </div>
					
					        <div class="shipping-row">
					            <label>보유 마일리지</label>
					            <input type="text"
					                   class="shipping-input"
					                   value="${member.mileage} P"
					                   readonly>
					        </div>
					
					        <div class="shipping-row">
					            <label for="useMileage">사용할 마일리지</label>
					            <input type="number"
					                   id="useMileage"
					                   name="useMileage"
					                   class="shipping-input"
					                   value="0"
					                   min="0"
					                   max="${member.mileage}">
					        </div>
					    </c:when>
					
					    <c:otherwise>
					        <input type="hidden" name="useMileage" value="0">
					    </c:otherwise>
					</c:choose>

                    <div class="cart-actions">
                        <a href="${pageContext.request.contextPath}/cart/list" class="action-btn outline">장바구니로 돌아가기</a>
                        <button type="submit" class="action-btn dark">주문 완료</button>
                    </div>
                </form>
            </div>

            <div class="cart-summary checkout-summary">
			    <div class="summary-card">
			        <span>총 상품 금액</span>
			        <strong>${sumMoney}원</strong>
			    </div>
			
			    <c:if test="${isMember}">
			        <div class="summary-card">
			            <span>예상 적립 마일리지</span>
			            <strong>${sumMoney * mileageRate / 100} P</strong>
			        </div>
			    </c:if>
			</div>
        </div>
    </div>
</section>