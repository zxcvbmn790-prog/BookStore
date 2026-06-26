<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

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

                <form action="${pageContext.request.contextPath}/order/pay" method="post" class="shipping-form" id="orderForm">
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
                        <label for="zonecode">배송 주소</label>
                        <div class="address-group">
                            <!-- 우편번호 (name 없음 - 서버로 안 보냄) -->
                            <input type="text" id="zonecode" class="shipping-input" placeholder="우편번호" readonly>
                            <button type="button" class="action-btn outline" onclick="execDaumPostcode()">우편번호 찾기</button>
                        </div>

                        <!-- 도로명주소 (name 없음 - 서버로 안 보냄) -->
                        <input type="text" id="roadAddress" class="shipping-input" placeholder="도로명 주소" readonly>

                        <!-- 상세주소 (직접입력, name 없음) -->
                        <input type="text" id="detailAddress" class="shipping-input" placeholder="상세주소">

                        <!-- 실제로 서버에 전송되는 필드: 우편번호+도로명+상세주소를 합친 문자열 -->
                        <input type="hidden" id="address" name="address">
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

                    <div class="checkout-summary-inline">
                        <div class="summary-card">
                            <span>총 상품 금액</span>
                            <strong>${sumMoney}원</strong>
                        </div>
                        <c:if test="${isMember}">
                            <div class="summary-card">
                                <span>예상 적립 마일리지</span>
                                <strong><fmt:formatNumber value="${sumMoney * mileageRate / 100}" pattern="#" /> P</strong>
                            </div>
                        </c:if>
                    </div>

                    <div class="cart-actions">
                        <a href="${pageContext.request.contextPath}/cart/list" class="action-btn outline">장바구니로 돌아가기</a>
                        <button type="submit" class="action-btn dark">주문 완료</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>

<!-- 카카오 우편번호 서비스 -->
<script src="//t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script>
function execDaumPostcode() {
    new kakao.Postcode({
        oncomplete: function(data) {
            var roadAddr = data.roadAddress;
            var jibunAddr = data.jibunAddress;
            var fullAddr = roadAddr ? roadAddr : jibunAddr;

            var extraAddr = '';
            if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                extraAddr += data.bname;
            }
            if (data.buildingName !== '' && data.apartment === 'Y') {
                extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
            }
            if (extraAddr !== '') {
                fullAddr += ' (' + extraAddr + ')';
            }

            document.getElementById('zonecode').value = data.zonecode;
            document.getElementById('roadAddress').value = fullAddr;
            document.getElementById('detailAddress').focus();

            updateFullAddress();
        }
    }).open();
}

function updateFullAddress() {
    var zonecode = document.getElementById('zonecode').value;
    var roadAddress = document.getElementById('roadAddress').value;
    var detailAddress = document.getElementById('detailAddress').value;

    document.getElementById('address').value =
        '[' + zonecode + '] ' + roadAddress + ' ' + detailAddress;
}

document.getElementById('detailAddress').addEventListener('input', updateFullAddress);

// 폼 제출 전 주소 검증
document.getElementById('orderForm').addEventListener('submit', function(e) {
    if (!document.getElementById('address').value.trim()) {
        alert('주소를 입력해주세요.');
        e.preventDefault();
    }
});
</script>