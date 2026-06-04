<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="header-inner">
    <div class="header-left">
        <a class="brand" href="${pageContext.request.contextPath}/book/list">BOOK FOREST</a>

        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/book/list">도서목록</a>

            <c:if test="${not empty sessionScope.loginUser and sessionScope.loginUser ne 'admin'}">
			    <a href="${pageContext.request.contextPath}/cart/list">장바구니</a>
			    <a href="${pageContext.request.contextPath}/order/list">주문내역</a>
			</c:if>

            <c:if test="${sessionScope.loginUser eq 'admin'}">
                <a href="${pageContext.request.contextPath}/admin/insertform">도서등록</a>
                <a href="${pageContext.request.contextPath}/admin/sales">판매통계</a>
                <a href="#">고객관리</a>
                <a href="${pageContext.request.contextPath}/chat/admin">실시간상담</a>
            </c:if>
        </nav>
    </div>

    <div class="header-right">
        <c:choose>
            <c:when test="${not empty sessionScope.loginUser}">
                <span class="welcome-text">${sessionScope.loginUser}님</span>
                <a class="header-btn outline" href="${pageContext.request.contextPath}/member/logout">로그아웃</a>
            </c:when>
            <c:otherwise>
                <a class="header-btn outline" href="${pageContext.request.contextPath}/member/login">로그인</a>
                <a class="header-btn solid" href="${pageContext.request.contextPath}/member/register">회원가입</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>