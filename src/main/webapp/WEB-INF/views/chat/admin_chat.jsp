<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
.admin-chat-page { display: flex; flex-direction: column; gap: 28px; }
.admin-chat-hero {
    background: linear-gradient(135deg, #0f172a, #312e81);
    color: #fff;
    border-radius: 24px;
    padding: 38px 42px;
    display: flex;
    align-items: center;
    gap: 18px;
}
.admin-chat-hero i { font-size: 44px; color: #a5b4fc; }
.admin-chat-hero h1 { margin: 0 0 8px; font-size: 30px; }
.admin-chat-hero p { margin: 0; color: rgba(255,255,255,0.76); }
.room-list-panel {
    background: #fff;
    border: 1px solid var(--line);
    border-radius: 24px;
    box-shadow: var(--shadow);
    overflow: hidden;
}
.room-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 22px 28px;
    border-bottom: 1px solid var(--line);
}
.room-list-header h2 { margin: 0; font-size: 20px; }
.room-count {
    padding: 5px 12px;
    border-radius: 999px;
    background: #e0e7ff;
    color: var(--primary-dark);
    font-size: 13px;
    font-weight: 700;
}
.room-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 18px 28px;
    border-bottom: 1px solid var(--line);
    transition: background 0.15s ease;
}
.room-item:last-child { border-bottom: none; }
.room-item:hover { background: #f8fafc; }
.room-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background: #e0e7ff;
    color: var(--primary-dark);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    font-weight: 800;
    flex-shrink: 0;
    text-transform: uppercase;
}
.room-info { flex: 1; min-width: 0; }
.room-name { font-size: 15px; font-weight: 700; margin-bottom: 4px; }
.room-preview {
    color: var(--sub);
    font-size: 13px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.room-meta { text-align: right; flex-shrink: 0; }
.room-time { color: var(--sub); font-size: 12px; }
.room-arrow { color: var(--sub); margin-top: 6px; }
.empty-rooms {
    padding: 64px 32px;
    text-align: center;
    color: var(--sub);
}
.empty-rooms i {
    display: block;
    font-size: 48px;
    color: #c7d2fe;
    margin-bottom: 14px;
}
</style>

<div class="admin-chat-page">
    <div class="admin-chat-hero">
        <i class="fa-solid fa-headset"></i>
        <div>
            <h1>실시간 고객 상담</h1>
            <p>고객별 상담방을 확인하고 1:1로 답변할 수 있습니다.</p>
        </div>
    </div>

    <div class="room-list-panel">
        <div class="room-list-header">
            <h2>상담 목록</h2>
            <span class="room-count">${roomIds.size()}개 대화</span>
        </div>

        <c:choose>
            <c:when test="${empty roomIds}">
                <div class="empty-rooms">
                    <i class="fa-solid fa-inbox"></i>
                    아직 상담 요청이 없습니다.
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="roomId" items="${roomIds}">
                    <c:set var="last" value="${lastMessageMap[roomId]}" />
                    <a class="room-item" href="${pageContext.request.contextPath}/chat/admin/room?roomId=${roomId}">
                        <div class="room-avatar">${roomId.charAt(0)}</div>
                        <div class="room-info">
                            <div class="room-name"><c:out value="${roomId}" /></div>
                            <div class="room-preview">
                                <c:choose>
                                    <c:when test="${not empty last}">
                                        <c:if test="${last.admin}">[관리자] </c:if>
                                        <c:if test="${not last.admin}">[<c:out value="${last.sender}" />] </c:if>
                                        <c:out value="${last.content}" />
                                    </c:when>
                                    <c:otherwise>아직 메시지가 없습니다.</c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="room-meta">
                            <div class="room-time">
                                <c:if test="${not empty last and not empty last.sentAt}">${last.sentAt.substring(11,16)}</c:if>
                            </div>
                            <div class="room-arrow"><i class="fa-solid fa-chevron-right"></i></div>
                        </div>
                    </a>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>