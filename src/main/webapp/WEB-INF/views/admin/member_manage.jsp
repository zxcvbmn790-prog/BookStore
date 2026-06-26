<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="admin-member-page">
    <div class="admin-page-head">
        <div>
            <span class="section-badge">CUSTOMERS</span>
            <h2>고객관리</h2>
            <p>가입된 회원 정보를 확인하고 권한 변경 및 탈퇴 처리를 할 수 있습니다.</p>
        </div>
    </div>

    <form method="get" action="${pageContext.request.contextPath}/admin/members" class="bm-search-bar" style="margin-bottom:20px;">
        <input type="text" name="keyword" value="${keyword}" placeholder="아이디 / 닉네임 / 이메일 검색" class="bm-search-input">
        <button type="submit" class="bm-search-btn">검색</button>
        <c:if test="${not empty keyword}">
            <a href="${pageContext.request.contextPath}/admin/members" class="bm-search-reset">전체보기</a>
        </c:if>
    </form>

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
                    <th>권한</th>
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
                            <c:if test="${m.role != 'ROLE_ADMIN'}">
                                <select name="role" class="role-select" onchange="changeRole('${m.username}', this.value)">
                                    <option value="ROLE_USER" ${m.role == 'ROLE_USER' ? 'selected' : ''}>일반 회원</option>
                                    <option value="ROLE_TRAKING" ${m.role == 'ROLE_TRAKING' ? 'selected' : ''}>트래킹 회원</option>
                                    <option value="ROLE_ADMIN" ${m.role == 'ROLE_ADMIN' ? 'selected' : ''}>관리자</option>
                                </select>
                            </c:if>
                            <c:if test="${m.role == 'ROLE_ADMIN'}">
                                <span style="display:inline-block; padding:5px 12px; background:#fef2f2; color:#dc2626; border-radius:8px; font-size:13px; font-weight:700;">최고 관리자</span>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${m.role != 'ROLE_TRAKING' and m.role != 'ROLE_ADMIN'}">
                                <a href="${pageContext.request.contextPath}/admin/member/delete?username=${m.username}" class="table-delete-btn" onclick="return confirm('해당 회원을 탈퇴 처리하시겠습니까?');">탈퇴 처리</a>
                            </c:if>
                            <c:if test="${m.role == 'ROLE_TRAKING' or m.role == 'ROLE_ADMIN'}">
                                <span style="color: #999; font-size: 0.9em;">탈퇴 불가</span>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<script>
function changeRole(username, newRole) {
    let roleName = "";
    if(newRole === "ROLE_USER") roleName = "일반 회원";
    else if(newRole === "ROLE_TRAKING") roleName = "트래킹 회원";
    else if(newRole === "ROLE_ADMIN") roleName = "관리자";

    if (confirm(username + " 회원의 권한을 [" + roleName + "](으)로 변경하시겠습니까?")) {
        // Controller의 @GetMapping 경로와 일치시킵니다.
        location.href = "${pageContext.request.contextPath}/admin/member/updateRole?username=" + username + "&role=" + newRole;
    } else {
        // 취소 시 원상복구하기 위해 목록 페이지로 이동 처리
        location.href = "${pageContext.request.contextPath}/admin/members";
    }
}
</script>