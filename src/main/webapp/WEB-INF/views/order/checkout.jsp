<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<section class="cart-page">
    <div class="section-head">
        <span class="section-badge">CHECKOUT</span>
        <h2>주문 / 결제</h2>
        <p>주문 정보를 확인하고 배송정보를 입력해주세요.</p>
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

                <form id="orderForm" class="shipping-form">
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
                            <input type="text" id="zonecode" class="shipping-input" placeholder="우편번호" readonly>
                            <button type="button" class="action-btn outline" onclick="execDaumPostcode()">우편번호 찾기</button>
                        </div>
                        <input type="text" id="roadAddress" class="shipping-input" placeholder="도로명 주소" readonly>
                        <input type="text" id="detailAddress" class="shipping-input" placeholder="상세주소">
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
					        <input type="hidden" id="useMileage" name="useMileage" value="0">
					    </c:otherwise>
					</c:choose>

                    <div class="checkout-summary-inline">
                        <div class="summary-card">
                            <span>총 상품 금액</span>
                            <strong id="totalAmount">${sumMoney}원</strong>
                        </div>
                        <c:if test="${isMember}">
                            <div class="summary-card">
                                <span>예상 적립 마일리지</span>
                                <strong><fmt:formatNumber value="${sumMoney * mileageRate / 100}" pattern="#" /> P</strong>
                            </div>
                        </c:if>
                    </div>

                    <input type="hidden" id="rawSumMoney" value="${sumMoney}">

                    <div class="cart-actions">
                        <a href="${pageContext.request.contextPath}/cart/list" class="action-btn outline">장바구니로 돌아가기</a>
                        <button type="submit" class="action-btn dark">주문 완료</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>

<script src="//t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="https://js.tosspayments.com/v1/payment"></script>

<script>
// 토스 클라이언트 키 초기화 (테스트용)
//const clientKey = 'test_ck_D5b3M7QHXSMwLNQGg7Vo3W4kvR2K';
const clientKey = 'test_ck_Gv6LjeKD8aXOdnl1lpYkrwYxAdXy';
const tossPayments = TossPayments(clientKey);

// 마일리지 입력 시 실시간 화면 금액 반영 (선택)
const useMileageInput = document.getElementById('useMileage');
if(useMileageInput) {
    useMileageInput.addEventListener('input', function() {
        const rawMoney = parseInt(document.getElementById('rawSumMoney').value);
        let mileage = parseInt(this.value) || 0;
        if(mileage > parseInt(this.max)) mileage = this.max;
        
        const finalAmount = rawMoney - mileage;
        document.getElementById('totalAmount').innerText = (finalAmount < 0 ? 0 : finalAmount) + '원';
    });
}

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

// 🔥 주문 완료 제출 시 토스결제창 연결 흐름 컨트롤
document.getElementById('orderForm').addEventListener('submit', function(e) {
    e.preventDefault(); // 기본 폼 전송 중단

    const addressVal = document.getElementById('address').value.trim();
    if (!addressVal) {
        alert('주소를 입력해주세요.');
        return;
    }

    // 1. 서버에 가주문(배송지 정보 저장 및 orderId 생성) 생성 요청
    const formData = {
        receiver: document.getElementById('receiver').value,
        phone: document.getElementById('phone').value,
        address: addressVal,
        useMileage: document.getElementById('useMileage') ? parseInt(document.getElementById('useMileage').value) || 0 : 0
    };

    fetch('${pageContext.request.contextPath}/order/prepare', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    })
    .then(res => {
        if (!res.ok) throw new Error('주문 정보 생성에 실패했습니다.');
        return res.json();
    })
    .then(data => {
        // 2. 가주문 생성이 성공하면 받은 orderId와 금액으로 토스 결제창 호출
        if(data.success) {
            tossPayments.requestPayment('카드', {
                amount: data.amount,
                orderId: data.orderId,
                orderName: data.orderName,
                successUrl: window.location.origin + '${pageContext.request.contextPath}/order/success',
                failUrl: window.location.origin + '${pageContext.request.contextPath}/order/fail'
            })
            .catch(function (error) {
                if (error.code === 'USER_CANCEL') {
                    alert('결제를 취소하셨습니다.');
                } else {
                    alert(error.message);
                }
            });
        } else {
            alert(data.message || '주문 준비 실패');
        }
    })
    .catch(err => {
        alert(err.message);
    });
});
</script>