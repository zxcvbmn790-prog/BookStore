<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="admin-member-page">
    <div class="admin-page-head">
        <div>
            <span class="section-badge">CUSTOMERS</span>
            <h2>고객관리</h2>
            <p>가입된 회원 정보를 확인하고 탈퇴 처리할 수 있습니다.</p>
        </div>
    </div>

    <c:if test="${not empty message}">
        <div class="auth-alert" style="background:#ecfdf3; color:#166534; border-color:#bbf7d0; margin-bottom:20px;">${message}</div>
    </c:if>

    <div class="table-card">
        <table class="member-table">
            <thead>
                <tr>
                    <th>번호</th>
                    <th>아이디</th>
                    <th>닉네임</th>
                    <th>이메일</th>
                    <th>전화번호</th>
                    <th>관리</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="m" items="${memberList}">
                    <tr>
                        <td>${m.id}</td>
                        <td>${m.username}</td>
                        <td>${m.nickname}</td>
                        <td>${m.email}</td>
                        <td>${m.phone}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/member/delete?username=${m.username}" class="table-delete-btn" onclick="return confirm('해당 회원을 탈퇴 처리하시겠습니까?');">탈퇴 처리</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</section>
