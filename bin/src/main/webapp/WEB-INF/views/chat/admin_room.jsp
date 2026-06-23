<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
.admin-room-page { display: flex; flex-direction: column; gap: 20px; }
.admin-room-back {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: var(--sub);
    font-size: 14px;
    font-weight: 600;
}
.admin-room-back:hover { color: var(--primary); }
.admin-room-card {
    background: #fff;
    border: 1px solid var(--line);
    border-radius: 24px;
    box-shadow: var(--shadow);
    overflow: hidden;
    display: flex;
    flex-direction: column;
    height: 680px;
    max-width: 900px;
}
.admin-room-header {
    background: var(--accent);
    color: #fff;
    padding: 20px 24px;
    display: flex;
    align-items: center;
    gap: 14px;
}
.admin-room-avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    background: rgba(255,255,255,0.14);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    font-weight: 800;
    text-transform: uppercase;
}
.admin-room-title { font-size: 18px; font-weight: 700; }
.admin-room-subtitle { color: rgba(255,255,255,0.72); font-size: 13px; margin-top: 3px; }
.admin-room-subtitle::before {
    content: '';
    display: inline-block;
    width: 7px;
    height: 7px;
    margin-right: 6px;
    border-radius: 50%;
    background: #22c55e;
}
.admin-messages {
    flex: 1;
    overflow-y: auto;
    padding: 22px;
    display: flex;
    flex-direction: column;
    gap: 14px;
    background: #f8fafc;
}
.msg-empty {
    margin: auto 0;
    text-align: center;
    color: var(--sub);
    font-size: 13px;
}
.msg-empty i {
    display: block;
    font-size: 40px;
    color: #c7d2fe;
    margin-bottom: 12px;
}
.msg-row { display: flex; gap: 10px; align-items: flex-end; }
.msg-row.mine { flex-direction: row-reverse; }
.msg-box { max-width: 380px; }
.msg-sender { font-size: 11px; font-weight: 700; color: var(--sub); margin-bottom: 4px; }
.msg-bubble {
    padding: 12px 16px;
    border-radius: 18px;
    font-size: 14px;
    line-height: 1.6;
    word-break: break-word;
}
.msg-row.theirs .msg-bubble {
    background: #fff;
    border: 1px solid var(--line);
    border-bottom-left-radius: 4px;
}
.msg-row.mine .msg-bubble {
    background: var(--primary);
    color: #fff;
    border-bottom-right-radius: 4px;
}
.msg-time { font-size: 11px; color: var(--sub); margin-top: 4px; }
.admin-input-area {
    display: flex;
    gap: 12px;
    padding: 16px 20px;
    border-top: 1px solid var(--line);
    background: #fff;
}
.admin-input-area input {
    flex: 1;
    border: 1px solid var(--line);
    border-radius: 14px;
    padding: 12px 16px;
    background: #f8fafc;
}
.admin-input-area input:focus {
    outline: none;
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(79,70,229,0.08);
}
.admin-send-btn {
    padding: 12px 22px;
    border: none;
    border-radius: 14px;
    background: var(--primary);
    color: #fff;
    font-weight: 700;
    cursor: pointer;
}
.admin-send-btn:hover { opacity: 0.9; }
</style>

<div class="admin-room-page">
    <a class="admin-room-back" href="${pageContext.request.contextPath}/chat/admin">
        <i class="fa-solid fa-arrow-left"></i> 상담 목록으로
    </a>

    <div class="admin-room-card">
        <div class="admin-room-header">
            <div class="admin-room-avatar">${roomId.charAt(0)}</div>
            <div>
                <div class="admin-room-title"><c:out value="${roomId}" /> 고객 상담</div>
                <div class="admin-room-subtitle">실시간 채팅 중</div>
            </div>
        </div>

        <div class="admin-messages" id="adminMessages">
            <c:choose>
                <c:when test="${empty messages}">
                    <div class="msg-empty" id="emptyRoomHint">
                        <i class="fa-solid fa-comment-dots"></i>
                        아직 메시지가 없습니다. 먼저 인사를 건네보세요!
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="msg" items="${messages}">
                        <div class="msg-row ${msg.admin ? 'mine' : 'theirs'}">
                            <div class="msg-box">
                                <c:if test="${not msg.admin}">
                                    <div class="msg-sender"><c:out value="${msg.sender}" /></div>
                                </c:if>
                                <div class="msg-bubble"><c:out value="${msg.content}" /></div>
                                <div class="msg-time"><c:out value="${msg.sentAt}" /></div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="admin-input-area">
            <input type="text" id="adminInput" placeholder="메시지를 입력하세요..." autocomplete="off">
            <button type="button" class="admin-send-btn" id="adminSend">전송</button>
        </div>
    </div>
</div>

<script>
(function() {
    const ROOM_ID = '${roomId}';
    const ctx = '${pageContext.request.contextPath}';
    const msgBox = document.getElementById('adminMessages');
    const input = document.getElementById('adminInput');
    const sendBtn = document.getElementById('adminSend');

    const scheme = location.protocol === 'https:' ? 'wss://' : 'ws://';
    let ws = null;

    function connectWebSocket() {
        ws = new WebSocket(scheme + location.host + ctx + '/ws/chat?roomId=' + encodeURIComponent(ROOM_ID));
        ws.onmessage = function(event) {
            const data = JSON.parse(event.data);
            appendMessage(data.sender, data.content, data.admin || data.isAdmin, data.sentAt);
        };
        ws.onclose = function() {
            setTimeout(connectWebSocket, 2000);
        };
    }

    function sendMessage() {
        const content = input.value.trim();
        if (!content) {
            return;
        }
        if (!ws || ws.readyState !== WebSocket.OPEN) {
            alert('채팅 서버에 연결 중입니다. 잠시 후 다시 시도해 주세요.');
            return;
        }
        ws.send(JSON.stringify({ content: content }));
        input.value = '';
    }

    function appendMessage(sender, content, isMine, sentAt) {
        const empty = document.getElementById('emptyRoomHint');
        if (empty) {
            empty.remove();
        }

        const row = document.createElement('div');
        row.className = 'msg-row ' + (isMine ? 'mine' : 'theirs');

        const box = document.createElement('div');
        box.className = 'msg-box';

        if (!isMine) {
            const senderEl = document.createElement('div');
            senderEl.className = 'msg-sender';
            senderEl.textContent = sender;
            box.appendChild(senderEl);
        }

        const bubble = document.createElement('div');
        bubble.className = 'msg-bubble';
        bubble.textContent = content;
        box.appendChild(bubble);

        if (sentAt) {
            const time = document.createElement('div');
            time.className = 'msg-time';
            time.textContent = sentAt;
            box.appendChild(time);
        }

        row.appendChild(box);
        msgBox.appendChild(row);
        msgBox.scrollTop = msgBox.scrollHeight;
    }

    sendBtn.addEventListener('click', sendMessage);
    input.addEventListener('keydown', function(event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            sendMessage();
        }
    });

    connectWebSocket();
    msgBox.scrollTop = msgBox.scrollHeight;
})();
</script>