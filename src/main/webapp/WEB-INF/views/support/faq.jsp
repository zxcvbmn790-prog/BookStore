<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="stats-page">
    <div class="section-head">
        <span class="section-badge">FAQ</span>
        <h2>자주 묻는 질문</h2>
        <p>회원가입, 주문, 평점, 상담 관련 질문을 빠르게 확인할 수 있어요.</p>
    </div>

    <div class="stats-panel">
        <c:forEach var="faq" items="${faqList}" varStatus="status">
            <div style="padding:22px 0; border-bottom:${status.last ? '0' : '1px solid #e5e7eb'};">
                <h3 style="margin:0 0 10px; font-size:20px;">Q. ${faq.question}</h3>
                <p style="margin:0; color:#555; line-height:1.7;">A. ${faq.answer}</p>
            </div>
        </c:forEach>
    </div>
</section>
