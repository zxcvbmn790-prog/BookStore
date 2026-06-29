<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- 📡 토스 페이먼츠 V2 통신망 연결 --%>
<script src="https://js.tosspayments.com/v2/standard"></script>

<%-- 💥 [타격 지점] 고유 ID 부여 및 공통 레이아웃 맞춤형 컨테이너 --%>
<div id="cosmic-toss-checkout-container" class="container mt-5 mb-5">
    <div class="cosmic-card">
        <div class="card-body p-4">
            
            <%-- 토스 UI 인젝션 구역 --%>
            <div id="payment-method"></div>
            <div id="agreement"></div>

            <%-- 사령부 커스텀 쿠폰 구역 --%>
            <div class="coupon-section">
                <label class="coupon-label">
                    <input id="coupon-box" type="checkbox" class="cosmic-checkbox" />
                    <span class="coupon-text">🎫 5,000원 쿠폰 적용</span>
                </label>
            </div>

            <%-- 결제 승인 버튼 --%>
            <div class="text-center mt-4">
                <button id="payment-button" class="btn-cosmic-submit w-100 btn-lg">
                    결제하기
                </button>
            </div>
            
        </div>
    </div>
</div>

<script>
    // 🔥 서버에서 받은 데이터 바인딩
    const orderId = "${orderId}";
    const orderName = "${empty orderName ? '도서 결제' : orderName}";
    const amountValue = ${amount};
    const customerEmail = "${customerEmail}";
    const customerName = "${customerName}";
    const customerMobilePhone = "01012345678"; /* 통신망 기본값 */

    main();

    async function main() {
        const button = document.getElementById("payment-button");
        const coupon = document.getElementById("coupon-box");

        const amount = {
            currency: "KRW",
            value: amountValue
        };

        const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
        const tossPayments = TossPayments(clientKey);

        const widgets = tossPayments.widgets({
            customerKey: generateRandomString()
        });

        await widgets.setAmount(amount);

        await widgets.renderPaymentMethods({
            selector: "#payment-method",
            variantKey: "DEFAULT",
        });

        await widgets.renderAgreement({
            selector: "#agreement",
            variantKey: "AGREEMENT"
        });

        // 쿠폰 이벤트 리스너
        coupon.addEventListener("change", async function () {
            if (coupon.checked) {
                await widgets.setAmount({
                    currency: "KRW",
                    value: amount.value - 5000
                });
                return;
            }
            await widgets.setAmount(amount);
        });

        // 결제 버튼 이벤트 리스너
        button.addEventListener("click", async function () {
            await widgets.requestPayment({
                orderId: orderId,
                orderName: orderName,
                successUrl: window.location.origin + "${pageContext.request.contextPath}/order/success",
                failUrl: window.location.origin + "${pageContext.request.contextPath}/order/fail",
                customerEmail: customerEmail,
                customerName: customerName,
                customerMobilePhone: customerMobilePhone
            });
        });
    }

    function generateRandomString() {
        return window.btoa(Math.random()).slice(0, 20);
    }
</script>