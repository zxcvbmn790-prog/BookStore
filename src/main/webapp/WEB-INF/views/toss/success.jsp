<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div id="cosmic-toss-success-container" class="container mt-5 mb-5">
    <div class="success-card">
        <%-- 🎉 축하 메시지 및 헤더 --%>
        <div class="text-center mb-4">
            <img width="100px" src="https://static.toss.im/illusts/check-blue-spot-ending-frame.png" alt="결제 완료" class="mb-3"/>
            <h2 class="form-main-title">결제를 완료했어요</h2>
            <p class="text-muted">결제가 정상적으로 완료되었습니다.</p>
        </div>

        <%-- 🧾 영수증 상세 정보 --%>
        <div class="receipt-box">
            <div class="receipt-row">
                <span class="receipt-label">결제금액</span>
                <span class="receipt-value text-primary-glow"><fmt:formatNumber value="${amount}" pattern="#,###"/> 원</span>
            </div>
            <div class="receipt-row">
                <span class="receipt-label">토스 주문번호</span>
                <span class="receipt-value">${orderId}</span>
            </div>
            <div class="receipt-row">
                <span class="receipt-label">paymentKey</span>
                <span class="receipt-value small-key">${paymentKey}</span>
            </div>
        </div>

        <%-- 🎛️ 액션 버튼 그룹 --%>
        <div class="action-buttons-group mt-4">
            <%-- 토스 API 기본 버튼 (보조 역할) --%>
            <div class="d-flex gap-2 mb-3">
                <button class="btn btn-outline-secondary w-50" onclick="location.href='https://docs.tosspayments.com/guides/v2/payment-widget/integration';">
                    연동 문서
                </button>
                <button class="btn btn-outline-info w-50" onclick="location.href='https://discord.gg/A4fRFXQhRu';">
                    실시간 문의
                </button>
            </div>
            
            <%-- 💥 사령부 메인 액션 버튼 --%>
            <c:choose>
                <c:when test="${not empty sessionScope.loginMember}">
                    <button class="btn btn-cosmic-submit w-100 btn-lg" onclick="location.href='${pageContext.request.contextPath}/'">
                        🚀 메인 화면으로 돌아가기
                    </button>
                </c:when>
                <c:otherwise>
                    <button class="btn btn-cosmic-submit w-100 btn-lg" onclick="location.href='${pageContext.request.contextPath}/cookie/purchase/success'">
                        🎫 비회원 주문번호 확인하기
                    </button>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<%-- 📡 결제 최종 승인 비동기 통신망 --%>
<script>
document.addEventListener("DOMContentLoaded", async function() {
    try {
        const response = await fetch("${pageContext.request.contextPath}/order/confirm", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                paymentKey: "${paymentKey}",
                orderId: "${orderId}",
                amount: ${amount}
            })
        });

        const result = await response.json();
        console.log("결제 승인 결과:", result);

        if (!response.ok) {
            alert("🚨 결제 승인 실패: " + (result.message || "원인 불명"));
            console.error(result);
        }
    } catch(e) {
        console.error("confirm API 통신 두절", e);
    }
});
</script>