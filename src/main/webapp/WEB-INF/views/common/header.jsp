<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="header-inner">
    <div class="header-left">
        <a class="brand" href="${pageContext.request.contextPath}/book/list">BOOK FOREST</a>

        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/book/list">도서목록</a>
            <a href="${pageContext.request.contextPath}/support/faq">자주 묻는 질문</a>

            <c:if test="${not empty sessionScope.loginUser and sessionScope.loginUser ne 'admin'}">
                <a href="${pageContext.request.contextPath}/cart/list">장바구니</a>
                <a href="${pageContext.request.contextPath}/order/list">주문내역</a>
            </c:if>

            <c:if test="${sessionScope.loginUser eq 'admin'}">
                <a href="${pageContext.request.contextPath}/admin/insertform">도서등록</a>
                <a href="${pageContext.request.contextPath}/admin/sales">판매통계</a>
                <a href="${pageContext.request.contextPath}/admin/members">고객관리</a>
                <a href="${pageContext.request.contextPath}/chat/admin">실시간상담</a>
            </c:if>
        </nav>
    </div>

    <div class="header-right">
        <c:choose>
            <c:when test="${not empty sessionScope.loginUser}">
                <button type="button" class="user-chip" onclick="toggleProfileDrawer(true)">${sessionScope.loginUser}님</button>
                <a class="header-btn outline" href="${pageContext.request.contextPath}/member/logout">로그아웃</a>
            </c:when>
            <c:otherwise>
                <a class="header-btn outline" href="${pageContext.request.contextPath}/member/login">로그인</a>
                <a class="header-btn solid" href="${pageContext.request.contextPath}/member/register">회원가입</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<c:if test="${not empty sessionScope.loginUser and sessionScope.loginUser ne 'admin'}">
    <div id="profileDrawerBackdrop" class="profile-drawer-backdrop" onclick="toggleProfileDrawer(false)"></div>
    <aside id="profileDrawer" class="profile-drawer">
        <div class="profile-drawer-top">
            <div>
                <div class="profile-drawer-label">MY MENU</div>
                <div class="profile-drawer-name">${sessionScope.loginUser}님</div>
                <div class="profile-drawer-sub">회원 정보와 계정 설정을 관리할 수 있어요.</div>
            </div>
            <button type="button" class="profile-close" onclick="toggleProfileDrawer(false)">&times;</button>
        </div>

        <div class="profile-drawer-menu">
            <a href="${pageContext.request.contextPath}/member/profile">정보 수정</a>
            <a href="${pageContext.request.contextPath}/member/password">비밀번호 수정</a>
            <a href="${pageContext.request.contextPath}/order/list">주문내역</a>
            <a href="${pageContext.request.contextPath}/cart/list">장바구니</a>
            <a href="${pageContext.request.contextPath}/member/delete">회원 탈퇴</a>
            <a href="${pageContext.request.contextPath}/member/logout">로그아웃</a>
        </div>
    </aside>

    <script>
        function toggleProfileDrawer(open) {
            var drawer = document.getElementById('profileDrawer');
            var backdrop = document.getElementById('profileDrawerBackdrop');
            if (!drawer || !backdrop) return;
            drawer.classList.toggle('open', open);
            backdrop.classList.toggle('show', open);
            document.body.classList.toggle('drawer-open', open);
        }
    </script>
</c:if>
