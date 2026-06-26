<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>배송 현황 조회</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
<style>
    body { font-family: 'Malgun Gothic', sans-serif; background: #f9f9f9; padding: 20px;}
    /*.tracking-container { max-height: 800px; max-width: 800px; margin: 40px auto; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
    */
    /* 스크롤 추가 및 최대 높이를 500px로 변경 */
    .tracking-container { 
        overflow-y: auto;  /* 높이가 500px을 넘을 경우 내부 스크롤 바 생성 */
        max-width: 800px; 
        margin: 40px auto; 
        background: #fff; 
        padding: 30px; 
        border-radius: 8px; 
        box-shadow: 0 2px 10px rgba(0,0,0,0.05); 
    }
    .title { font-size: 20px; font-weight: bold; margin-bottom: 30px; color: #333; }
    
    /* 스텝바 전체 컨테이너 */
    .step-bar { display: flex; justify-content: space-between; align-items: center; position: relative; margin-top: 40px; }
    /* 연결 선 (배경) */
    .step-bar::before { content: ""; position: absolute; top: 35px; left: 0; width: 100%; height: 4px; background: #ddd; z-index: 1; }
    
    .step-item { position: relative; z-index: 2; text-align: center; flex: 1; }
    
    /* 동그라미 아이콘 기본 스타일 (비활성화) */
    .step-icon { width: 70px; height: 70px; border-radius: 50%; background: #fff; border: 2px solid #ccc; display: flex; justify-content: center; align-items: center; margin: 0 auto 15px auto; color: #999; font-weight: bold; font-size: 24px; transition: all 0.3s ease; }
    .step-text { font-size: 13px; color: #666; font-weight: 500; }
    .step-num { font-size: 11px; color: #999; display: block; margin-bottom: 4px; }

    /* 활성화된 상태 (파란색 하이라이트) 스타일 */
    .step-item.active .step-icon { background: #008bcc; border-color: #008bcc; color: #fff; box-shadow: 0 0 10px rgba(0,139,204,0.3); }
    .step-item.active .step-text { color: #008bcc; font-weight: bold; }
    .step-item.active .step-num { color: #008bcc; font-weight: bold; }
</style>
</head>
<body>

<div class="tracking-container">
    <div class="title">배송현황 (주문번호: ${order.orderId})</div>
    
    <c:set var="status" value="${order.trakingstatus}" />

    <div class="step-bar">
        <div class="step-item ${status eq '접수' || status eq '배송준비중' || status eq '배송중' || status eq '배송완료' ? 'active' : ''}">
            <div class="step-icon"><i class="fa-solid fa-cart-arrow-down"></i></div>
            <div class="step-text"><span class="step-num">STEP 1</span>상품접수</div>
        </div>
        
        <div class="step-item ${status eq '배송준비중' || status eq '배송중' || status eq '배송완료' ? 'active' : ''}">
            <div class="step-icon"><i class="fa-solid fa-cart-flatbed"></i></div>
            <div class="step-text"><span class="step-num">STEP 2</span>배송준비중</div>
        </div>
        
        <div class="step-item ${status eq '배송중' || status eq '배송완료' ? 'active' : ''}">
            <div class="step-icon"><i class="fa-solid fa-rocket"></i></div>
            <div class="step-text"><span class="step-num">STEP 3</span>상품이동중</div>
        </div>
        
        
        <div class="step-item ${status eq '배송완료' ? 'active' : ''}">
            <div class="step-icon"><i class="fa-solid fa-check"></i></div>
            <div class="step-text"><span class="step-num">STEP 4</span>배송완료</div>
        </div>
    </div>
    
    <c:if test="${status eq '배송취소'}">
        <div style="margin-top: 40px; text-align: center; color: #ff3b30; font-weight: bold; background-color: #fff5f5; padding: 15px; border-radius: 6px; border: 1px solid #ffcccc;">
            ⚠️ 본 주문은 배송이 취소되었습니다.
        </div>
    </c:if>
</div>

</body>
</html>